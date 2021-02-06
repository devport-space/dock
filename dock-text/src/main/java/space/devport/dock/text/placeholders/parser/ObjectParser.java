package space.devport.dock.text.placeholders.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectParser<T> {

    @Nullable
    Object extractValue(@NotNull T object);
}