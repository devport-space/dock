package space.devport.utils.messageutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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
    // In order
    @Getter
    @Setter
    private LinkedHashMap<String, String> placeholderCache = new LinkedHashMap<>();

    // Default to display when the placeholder is not filled in.
    @Getter
    private String defaultValue = "null";

    /**
     * Copy constructor.
     *
     * @param format Parse format to copy
     */
    public ParseFormat(@NotNull ParseFormat format) {
        this.copyPlaceholders(format);
        this.defaultValue = format.getDefaultValue();
    }

    /**
     * Constructor with placeholder array.
     *
     * @param placeholders Placeholder array
     */
    public ParseFormat(@NotNull String... placeholders) {
        for (String placeholder : placeholders)
            placeholderCache.put(placeholder, defaultValue);
    }

    /**
     * Constructor with placeholders and values.
     *
     * @param placeholders Placeholder array
     * @param values       Value array
     */
    public ParseFormat(@NotNull String[] placeholders, @NotNull String[] values) {
        for (int i = 0; i < placeholders.length; i++)
            placeholderCache.put(placeholders[i], values[i]);
    }

    /**
     * Adds a single placeholder to the cache.
     * Fills it with default value.
     *
     * @param placeholder String placeholder
     * @return ParseFormat object
     */
    public ParseFormat addPlaceholder(@NotNull String placeholder) {
        placeholderCache.put(placeholder, defaultValue);
        return this;
    }

    /**
     * Add multiple placeholders to the cache.
     *
     * @param placeholders Placeholders to add in an array
     * @return ParseFormat object
     */
    public ParseFormat addPlaceholders(@NotNull String... placeholders) {
        for (String placeholder : placeholders)
            addPlaceholder(placeholder);
        return this;
    }

    /**
     * Set placeholder cache.
     *
     * @param placeholders Placeholders to be set
     * @return ParseFormat object
     */
    public ParseFormat setPlaceholders(@NotNull String... placeholders) {
        clearPlaceholders();
        addPlaceholders(placeholders);
        return this;
    }

    /**
     * Copy placeholders and values from a format.
     *
     * @param format Parse format to copy placeholders from
     * @return ParseFormat object
     */
    public ParseFormat copyPlaceholders(@NotNull ParseFormat format) {
        format.getPlaceholderCache().forEach((key, value) -> placeholderCache.put(key, value));
        return this;
    }

    /**
     * Fill placeholders with values, in order.
     *
     * @param arguments Values to fill in
     * @return ParseFormat object
     */
    public ParseFormat fill(@NotNull String... arguments) {
        int n = 0;

        for (String placeholder : placeholderCache.keySet()) {
            placeholderCache.put(placeholder, arguments[n]);

            n++;
            if (n == arguments.length)
                break;
        }

        return this;
    }

    /**
     * Parse a string using this format.
     *
     * @param string String to parse
     * @return Parsed string
     */
    public String parse(@NotNull String string) {
        for (String placeholder : placeholderCache.keySet())
            string = string.replace(placeholder, placeholderCache.get(placeholder));
        return string;
    }

    /**
     * Fill a single placeholder value.
     *
     * @param placeholder Placeholder to replace
     * @param value       Value to fill in
     * @return ParseFormat object
     */
    public ParseFormat fill(@NotNull String placeholder, @NotNull String value) {
        placeholderCache.put(placeholder, value);
        return this;
    }

    /**
     * Set default placeholder value.
     *
     * @param defaultValue Default value
     * @return ParseFormat object
     */
    public ParseFormat setDefaultValue(@NotNull String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Clear the placeholder cache.
     *
     * @return ParseFormat object
     */
    public ParseFormat clearPlaceholders() {
        placeholderCache.clear();
        return this;
    }

    /**
     * Get keys of placeholder cache.
     *
     * @return Set of Strings
     */
    public Set<String> getPlaceholders() {
        return placeholderCache.keySet();
    }

    /**
     * Get values of placeholder cache.
     *
     * @return Collection of Strings
     */
    public Collection<String> getValues() {
        return placeholderCache.values();
    }
}