package space.devport.utils.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DynamicParser<T> {
    @Nullable String extractValue(@NotNull T object);
}