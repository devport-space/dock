package space.devport.utils.utility;

import com.google.common.base.Strings;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.DevportPlugin;
import space.devport.utils.item.Amount;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ParseUtil {

    private final ConsoleOutput output;

    public ParseUtil(ConsoleOutput output) {
        this.output = output;
    }

    public ParseUtil(DevportPlugin devportPlugin) {
        this(devportPlugin.getConsoleOutput());
    }

    /**
     * Attempt to parse the enum from String {@param str}
     *
     * @return Parsed enum or {@code null}
     */
    @Nullable
    public <E extends Enum<E>> E parseEnum(String str, Class<E> clazz) {
        try {
            return E.valueOf(clazz, str);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Attempt to parse the enum from String {@param str}
     *
     * @return Parsed enum or {@param defaultValue}
     */
    @NotNull
    public <E extends Enum<E>> E parseEnum(String str, Class<E> clazz, @NotNull E defaultValue) {
        try {
            return E.valueOf(clazz, str);
        } catch (IllegalArgumentException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Attempt to parse an object from String.
     *
     * @return Parsed object or {@param input} if the input is {@code null} or empty.
     */
    public static Object parseNumber(String input) {

        if (Strings.isNullOrEmpty(input))
            return input;

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

        return input;
    }

    public double parseDouble(String str, boolean... silent) {
        return parseDouble(str, 0, silent);
    }

    public double parseDouble(String str, double defaultValue, boolean... silent) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            if (silent.length < 1 || !silent[0])
                output.warn("Could not parse double from " + str + ", using " + defaultValue + " as default.");
            return defaultValue;
        }
    }

    public int parseInteger(String str, boolean... silent) {
        return parseInteger(str, 0, silent);
    }

    public int parseInteger(String str, int defaultValue, boolean... silent) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            if (silent.length < 1 || !silent[0])
                output.warn("Could not parse int from " + str + ", using " + defaultValue + " as default.");
            return defaultValue;
        }
    }

    public Vector parseVector(@Nullable String str) {

        if (Strings.isNullOrEmpty(str)) {
            return new Vector();
        }

        String[] arr = str.split(";");

        if (arr.length != 3) {
            output.warn("Could not parse vector from " + str + ", invalid number of parameters.");
            return new Vector();
        }

        Amount x = Amount.fromString(arr[0]);
        Amount y = Amount.fromString(arr[1]);
        Amount z = Amount.fromString(arr[2]);

        return new Vector(x == null ? 0 : x.getDouble(),
                y == null ? 0 : y.getDouble(),
                z == null ? 0 : z.getDouble());
    }

    public <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            T t = supplier.get();
            return t == null ? defaultValue : t;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public <T> T getOrDefault(Supplier<T> supplier, T defaultValue, Consumer<Throwable> exceptionCallback) {
        try {
            T t = supplier.get();
            return t == null ? defaultValue : t;
        } catch (Exception e) {
            exceptionCallback.accept(e);
            return defaultValue;
        }
    }
}