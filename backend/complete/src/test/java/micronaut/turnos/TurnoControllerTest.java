package micronaut.turnos;

import micronaut.turnos.domain.Turno;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest // <1>
public class TurnoControllerTest {

    @Inject
    @Client("/")
    HttpClient client; // <2>

    @Test
    public void supplyAnInvalidOrderTriggersValidationFailure() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/turnos/list?order=foo"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testFindNonExistingTurnoReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/turnos/99"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testTurnoCrudOperations() {

        List<Long> turnosIds = new ArrayList<>();

        HttpRequest request = HttpRequest.POST("/turnos", new TurnoSaveCommand(Long.valueOf(123), "Nombre", "Apellido", new Date(), "Time", "Place")); // <3>
        HttpResponse response = client.toBlocking().exchange(request);
        turnosIds.add(entityId(response));

        assertEquals(HttpStatus.CREATED, response.getStatus());

        request = HttpRequest.POST("/turnos", new TurnoSaveCommand(Long.valueOf(456), "Nombre2", "Apellido2", new Date(), "Time2", "Place2")); // <3>
        response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());

        Long id = entityId(response);
        turnosIds.add(id);
        request = HttpRequest.GET("/turnos/" + id);

        Turno turno = client.toBlocking().retrieve(request, Turno.class); // <4>

        assertEquals("Nombre2", turno.getName());
        assertEquals(456, turno.getHc());

        request = HttpRequest.GET("/turnos/list");
        List<Turno> turnos = client.toBlocking().retrieve(request, Argument.of(List.class, Turno.class));

        assertEquals(2, turnos.size());

        request = HttpRequest.GET("/turnos/list?max=1");
        turnos = client.toBlocking().retrieve(request, Argument.of(List.class, Turno.class));

        assertEquals(1, turnos.size());
        assertEquals("Nombre", turnos.get(0).getName());
        assertEquals(123, turnos.get(0).getHc());

        request = HttpRequest.GET("/turnos/list?max=1&order=desc&sort=name");
        turnos = client.toBlocking().retrieve(request, Argument.of(List.class, Turno.class));

        assertEquals(1, turnos.size());
        assertEquals("Nombre2", turnos.get(0).getName());
        assertEquals(456, turnos.get(0).getHc());

        request = HttpRequest.GET("/turnos/list?max=1&offset=10");
        turnos = client.toBlocking().retrieve(request, Argument.of(List.class, Turno.class));

        assertEquals(0, turnos.size());

        // cleanup:
        for (Long turnoId : turnosIds) {
            request = HttpRequest.DELETE("/turnos/" + turnoId);
            response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }
    }

    protected Long entityId(HttpResponse response) {
        String path = "/turnos/";
        String value = response.header(HttpHeaders.LOCATION);
        if (value == null) {
            return null;
        }
        int index = value.indexOf(path);
        if (index != -1) {
            return Long.valueOf(value.substring(index + path.length()));
        }
        return null;
    }
}
