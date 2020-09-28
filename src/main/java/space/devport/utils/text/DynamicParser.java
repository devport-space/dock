package space.devport.utils.text;

import java.util.function.Function;

public interface DynamicParser<T> extends Function<T, String> {
}