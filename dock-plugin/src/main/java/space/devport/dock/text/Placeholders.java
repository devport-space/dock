package space.devport.dock.text;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.struct.Context;
import space.devport.dock.text.message.Message;
import space.devport.dock.text.parser.ObjectParser;
import space.devport.dock.text.parser.GeneralParser;

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
    private String placeholderSign = "%";

    @Getter
    @Setter
    private Map<String, String> placeholderCache = new LinkedHashMap<>();

    // Dynamic parsers reference an exact %placeholder%.
    @Getter
    private final Map<String, Function<Object, String>> objectParsers = new HashMap<>();

    // General parsers don't care and do whatever they want with the given string and context object.
    @Getter
    private final Set<BiFunction<Object, String, String>> generalParsers = new HashSet<>();

    private final Context context = new Context();

    private Placeholders(@NotNull Placeholders placeholders) {
        this.copy(placeholders);
    }

    public static Placeholders of(Placeholders placeholders) {
        return new Placeholders(placeholders);
    }

    /**
     * Fill a single placeholder value.
     *
     * @param placeholder Placeholder to replace
     * @param value       Value to fill in
     * @return ParseFormat object
     */
    @NotNull
    public Placeholders add(@NotNull String placeholder, @Nullable Object value) {
        Objects.requireNonNull(placeholder, "Placeholder cannot be null.");

        placeholderCache.put(ensureSigns(placeholder), String.valueOf(value));
        return this;
    }

    private String ensureSigns(String str) {
        return str.startsWith(placeholderSign) && str.endsWith(placeholderSign) ? str : String.format("%s%s%s", placeholderSign, str, placeholderSign);
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

        // Run additional parsers as the don't need to have %x%.
        if (!generalParsers.isEmpty())
            string = parseExternal(string);

        if (!string.contains(placeholderSign))
            return string;

        // Parse cache.
        for (String placeholder : placeholderCache.keySet())
            string = string.replaceAll("(?i)" + placeholder, placeholderCache.get(placeholder));

        // Parse dynamic.
        if (!objectParsers.isEmpty())
            string = parseDynamic(string);

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

    @NotNull
    public String parseExternal(String text) {
        for (BiFunction<Object, String, String> parser : generalParsers) {
            for (Object context : this.context.getValues()) {
                String result = parser.apply(context, text);
                if (result != null)
                    text = result;
            }
        }
        return text;
    }

    public <T> Placeholders addParser(@NotNull GeneralParser<T> parser, @NotNull Class<T> clazz) {
        Objects.requireNonNull(parser, "Parser cannot be null.");
        Objects.requireNonNull(clazz, "Class cannot be null.");

        this.generalParsers.add((o, str) -> {
            if (o != null && !Strings.isNullOrEmpty(str) && clazz.isAssignableFrom(o.getClass())) {
                T t = clazz.cast(o);
                str = parser.parse(str, t);
            }
            return str;
        });
        return this;
    }

    public <T> Placeholders addDynamicPlaceholder(@NotNull String placeholder, @NotNull ObjectParser<T> parser, @NotNull Class<T> clazz) {
        Objects.requireNonNull(placeholder, "Placeholder cannot be null.");
        Objects.requireNonNull(parser, "Parser cannot be null.");
        Objects.requireNonNull(clazz, "Class cannot be null.");

        this.objectParsers.put(placeholder, o -> {
            if (o != null && clazz.isAssignableFrom(o.getClass())) {
                T t = clazz.cast(o);
                return String.valueOf(parser.extractValue(t));
            }
            return null;
        });
        return this;
    }

    @Contract("null -> null;!null -> !null")
    public String parseDynamic(@Nullable String text) {
        if (!Strings.isNullOrEmpty(text)) {

            String txt = text.toLowerCase();
            for (Map.Entry<String, Function<Object, String>> entry : objectParsers.entrySet()) {

                // No more placeholders to parse.
                if (!txt.contains(placeholderSign))
                    break;

                if (!txt.contains(entry.getKey().toLowerCase()))
                    continue;

                String value = null;
                for (Object context : this.context.getValues()) {
                    String ctxValue = entry.getValue().apply(context);
                    if (ctxValue != null)
                        value = ctxValue;
                }

                if (value == null) continue;

                text = text.replaceAll("(?i)" + entry.getKey(), value);
            }
        }
        return text;
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
        placeholders.getObjectParsers().forEach(this.objectParsers::put);
        this.generalParsers.addAll(placeholders.getGeneralParsers());

        // Copy cache.
        placeholders.getPlaceholderCache().forEach((key, value) -> placeholderCache.put(key, value));
        this.context.add(placeholders.getContext());
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

    @NotNull
    public Context getContext() {
        return this.context;
    }

    @NotNull
    public Placeholders setContext(Context context) {
        this.context.set(context);
        return this;
    }

    @NotNull
    public Placeholders addContext(Context context) {
        this.context.add(context);
        return this;
    }

    @NotNull
    public Placeholders addContext(Object object) {
        this.context.add(object);
        return this;
    }

    @NotNull
    public Placeholders addContext(Object... objects) {
        Arrays.asList(objects).forEach(this.context::add);
        return this;
    }

    @NotNull
    public Placeholders clearContext() {
        this.context.clear();
        return this;
    }

    @NotNull
    public Placeholders withSign(@NotNull String sign) {
        this.placeholderSign = Objects.requireNonNull(sign);
        return this;
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