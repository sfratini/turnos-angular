package micronaut.turnos;

import micronaut.turnos.domain.Turno;
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.spring.tx.annotation.Transactional;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.text.MessageFormat;

@Singleton // <1>
public class TurnosRepositoryImpl implements TurnosRepository {

    @PersistenceContext
    private EntityManager entityManager;  // <2>
    private final ApplicationConfiguration applicationConfiguration;

    public TurnosRepositoryImpl(@CurrentSession EntityManager entityManager,
                               ApplicationConfiguration applicationConfiguration) { // <2>
        this.entityManager = entityManager;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    @Transactional(readOnly = true) // <3>
    public Optional<Turno> findById(@NotNull Long id) {
        return Optional.ofNullable(entityManager.find(Turno.class, id));
    }

    @Override
    @Transactional // <4>
    public Turno save(@NotBlank Long hc, @NotBlank String name, @NotBlank String lastname, @NotBlank Date date, @NotBlank String time, @NotBlank String place) {
        Turno turno = new Turno(hc, name, lastname, date, time, place);
        entityManager.persist(turno);
        return turno;
    }

    @Override
    @Transactional
    public void deleteById(@NotNull Long id) {
        findById(id).ifPresent(turno -> entityManager.remove(turno));
    }

    @Override
    @Transactional
    public void deleteAll() {
        try {
            String qlString = "DELETE * FROM Turno as g";
            TypedQuery<Turno> query = entityManager.createQuery(qlString, Turno.class);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name", "hc", "lastname", "date", "time", "place");

    @Transactional(readOnly = true)
    public List<Turno> findAll(@NotNull SortingAndOrderArguments args) {
        String qlString = "SELECT g FROM Turno as g";
        if (args.getOrder().isPresent() && args.getSort().isPresent() && VALID_PROPERTY_NAMES.contains(args.getSort().get())) {
                qlString += " ORDER BY g." + args.getSort().get() + " " + args.getOrder().get().toLowerCase();
        }
        TypedQuery<Turno> query = entityManager.createQuery(qlString, Turno.class);
        query.setMaxResults(args.getMax().orElseGet(applicationConfiguration::getMax));
        args.getOffset().ifPresent(query::setFirstResult);

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<Turno> search(@NotNull SearchArguments args) {

        String qlString = "SELECT g FROM Turno as g";
        if (args.getText().isPresent()){
            qlString += MessageFormat.format(" where lower(hc) like ''%{0}%'' or lower(name) like ''%{0}%'' or lower(lastname) like ''%{0}%'' ", args.getText().get().toLowerCase());
        }
        if (args.getOrder().isPresent() && args.getSort().isPresent() && VALID_PROPERTY_NAMES.contains(args.getSort().get())) {
                qlString += " ORDER BY g." + args.getSort().get() + " " + args.getOrder().get().toLowerCase();
        }
        TypedQuery<Turno> query = entityManager.createQuery(qlString, Turno.class);
        query.setMaxResults(args.getMax().orElseGet(applicationConfiguration::getMax));
        args.getOffset().ifPresent(query::setFirstResult);

        return query.getResultList();
    }

}
