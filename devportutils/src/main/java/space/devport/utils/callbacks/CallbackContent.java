package space.devport.utils.callbacks;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class CallbackContent {

    private final String input;
    @Getter
    private final Throwable throwable;
    @Getter
    private final String variable;

    private CallbackContent(Throwable throwable, String variable, String input) {
        this.input = input;
        this.throwable = throwable;
        this.variable = variable;
    }

    @NotNull
    public static CallbackContent createNew(Throwable throwable, String variable, String input) {
        return new CallbackContent(throwable, variable, input);
    }

    @NotNull
    public static CallbackContent createNew(Throwable throwable, String variable) {
        return new CallbackContent(throwable, variable, null);
    }

    @NotNull
    public String getInput() {
        return input == null ? "null" : input;
    }

    public void printStackTrace() {
        if (throwable != null)
            throwable.printStackTrace();
    }
}
