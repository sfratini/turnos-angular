package micronaut.turnos;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Introspected // <1>
public class TurnoSaveCommand {

    @NotBlank
    private Long hc;
    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    @NotBlank
    private Date date;
    @NotBlank
    private String time;
    @NotBlank
    private String place;

    public TurnoSaveCommand() {
    }

    public TurnoSaveCommand(Long hc, String name, String lastname, Date date, String time, String place) {
        this.hc = hc;
        this.name = name;
        this.lastname = lastname;
        this.date = date;
        this.time = time;
        this.place = place;
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

}
