package space.devport.utils.callbacks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReasonedCallback {

    void call(@NotNull CallbackReason reason, @Nullable String param);

    enum CallbackReason {
        NULL,
        SPLIT_NOT_ENOUGH
    }
}
