package space.devport.utils.commands.struct;

import org.jetbrains.annotations.Nullable;

public interface ArgumentParser<T> {

    @Nullable
    T parse(@Nullable String input);
}
