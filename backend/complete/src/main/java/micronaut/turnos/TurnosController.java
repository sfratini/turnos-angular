package micronaut.turnos;

import micronaut.turnos.domain.Turno;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller("/turnos") 
public class TurnosController {

    protected final TurnosRepository turnosRepository;

    public TurnosController(TurnosRepository turnosRepository) { 
        this.turnosRepository = turnosRepository;
    }

    @Get("/deleteAll") // For cleaning up during testing
    public HttpResponse deleteAll() {
        turnosRepository.deleteAll();
        return HttpResponse.noContent();
    }

    @Get("/{id}") 
    public Turno show(Long id) {
        return turnosRepository
                .findById(id)
                .orElse(null); 
    }

    @Get(value = "/list{?args*}") 
    public List<Turno> list(@Valid SortingAndOrderArguments args) {
        return turnosRepository.findAll(args);
    }

    @Get(value = "/search{?args*}") 
    public List<Turno> search(@Valid SearchArguments args) {
        return turnosRepository.search(args);
    }

    @Post("/") 
    public HttpResponse<Turno> save(@Body @Valid TurnoSaveCommand cmd) {
        Turno turno = turnosRepository.save(cmd.getHc(), cmd.getName(), cmd.getLastname(), cmd.getDate(), cmd.getTime(), cmd.getPlace());

        return HttpResponse
                .created(turno)
                .headers(headers -> headers.location(location(turno.getId())));
    }

    @Delete("/{id}") 
    public HttpResponse delete(Long id) {
        turnosRepository.deleteById(id);
        return HttpResponse.noContent();
    }

    protected URI location(Long id) {
        return URI.create("/turnos/" + id);
    }

    protected URI location(Turno turno) {
        return location(turno.getId());
    }
}
