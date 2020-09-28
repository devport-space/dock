package space.devport.utils.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlaceholderParser<T> {
    @Nullable String parse(@NotNull String input, @NotNull T object);
}