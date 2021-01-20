package space.devport.utils.callbacks;

import org.jetbrains.annotations.NotNull;

public interface ExceptionCallback {

    // Use this when you don't want to handle exception callback and want no exception "thrown".
    ExceptionCallback IGNORE = e -> {
    };

    void call(@NotNull CallbackContent content);
}
