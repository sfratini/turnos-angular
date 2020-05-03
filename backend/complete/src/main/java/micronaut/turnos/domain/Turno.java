package micronaut.turnos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "turno")
public class Turno {

    public Turno() {}

    public Turno(@NotNull Long hc, @NotNull String name, @NotNull String lastname, @NotNull Date date, @NotNull String time, @NotNull String place) {
        this.hc = hc;
        this.name = name;
        this.lastname = lastname;
        this.date = date;
        this.time = time;
        this.place = place;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "hc", nullable = false, unique = false)
    private Long hc;

    @NotNull
    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @NotNull
    @Column(name = "lastname", nullable = false, unique = false)
    private String lastname;

    @NotNull
    @Column(name = "date", nullable = false, unique = false)
    private Date date;

    @NotNull
    @Column(name = "time", nullable = false, unique = false)
    private String time;

    @NotNull
    @Column(name = "place", nullable = false, unique = false)
    private String place;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public Long getHc() {
        return hc;
    }

    public void setHc(Long hc) {
        this.hc = hc;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    @Override
    public String toString() {
        return "Turno{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
