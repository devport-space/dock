package space.devport.utils.text;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.struct.Context;
import space.devport.utils.text.message.Message;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class that holds placeholders.
 *
 * @author Devport Team
 */
@NoArgsConstructor
public class Placeholders implements Cloneable {

    @Getter
    @Setter
    private Map<String, String> placeholderCache = new LinkedHashMap<>();

    /**
     * Copy constructor.
     *
     * @param placeholders Parse format to copy
     */
    public Placeholders(@NotNull Placeholders placeholders) {
        this.copy(placeholders);
    }

    /**
     * Constructor with placeholders and values.
     *
     * @param placeholders Placeholder array
     * @param values       Value array
     */
    @Deprecated
    public Placeholders(@NotNull String[] placeholders, @NotNull Object[] values) {
        for (int i = 0; i < placeholders.length; i++)
            placeholderCache.put(placeholders[i], values[i].toString());
    }

    /**
     * Copy placeholders from another instance.
     *
     * @param placeholders {@link Placeholders} to copy from.
     * @return If input Placeholders are {@code null}, nothing will be copied and this instance returned.
     */
    @NotNull
    public Placeholders copy(@Nullable Placeholders placeholders) {
        if (placeholders == null)
            return this;

        // Copy dynamic parsers.
        placeholders.getDynamicPlaceholders().forEach(this.dynamicPlaceholders::put);
        this.parsers.addAll(placeholders.getParsers());

        // Copy cache.
        placeholders.getPlaceholderCache().forEach((key, value) -> placeholderCache.put(key, value));
        this.context.add(placeholders.getContext());
        return this;
    }

    /**
     * Parse placeholders in a {@link String}.
     *
     * @param string String to parse.
     * @return Parsed {@link String}. Null if provided String is null, empty if it's empty.
     */
    @Contract("null -> null")
    public String parse(@Nullable String string) {

        if (Strings.isNullOrEmpty(string))
            return string;

        // Parse cache.
        for (String placeholder : placeholderCache.keySet())
            string = string.replaceAll("(?i)" + placeholder, placeholderCache.get(placeholder));

        // Parse dynamic.
        string = parseDynamic(string);
        string = parseExternal(string);
        return string;
    }

    @Deprecated
    public Message parse(@NotNull Message message) {
        message.getPlaceholders().copy(this);
        return message;
    }

    @NotNull
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

    public Placeholders addContext(Context context) {
        this.context.add(context);
        return this;
    }

    public Placeholders addContext(Object object) {
        this.context.add(object);
        return this;
    }

    public Placeholders addContext(Object... objects) {
        Arrays.asList(objects).forEach(this.context::add);
        return this;
    }

    public Placeholders clearContext() {
        this.context.clear();
        return this;
    }

    @Getter
    private final Set<BiFunction<Object, String, String>> parsers = new HashSet<>();

    @NotNull
    public String parseExternal(String text) {
        for (BiFunction<Object, String, String> parser : parsers) {
            for (Object context : this.context.getValues()) {
                String result = parser.apply(context, text);
                if (result != null)
                    text = result;
            }
        }
        return text;
    }

    public <T> Placeholders addParser(PlaceholderParser<T> parser, Class<T> type) {
        this.parsers.add((o, str) -> {
            if (o != null && !Strings.isNullOrEmpty(str) && type.isAssignableFrom(o.getClass())) {
                T t = type.cast(o);
                str = parser.parse(str, t);
            }
            return str;
        });
        return this;
    }

    public <T> Placeholders addDynamicPlaceholder(String placeholder, DynamicParser<T> parser, Class<T> type) {
        this.dynamicPlaceholders.put(placeholder, o -> {
            if (o != null && type.isAssignableFrom(o.getClass())) {
                T t = type.cast(o);
                return parser.extractValue(t);
            }
            return null;
        });
        return this;
    }

    @NotNull
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

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Placeholders clone() {
        return new Placeholders(this);
    }

    @Override
    public String toString() {
        return placeholderCache.toString();
    }
}