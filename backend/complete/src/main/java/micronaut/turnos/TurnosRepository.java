package micronaut.turnos;

import micronaut.turnos.domain.Turno;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Date;

public interface TurnosRepository {

    Optional<Turno> findById(@NotNull Long id);

    Turno save(@NotBlank Long hc, @NotBlank String name, @NotBlank String lastname, @NotBlank Date date, @NotBlank String time, @NotBlank String place);

    void deleteById(@NotNull Long id);

    void deleteAll();

    List<Turno> findAll(@NotNull SortingAndOrderArguments args);

    List<Turno> search(@NotNull SearchArguments args);

}
