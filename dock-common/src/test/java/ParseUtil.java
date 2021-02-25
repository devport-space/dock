import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.common.Result;

import java.util.function.Supplier;

// ParseUtil implementation with Result for testing purposes.
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
    @NotNull
    public <E extends Enum<E>> Result<E> parseEnum(String input, Class<E> clazz) {
        return Result.supply(() -> E.valueOf(clazz, input.toUpperCase()));
    }

    /*
    *//**
     * Attempt to parse a {@link Number} from {@link String}.
     *
     * @param input    String to parse.
     * @param callback {@link ExceptionCallback} to call on failure.
     * @return Parsed object or input if the input is {@code null} or empty.
     *//*
    @Contract("null,_ -> null")
    public static Object parseNumberHandled(String input, @Nullable ExceptionCallback callback) {

        if (Strings.isNullOrEmpty(input)) {
            CallbackContent.createNew(new IllegalArgumentException("Input string cannot be null or empty."), "input", input)
                    .callOrThrow(callback);
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
        return parseDoubleHandled(str, 0, null);
    }

    public double parseDoubleHandled(String str, @Nullable ExceptionCallback callback) {
        return parseDoubleHandled(str, 0, callback);
    }

    @Contract("null,_ -> param2")
    public double parseDouble(String str, double defaultValue) {
        return parseDoubleHandled(str, defaultValue, null);
    }

    @Contract("null,_,_ -> param2")
    public double parseDoubleHandled(String str, double defaultValue, @Nullable ExceptionCallback callback) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            CallbackContent.createNew(e, "input", str).callOrThrow(callback);
            return defaultValue;
        }
    }

    public int parseInteger(String str) {
        return parseIntegerHandled(str, 0, null);
    }

    public int parseIntegerHandled(String str, @Nullable ExceptionCallback callback) {
        return parseIntegerHandled(str, 0, callback);
    }

    @Contract("null,_ -> param2")
    public int parseInteger(String str, int defaultValue) {
        return parseIntegerHandled(str, defaultValue, null);
    }

    @Contract("null,_,_ -> param2")
    public int parseIntegerHandled(String str, int defaultValue, @Nullable ExceptionCallback callback) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            CallbackContent.createNew(e, "input", str).callOrThrow(callback);
            return defaultValue;
        }
    }

    *//*
    @Contract("null -> null")
    public Vector parseVector(String str) {
        return parseVectorHandled(str, null, null);
    }

    @Contract("null,null -> null")
    public Vector parseVector(String str, Vector defaultValue) {
        return parseVectorHandled(str, defaultValue, null);
    }
    *//*

    //TODO: Parse vectors with negative numbers.
    *//*

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
    *//*

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * Run attached {@link ExceptionCallback} when an Exception is thrown in the supplier.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>          Type signature
     * @param supplier     {@code Supplier<T>} supplier to run.
     * @param defaultValue Default value to return if an exception was thrown.
     * @param callback     {@link ExceptionCallback} to run on failure.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     *//*
    public <T> T parseHandled(@NotNull Supplier<T> supplier, @Nullable T defaultValue, @Nullable ExceptionCallback callback) {
        Objects.requireNonNull(supplier);

        try {
            return supplier.get();
        } catch (Exception e) {
            CallbackContent.createNew(e).callOrThrow(callback);
            return defaultValue;
        }
    }

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>          Type signature
     * @param supplier     {@code Supplier<T>} supplier to run.
     * @param defaultValue Default value to return if an exception was thrown.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     *//*
    public <T> T parse(@NotNull Supplier<T> supplier, T defaultValue) {
        return parseHandled(supplier, defaultValue, null);
    }

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return {@code null} instead.
     * Run attached {@link ExceptionCallback} when an Exception is thrown in the supplier.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>      Type signature
     * @param supplier {@code Supplier<T>} supplier to run.
     * @param callback {@link ExceptionCallback} to run on failure.
     * @return Supplied value via {@code Supplier<T>} or {@code null}.
     *//*
    public <T> T parseHandled(@NotNull Supplier<T> supplier, ExceptionCallback callback) {
        return parseHandled(supplier, null, callback);
    }

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * Run attached {@link ExceptionCallback} when an Exception is thrown in the supplier.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>      Type signature
     * @param supplier {@code Supplier<T>} supplier to run.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     *//*
    public <T> T parse(@NotNull Supplier<T> supplier) {
        return parseHandled(supplier, null, null);
    }

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * Run attached {@link ExceptionCallback} when an Exception is thrown in the supplier.
     * <p>
     * This is here just for styling and easier exception handling.
     * <p>
     * If the default value is not null, this can never return {@code null}.
     *
     * @param <T>          Type signature
     * @param supplier     {@code Supplier<T>} supplier to run.
     * @param defaultValue Default value to return if an exception was thrown.
     * @param callback     {@link ExceptionCallback} to run on failure.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     *//*
    public <T> T parseNotNullHandled(@NotNull Supplier<T> supplier, @Nullable T defaultValue, @Nullable ExceptionCallback callback) {
        Objects.requireNonNull(supplier);

        try {
            T t = supplier.get();
            return t == null ? defaultValue : t;
        } catch (Exception e) {
            CallbackContent.createNew(e).callOrThrow(callback);
            return defaultValue;
        }
    }

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>          Type signature
     * @param supplier     {@code Supplier<T>} supplier to run.
     * @param defaultValue Default value to return if an exception was thrown.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     *//*
    public <T> T parseNotNull(@NotNull Supplier<T> supplier, T defaultValue) {
        return parseNotNullHandled(supplier, defaultValue, null);
    }

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return {@code null} instead.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>      Type signature
     * @param supplier {@code Supplier<T>} supplier to run.
     * @return Result holding the returned value or thrown exception.
     */
    public <T> Result<T> parse(@NotNull Supplier<T> supplier) {
        return Result.supply(supplier);
    }

    /*

    *//**
     * Attempt to run given {@code Supplier<T>}, if it fails with an exception, suppress it and return a default value instead.
     * Run attached {@link ExceptionCallback} when an Exception is thrown in the supplier.
     * <p>
     * This is here just for styling and easier exception handling.
     *
     * @param <T>      Type signature
     * @param supplier {@code Supplier<T>} supplier to run.
     * @return Supplied value via {@code Supplier<T>} or defaultValue specified.
     *//*
    public <T> T parseNotNull(@NotNull Supplier<T> supplier) {
        return parseNotNullHandled(supplier, null, null);
    }

    public double roundDouble(double value, int places) {
        return roundDoubleHandled(value, places, null);
    }

    public double roundDoubleHandled(double value, int places, @Nullable ExceptionCallback callback) {
        if (places < 0) {
            CallbackContent.createNew(new IllegalArgumentException("Decimal places cannot be null.")).callOrThrow(callback);
            return value;
        }

        BigDecimal decimal = BigDecimal.valueOf(value);
        return decimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
    }*/
}