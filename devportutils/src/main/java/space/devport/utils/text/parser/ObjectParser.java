package space.devport.utils.text.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectParser<T> {

    @Nullable
    Object extractValue(@NotNull T object);
}