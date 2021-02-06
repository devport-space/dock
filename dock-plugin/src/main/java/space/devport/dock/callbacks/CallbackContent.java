package space.devport.dock.callbacks;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.callbacks.exception.CallbackException;
import space.devport.dock.text.StringUtil;

public class CallbackContent {

    private final Object input;
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

    public void callOrThrow(@Nullable ExceptionCallback callback) {
        if (callback == null)
            try {
                throw new CallbackException(this);
            } catch (CallbackException e) {
                e.printStackTrace();
            }
        else callback.call(this);
    }

    public Object getInputRaw() {
        return input;
    }

    public String getVariableRaw() {
        return variable;
    }

    @NotNull
    public String getInput() {
        return StringUtil.valueOfEmpty(input);
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
