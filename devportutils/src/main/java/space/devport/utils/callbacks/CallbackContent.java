package space.devport.utils.callbacks;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class CallbackContent {

    private final String input;
    @Getter
    private final Throwable throwable;
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

    public static CallbackContent createNew(Throwable throwable) {
        return new CallbackContent(throwable, null, null);
    }

    @NotNull
    public String getInput() {
        return String.valueOf(input);
    }

    @NotNull
    public String getVariable() {
        return String.valueOf(variable);
    }

    public void printStackTrace() {
        if (throwable != null)
            throwable.printStackTrace();
    }
}
