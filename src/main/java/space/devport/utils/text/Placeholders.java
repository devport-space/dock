package space.devport.utils.text;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.struct.Context;
import space.devport.utils.text.message.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
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
     * @param placeholders Parse format to copy placeholders from
     * @return ParseFormat object
     */
    public Placeholders copy(@NotNull Placeholders placeholders) {
        placeholders.getPlaceholderCache().forEach((key, value) -> placeholderCache.put(key, value));
        this.context.add(placeholders.getContext());
        placeholders.getDynamicPlaceholders().forEach(this.dynamicPlaceholders::put);
        this.parsers.addAll(placeholders.getParsers());
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
            string = string.replaceAll("(?i)" + placeholder, placeholderCache.get(placeholder));
        return string;
    }

    public Message parse(@NotNull Message message) {
        message.getPlaceholders().copy(this);
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

    private final Context context = new Context();

    @Getter
    private final Map<String, Function<Object, String>> dynamicPlaceholders = new HashMap<>();

    public Context getContext() {
        return this.context;
    }

    public Placeholders setContext(Context context) {
        this.context.set(context);
        return this;
    }

    @Getter
    private final Set<BiFunction<Object, String, String>> parsers = new HashSet<>();

    public String parseExternal(String text) {
        for (BiFunction<Object, String, String> parser : parsers) {
            String value = null;
            for (Object context : this.context.getValues()) {
                String ctxValue = parser.apply(context, text);
                if (ctxValue != null)
                    value = ctxValue;
            }
            if (value == null) continue;
            text = value;
        }
        return text;
    }

    public <T> Placeholders addParser(BiFunction<T, String, String> parser) {
        this.parsers.add((o, str) -> {
            if (o != null)
                return parser.apply((T) o, str);
            return str;
        });
        return this;
    }

    public <T> Placeholders addDynamicPlaceholder(String placeholder, DynamicParser<T> parser) {
        this.dynamicPlaceholders.put(placeholder, o -> {
            if (o != null)
                return parser.apply((T) o);
            return null;
        });
        return this;
    }

    public String parseDynamic(String text) {
        for (Map.Entry<String, Function<Object, String>> entry : dynamicPlaceholders.entrySet()) {
            if (!text.toLowerCase().contains(entry.getKey().toLowerCase())) {
                continue;
            }

            String value = null;
            for (Object context : this.context.getValues()) {
                String ctxValue = entry.getValue().apply(context);
                if (ctxValue != null)
                    value = ctxValue;
            }

            if (value == null) continue;

            text = text.replaceAll("(?i)" + entry.getKey(), value);
        }
        return text;
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