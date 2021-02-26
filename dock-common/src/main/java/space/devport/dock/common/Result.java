package space.devport.dock.common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Result holds the resulting value of an operation, or an exception that caused it's failure.
 * <p>
 * Note that the contents of a Result can be changed with it's calls.
 * This is a change compared to Optional, which creates a new wrapper on some calls.
 */
public class Result<T> {

    private T value;

    private Exception exception;

    private Result() {
        this.value = null;
        this.exception = null;
    }

    private Result(T value) {
        this.value = Objects.requireNonNull(value, "Result value cannot be null.");
        this.exception = null;
    }

    private Result(Exception exception) {
        Objects.requireNonNull(exception, "Result exception cannot be null.");
        this.exception = exception;
        this.value = null;
    }

    /**
     * Create an empty Result, that will return true on {@link Result#isEmpty()}.
     *
     * @param <T> Result value type parameter.
     * @return Empty Result.
     */
    @NotNull
    public static <T> Result<T> empty() {
        return new Result<>();
    }

    /**
     * Create a new Result with a value, that will return true on {@link Result#isPresent()},
     * if the supplied value is not {@code null}.
     *
     * @param <T>   Result value type parameter.
     * @param value Value that the Result should contain.
     * @return A new Result.
     */
    @NotNull
    public static <T> Result<T> of(@Nullable T value) {
        return value == null ? empty() : new Result<>(value);
    }

    /**
     * Create a new Result from supplier. If no exception was thrown, the Result will hold supplied value (or {@code null}).
     * <p>
     * If the supplier throws an exception, it will be reflected in the result.
     *
     * @param <T>      Result value type parameter.
     * @param supplier Supplier to use.
     * @return A new Result.
     */
    @NotNull
    public static <T> Result<T> supply(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null.");
        try {
            return Result.of(supplier.get());
        } catch (Exception exception) {
            return Result.ofException(exception);
        }
    }

    /**
     * Create a Result with an exception, that will return true on {@link Result#isFailed()}.
     *
     * @param <T>       Result value type parameter.
     * @param exception Exception to use.
     * @return A new Result.
     */
    @NotNull
    public static <T> Result<T> ofException(@NotNull Exception exception) {
        return new Result<>(exception);
    }

    /**
     * Create a Result from an {@link Optional}.
     * <p>
     * If the Optional was empty, return an empty Result.
     *
     * @param <T>      Result value type parameter.
     * @param optional {@link Optional} to parse from.
     * @return A new Result.
     */
    @NotNull
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Result<T> ofOptional(Optional<T> optional) {
        return optional.map(Result::of).orElseGet(Result::empty);
    }

    /**
     * Immediately executes given {@link Consumer} if {@link Result#isPresent()} returns true.
     *
     * @param consumer Consumer to execute.
     * @return This result.
     */
    @NotNull
    public Result<T> ifPresent(Consumer<T> consumer) {
        if (isPresent())
            consumer.accept(value);
        return this;
    }

    /**
     * Immediately executes given {@link Runnable} if {@link Result#isEmpty()} returns true.
     *
     * @param runnable Runnable to execute.
     * @return This result.
     */
    @NotNull
    public Result<T> ifEmpty(Runnable runnable) {
        if (isEmpty())
            runnable.run();
        return this;
    }

    /**
     * Immediately executes given {@link Consumer} if {@link Result#isFailed()} returns true.
     *
     * @param failedConsumer Consumer to execute.
     * @return This result.
     */
    @NotNull
    public Result<T> ifFailed(Consumer<Exception> failedConsumer) {
        if (isFailed())
            failedConsumer.accept(exception);
        return this;
    }

    // Replace the value if it's not present.

    /**
     * Replace the held value, if {@link Result#isEmpty()} returns true.
     *
     * @param value Value to use.
     * @return This result.
     */
    @NotNull
    public Result<T> orDefault(T value) {
        if (isEmpty())
            this.value = value;
        return this;
    }

    /**
     * Replace the held value with value from {@link Supplier}, if {@link Result#isEmpty()} returns true.
     * <p>
     * Note: Any exception thrown in the supplier is suppressed and cached.
     *
     * @param supplier Supplier to provide the new value.
     * @return This result.
     */
    @NotNull
    public Result<T> orDefaultGet(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Supplier cannot be null.");
        try {
            this.value = supplier.get();
        } catch (Exception e) {
            this.exception = e;
        }
        return this;
    }

    @NotNull
    public <X extends Exception> Result<T> ifEmptyThrow(Supplier<X> exceptionSupplier) throws X {
        if (!isPresent())
            throw exceptionSupplier.get();
        return this;
    }

    @NotNull
    public <U> Result<U> map(Function<T, U> mapper) {
        return Result.of(mapper.apply(value));
    }

    // Apply a function to value.
    @NotNull
    public Result<T> apply(Function<T, T> operator) {
        if (isPresent())
            this.value = operator.apply(value);
        return this;
    }

    @NotNull
    public Result<T> executeReactions(ResultReactions<T> reactions) {
        reactions.intake(this);
        return this;
    }

    // Terminal operations

    // isPresent & isEmpty are just inversions of each other.
    public boolean isPresent() {
        return value != null;
    }

    // isEmpty returns true if there is no value present.
    // Note that both isEmpty and isFailed can return true on the same Result.
    public boolean isEmpty() {
        return value == null;
    }

    // isFailed returns true if an exception was supplied.
    public boolean isFailed() {
        return exception != null;
    }

    /**
     * Attempt to retrieve the stored value.
     *
     * @return Held value.
     * @throws NoSuchElementException if {@link Result#isEmpty()} returns true.
     * @throws RuntimeException       containing an underlying cause exception, if {@link Result#isFailed()} returns true.
     */
    @NotNull
    public T get() throws RuntimeException {
        if (isPresent())
            return value;
        else {
            if (exception != null)
                // Wrap in a Runtime exception.
                throw new RuntimeException(exception);
            else
                throw new NoSuchElementException();
        }
    }

    @Contract("!null -> !null")
    public T orElse(T defaultValue) {
        return isPresent() ? value : defaultValue;
    }

    @Nullable
    public T orNull() {
        return isPresent() ? value : null;
    }

    @Nullable
    public T orElseGet(@NotNull Supplier<T> defaultSupplier) {
        Objects.requireNonNull(defaultSupplier, "Default value supplier cannot be null.");
        return isPresent() ? value : defaultSupplier.get();
    }

    // Throw stored exception and flush it.
    public void throwException() throws Exception {
        if (exception != null) {
            // Store temporarily and throw after.
            Exception exception = this.exception;
            this.exception = null;

            throw exception;
        }
    }

    @NotNull
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    // This is here purely for esthetics.
    @NotNull
    public Supplier<T> toSupplier() {
        return this::get;
    }

    // A set of predefined reactions to reduce boilerplate.
    public interface ResultReactions<T> {

        // Process a result.
        void intake(Result<T> result);
    }
}
