package space.devport.utils.callbacks;

import org.jetbrains.annotations.NotNull;

public interface ExceptionCallback {

    void call(@NotNull CallbackContent content);
}
