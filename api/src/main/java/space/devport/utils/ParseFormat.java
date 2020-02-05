package space.devport.utils;

import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ParseFormat {
    // Instanced class the holds custom placeholders

    // Holds custom placeholders
    private HashMap<String, String> placeholderCache = new HashMap<>();

    public Set<String> getPlaceholders() {
        return placeholderCache.keySet();
    }

    public Collection<String> getValues() {
        return placeholderCache.values();
    }

    // Default to display when the placeholder is  not filled in.
    @Getter
    private String defaultValue = "null";

    public ParseFormat() {
    }

    public ParseFormat(String[] placeholders, String[] values) {
        for (int i = 0; i < placeholders.length; i++) {
            placeholderCache.put(placeholders[i], values[i]);
        }
    }

    public ParseFormat(String[] placeholders) {
        for (String placeholder : placeholders)
            placeholderCache.put(placeholder, defaultValue);
    }

    // Add a single placeholder to the cache
    public ParseFormat addPlaceholder(String placeholder) {
        placeholderCache.put(placeholder, defaultValue);
        return this;
    }

    public ParseFormat addPlaceholder(String placeholder, String value) {
        placeholderCache.put(placeholder, value);
        return this;
    }

    // Add multiple placeholders to the cache
    // If it contains the placeholder already, fill with default values.
    public ParseFormat addPlaceholders(String[] placeholders) {

        for (String placeholder : placeholders)
            addPlaceholder(placeholder);

        return this;
    }

    // Uses passed arguments to fill in out cache
    // Is used from first to last.
    public ParseFormat fill(String[] arguments) {
        int n = arguments.length - 1;

        for (String placeholder : placeholderCache.keySet()) {
            placeholderCache.put(placeholder, arguments[n]);

            n--;

            // Escape when we reach the end of supply line.
            if (n == -1)
                break;
        }

        return this;
    }

    // Parse a string with current placeholders
    public String parse(String str) {

        for (String placeholder : placeholderCache.keySet()) {
            str = str.replaceAll(placeholder, placeholderCache.get(placeholder));
        }

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
}
