package space.devport.dock.util;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.common.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Supplier;

@UtilityClass
public class ParseUtil {

    /**
     * Attempt to parse an enum from {@link String}.
     *
     * @param <E>   Enum type signature.
     * @param input String to parse.
     * @param clazz Enum class to parse.
     * @return Parsed enum of {@code <E>} or defaultValue.
     */
    public <E extends Enum<E>> Result<E> parseEnum(String input, Class<E> clazz) {
        return Result.supply(() -> E.valueOf(clazz, input.toUpperCase()));
    }

    /**
     * Attempt to parse a {@link Number} from {@link String}.
     *
     * @param input String to parse.
     * @return Parsed object or input if the input is {@code null} or empty.
     */
    @NotNull
    public static Result<Object> parseNumber(String input) {

        if (Strings.isNullOrEmpty(input)) {
            return Result.ofException(new IllegalArgumentException(String.format("Input string cannot be null or empty. Got: %s", input)));
        }

        final String str = input.trim();

        try {
            return Result.of(Integer.parseInt(str));
        } catch (NumberFormatException ignored) {
            // Not an int
        }

        try {
            return Result.of(Long.parseLong(str));
        } catch (NumberFormatException ignored) {
            // Not a long
        }

        try {
            return Result.of(Double.parseDouble(str));
        } catch (NumberFormatException ignored) {
            // Not a double
        }

        try {
            return Result.of(Float.parseFloat(str));
        } catch (NumberFormatException ignored) {
            // Not a Float
        }

        return Result.of(str);
    }

    @NotNull
    public Result<Double> parseDouble(String str) {
        return Result.supply(() -> Double.parseDouble(str.trim()));
    }

    @NotNull
    public Result<Integer> parseInteger(String str) {
        return Result.supply(() -> Integer.parseInt(str.trim()));
    }

    //TODO: Parse vectors with negative numbers.
    /*

    Doesn't parse negative numbers and requires Amount from the main module. Recode this whole thing and reintroduce it.

    @Contract("null,null,_ -> null")
    public Vector parseVectorHandled(String str, @Nullable Vector defaultValue, @Nullable ExceptionCallback callback) {

        if (Strings.isNullOrEmpty(str)) {
            CallbackContent.createNew(new IllegalArgumentException("Input string cannot be null or empty."), "input", str)
                    .callOrThrow(callback);
            return defaultValue;
        }

        String[] arr = str.split(";");

        if (arr.length != 3) {
            CallbackContent.createNew(new IllegalArgumentException("Not enough arguments."), "input", str)
                    .callOrThrow(callback);
            return defaultValue;
        }

        Amount x = Amount.fromString(arr[0]);
        Amount y = Amount.fromString(arr[1]);
        Amount z = Amount.fromString(arr[2]);

        return new Vector(x == null ? 0 : x.getDouble(),
                y == null ? 0 : y.getDouble(),
                z == null ? 0 : z.getDouble());
    }
    */

    /**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>      Type signature
     * @param supplier {@code Supplier<T>} supplier to run.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     */
    @NotNull
    public <T> Result<T> parse(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Value supplier cannot be null.");
        return Result.supply(supplier);
    }

    /**
     * Round a double to a certain number of decimal places.
     *
     * @param value  Value to round.
     * @param places How many decimal places to round to.
     * @return Rounded number.
     * @throws IllegalArgumentException if the amount of decimal places is negative.
     */
    public double roundDouble(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException(String.format("Decimal places cannot be negative. Got: %d", places));

        BigDecimal decimal = BigDecimal.valueOf(value);
        return decimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
}