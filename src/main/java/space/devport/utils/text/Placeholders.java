package space.devport.utils.text;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.text.message.Message;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that holds placeholders.
 *
 * @author Devport Team
 */
@NoArgsConstructor
public class Placeholders {

    @Getter
    @Setter
    private LinkedHashMap<String, String> placeholderCache = new LinkedHashMap<>();

    /**
     * Copy constructor.
     *
     * @param format Parse format to copy
     */
    public Placeholders(@NotNull Placeholders format) {
        this.copy(format);
    }

    /**
     * Constructor with placeholders and values.
     *
     * @param placeholders Placeholder array
     * @param values       Value array
     */
    public Placeholders(@NotNull String[] placeholders, @NotNull Object[] values) {
        for (int i = 0; i < placeholders.length; i++)
            placeholderCache.put(placeholders[i], values[i].toString());
    }

    /**
     * Copy placeholders and values from a format.
     *
     * @param format Parse format to copy placeholders from
     * @return ParseFormat object
     */
    public Placeholders copy(@NotNull Placeholders format) {
        format.getPlaceholderCache().forEach((key, value) -> placeholderCache.put(key, value));
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

    public Message parse(@NotNull Message message) {
        for (String placeholder : placeholderCache.keySet())
            message.replace(placeholder, placeholderCache.get(placeholder));
        return message;
    }

    public List<String> parse(@NotNull List<String> list) {
        return list.stream()
                .map(this::parse)
                .collect(Collectors.toList());
    }

    /**
     * Fill a single placeholder value.
     *
     * @param placeholder Placeholder to replace
     * @param value       Value to fill in
     * @return ParseFormat object
     */
    public Placeholders add(@NotNull String placeholder, @Nullable Object value) {
        if (value != null)
            placeholderCache.put(placeholder, value.toString());
        return this;
    }

    /**
     * Clear the placeholder cache.
     *
     * @return ParseFormat object
     */
    public Placeholders clear() {
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
}