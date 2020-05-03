package micronaut.turnos;

import javax.annotation.Nullable;
import java.util.Optional;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class SearchArguments extends SortingAndOrderArguments {

    @Nullable
    private String text;

    public SearchArguments() {

    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    public Optional<String> getText() {
        return Optional.ofNullable(text);
    }

}
