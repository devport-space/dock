package space.devport.utils.text;

import java.util.function.Consumer;

/**
 * A Placeholder is an object that holds a Placeholders instance and uses the chain structure
 * to allow it's modification.
 */
public interface Placeholder {

    Placeholder applyPlaceholders(Consumer<Placeholders> modifier);
}
