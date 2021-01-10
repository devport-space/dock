package space.devport.utils.utility;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.callbacks.CallbackContent;
import space.devport.utils.callbacks.ExceptionCallback;
import space.devport.utils.item.data.Amount;

import java.util.function.Supplier;

@UtilityClass
public class ParseUtil {

    @Nullable
    @Contract("null,_ -> null")
    public <E extends Enum<E>> E parseEnum(String input, Class<E> clazz) {
        return parseEnum(input, clazz, null, null);
    }

    @Contract("null,_,_ -> null")
    public <E extends Enum<E>> E parseEnum(String input, Class<E> clazz, @Nullable ExceptionCallback callback) {
        return parseEnum(input, clazz, null, callback);
    }

    @Contract("null,_,_ -> param3")
    public <E extends Enum<E>> E parseEnum(String input, Class<E> clazz, E defaultValue) {
        return parseEnum(input, clazz, defaultValue, null);
    }

    /**
     * Attempt to parse an enum from {@link String}.
     *
     * @param <E>          Enum type signature.
     * @param input        String to parse.
     * @param clazz        Enum class to parse.
     * @param defaultValue Default value to return on failure.
     * @param callback     {@link ExceptionCallback} to call on failure.
     * @return Parsed enum of {@code <E>} or defaultValue.
     */
    @Contract("null,_,null,_ -> null")
    public <E extends Enum<E>> E parseEnum(String input, Class<E> clazz, @Nullable E defaultValue, @Nullable ExceptionCallback callback) {
        try {
            return E.valueOf(clazz, input.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            if (callback != null)
                callback.call(CallbackContent.createNew(e, "input", input));
            return defaultValue;
        }
    }

    @Contract("null -> null")
    public static Object parseNumber(String str) {
        return parseNumber(str, null);
    }

    /**
     * Attempt to parse a {@link Number} from {@link String}.
     *
     * @param input    String to parse.
     * @param callback {@link ExceptionCallback} to call on failure.
     * @return Parsed object or input if the input is {@code null} or empty.
     */
    @Contract("null,_ -> null")
    public static Object parseNumber(String input, @Nullable ExceptionCallback callback) {

        if (Strings.isNullOrEmpty(input)) {
            if (callback != null)
                callback.call(CallbackContent.createNew(new IllegalArgumentException("Input string cannot be null or empty."),
                        "input", input));
            return input;
        }

        final String str = input.trim();

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignored) {
            // Not an int
        }

        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ignored) {
            // Not a long
        }

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
            // Not a double
        }

        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException ignored) {
            // Not a Float
        }

        return str;
    }

    public double parseDouble(String str) {
        return parseDouble(str, 0, null);
    }

    public double parseDouble(String str, @Nullable ExceptionCallback callback) {
        return parseDouble(str, 0, callback);
    }

    @Contract("null,_ -> param2")
    public double parseDouble(String str, double defaultValue) {
        return parseDouble(str, defaultValue, null);
    }

    @Contract("null,_,_ -> param2")
    public double parseDouble(String str, double defaultValue, @Nullable ExceptionCallback callback) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            if (callback != null)
                callback.call(CallbackContent.createNew(e, "input", str));
            return defaultValue;
        }
    }

    public int parseInteger(String str) {
        return parseInteger(str, 0, null);
    }

    public int parseInteger(String str, @Nullable ExceptionCallback callback) {
        return parseInteger(str, 0, callback);
    }

    @Contract("null,_ -> param2")
    public int parseInteger(String str, int defaultValue) {
        return parseInteger(str, defaultValue, null);
    }

    @Contract("null,_,_ -> param2")
    public int parseInteger(String str, int defaultValue, @Nullable ExceptionCallback callback) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            if (callback != null)
                callback.call(CallbackContent.createNew(e, "input", str));
            return defaultValue;
        }
    }

    @Contract("null -> null")
    public Vector parseVector(String str) {
        return parseVector(str, null, null);
    }

    @Contract("null,null -> null")
    public Vector parseVector(String str, Vector defaultValue) {
        return parseVector(str, defaultValue, null);
    }

    //TODO: Parse vectors with negative numbers.
    @Contract("null,null,_ -> null")
    public Vector parseVector(String str, @Nullable Vector defaultValue, @Nullable ExceptionCallback callback) {

        if (Strings.isNullOrEmpty(str)) {
            if (callback != null)
                callback.call(CallbackContent.createNew(new IllegalArgumentException("Input string cannot be null or empty."),
                        "input", str));
            return defaultValue;
        }

        String[] arr = str.split(";");

        if (arr.length != 3) {
            if (callback != null)
                callback.call(CallbackContent.createNew(new IllegalArgumentException("Not enough arguments."),
                        "input", str));
            return defaultValue;
        }

        Amount x = Amount.fromString(arr[0]);
        Amount y = Amount.fromString(arr[1]);
        Amount z = Amount.fromString(arr[2]);

        return new Vector(x == null ? 0 : x.getDouble(),
                y == null ? 0 : y.getDouble(),
                z == null ? 0 : z.getDouble());
    }

    public <T> T getOrDefault(@NotNull Supplier<T> supplier, T defaultValue) {
        return getOrDefault(supplier, defaultValue, null);
    }

    public <T> T getOrDefault(Supplier<T> supplier, T defaultValue, @Nullable ExceptionCallback callback) {
        try {
            T t = supplier.get();
            return t == null ? defaultValue : t;
        } catch (Exception e) {
            if (callback != null)
                callback.call(CallbackContent.createNew(e, "input"));
            return defaultValue;
        }
    }
}