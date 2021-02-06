package space.devport.dock.text.placeholders.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GeneralParser<T> {

    @Nullable
    String parse(@NotNull String input, @NotNull T object);
}