package space.devport.utils.messageutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Class that holds placeholders.
 *
 * @author Devport Team
 */
@NoArgsConstructor
public class ParseFormat {

    // Holds custom placeholders and their current values
    @Getter
    @Setter
    private LinkedHashMap<String, String> placeholderCache = new LinkedHashMap<>();

    // Default to display when the placeholder is not filled in.
    @Getter
    private String defaultValue = "null";

    public ParseFormat(ParseFormat format) {
        this.copyPlaceholders(format);
        this.defaultValue = format.getDefaultValue();
    }

    // Class constructor with placeholders.
    public ParseFormat(String[] placeholders) {
        for (String placeholder : placeholders)
            placeholderCache.put(placeholder, defaultValue);
    }

    // Class constructor with placeholders & values.
    public ParseFormat(String[] placeholders, String[] values) {
        for (int i = 0; i < placeholders.length; i++)
            placeholderCache.put(placeholders[i], values[i]);
    }

    // Add a single placeholder with no value.
    public ParseFormat addPlaceholder(String placeholder) {
        placeholderCache.put(placeholder, defaultValue);
        return this;
    }

    // Add a single placeholder with value.
    public ParseFormat addPlaceholder(String placeholder, String value) {
        placeholderCache.put(placeholder, value);
        return this;
    }

    // Add multiple placeholders to the cache.
    public ParseFormat addPlaceholders(String[] placeholders) {
        for (String placeholder : placeholders)
            addPlaceholder(placeholder);
        return this;
    }

    public ParseFormat setPlaceholders(String[] placeholders) {
        clearPlaceholders();
        addPlaceholders(placeholders);
        return this;
    }

    // Copy placeholders from a format
    public ParseFormat copyPlaceholders(ParseFormat format) {
        format.getPlaceholderCache().forEach((key, value) -> placeholderCache.put(key, value));
        return this;
    }

    // Uses passed arguments to fill values for placeholders first to last.
    public ParseFormat fill(String[] arguments) {
        int n = 0;

        for (String placeholder : placeholderCache.keySet()) {
            placeholderCache.put(placeholder, arguments[n]);

            n++;

            if (n == arguments.length)
                break;
        }

        return this;
    }

    // Parse a string with current placeholders
    public String parse(String str) {
        for (String placeholder : placeholderCache.keySet())
            str = str.replace(placeholder, placeholderCache.get(placeholder));
        return str;
    }

    // Fill in a single placeholder with a value
    public ParseFormat fill(String placeholder, String value) {
        placeholderCache.put(placeholder, value);
        return this;
    }

    // Set the default placeholder value.
    public ParseFormat setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ParseFormat clearPlaceholders() {
        placeholderCache.clear();
        return this;
    }

    public Set<String> getPlaceholders() {
        return placeholderCache.keySet();
    }

    public Collection<String> getValues() {
        return placeholderCache.values();
    }
}