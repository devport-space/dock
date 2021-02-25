package space.devport.dock;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result<T> {

    private static final Result<?> EMPTY = new Result<>();

    private T value;

    private final Exception exception;

    private Result() {
        this.value = null;
        this.exception = null;
    }

    private Result(T value) {
        this.value = Objects.requireNonNull(value, "Result value cannot be null.");
        this.exception = null;
    }

    private Result(Exception exception) {
        this.exception = exception;
        this.value = null;
    }

    public static <T> Result<T> empty() {
        @SuppressWarnings("unchecked")
        Result<T> result = (Result<T>) EMPTY;
        return result;
    }

    public static <T> Result<T> of(T value) {
        return value == null ? empty() : new Result<>(value);
    }

    public static <T> Result<T> supply(Supplier<T> supplier) {
        try {
            return Result.of(supplier.get());
        } catch (Exception exception) {
            return Result.ofException(exception);
        }
    }

    public static <T> Result<T> ofException(Exception exception) {
        return new Result<>(exception);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Result<T> ofOptional(Optional<T> optional) {
        return optional.map(Result::of).orElseGet(Result::empty);
    }

    public Result<T> ifPresent(Consumer<T> consumer) {
        if (isPresent())
            consumer.accept(value);
        return this;
    }

    public Result<T> ifEmpty(Runnable runnable) {
        if (isEmpty())
            runnable.run();
        return this;
    }

    public Result<T> ifFailed(Consumer<Exception> failedConsumer) {
        if (isFailed())
            failedConsumer.accept(exception);
        return this;
    }

    // Replace the value if it's not present.

    public Result<T> orElse(T value) {
        if (isEmpty())
            this.value = value;
        return this;
    }

    public Result<T> orElseGet(Supplier<T> supplier) {
        return Result.supply(supplier);
    }

    public <X extends Exception> Result<T> orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (!isPresent())
            throw exceptionSupplier.get();
        return this;
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        return Result.of(mapper.apply(value));
    }

    // Apply a function to value.
    public Result<T> apply(Function<T, T> operator) {
        if (isPresent())
            this.value = operator.apply(value);
        return this;
    }

    public Result<T> executeReactions(ResultReactions<T> reactions) {
        return reactions.intake(this);
    }

    // Terminal operations

    // isPresent & isEmpty are just inversions of each other.
    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    // isFailed returns true if an exception was supplied.
    public boolean isFailed() {
        return exception != null;
    }

    public T get() throws NoSuchElementException {
        if (isPresent())
            return value;
        else throw new NoSuchElementException();
    }

    public T get(T defaultValue) {
        return isPresent() ? value : defaultValue;
    }

    public T get(Supplier<T> defaultSupplier) {
        return isPresent() ? value : defaultSupplier.get();
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    // This is here purely for esthetics.
    public Supplier<T> toSupplier() {
        return this::get;
    }

    // Usage aka I <3 lambdas.

    // Sample producing methods.

    public static Result<Integer> parseInteger(String string) {
        try {
            // Return the result.
            return Result.of(Integer.parseInt(string));
        } catch (NumberFormatException exception) {
            // Return an empty result with an exception.
            return Result.ofException(exception);
        }
    }

    public static Result<Integer> parseIntegerWithSupply(String string) {
        // Or use the Result#supply with a supplier, that catches exceptions.
        return Result.supply(() -> Integer.parseInt(string));
    }

    // A set of predefined reactions to reduce boilerplate.
    private interface ResultReactions<T> {

        // Process a result.
        Result<T> intake(Result<T> result);
    }

    {
        ResultReactions<Integer> reactions = result -> result
                .ifFailed(e -> System.out.println("Failed to do this: " + e.getMessage()))
                .ifEmpty(() -> System.out.println("Result has no value."));

        // Define reactions in different cases.
        parseIntegerWithSupply("a")
                // Execute predefined reactions in the chain.
                .executeReactions(reactions)
                // If the value is present, verbose it out.
                .ifPresent(value -> System.out.println("Parsed integer: " + value))
                // If it failed with an exception, send an error or something.
                .ifFailed(exception -> System.out.println("Failed to parse integer: " + exception.getMessage()))
                // Supply a default
                .orElseGet(parseIntegerWithSupply("b").orElse(10).toSupplier());
    }
}
