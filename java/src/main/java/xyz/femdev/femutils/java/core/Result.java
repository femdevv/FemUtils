package xyz.femdev.femutils.java.core;

import java.util.function.Function;

/**
 * A generic container for representing either a success ({@link Ok}) or an error ({@link Err}).
 *
 * @param <T> the type of the successful result value
 * @param <E> the type of the error value
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {

    /**
     * Creates a successful result.
     *
     * @param v   the value to wrap
     * @param <T> the type of the value
     * @param <E> the type of the error
     * @return a new {@link Ok} instance containing the value
     */
    static <T, E> Ok<T, E> ok(T v) {
        return new Ok<>(v);
    }

    /**
     * Creates an error result.
     *
     * @param e   the error to wrap
     * @param <T> the type of the value (unused)
     * @param <E> the type of the error
     * @return a new {@link Err} instance containing the error
     */
    static <T, E> Err<T, E> err(E e) {
        return new Err<>(e);
    }

    /**
     * Checks if the result is successful.
     *
     * @return {@code true} if this is an {@link Ok}, {@code false} otherwise
     */
    default boolean isOk() {
        return this instanceof Ok<?, ?>;
    }

    /**
     * Checks if the result is an error.
     *
     * @return {@code true} if this is an {@link Err}, {@code false} otherwise
     */
    default boolean isErr() {
        return this instanceof Err<?, ?>;
    }

    /**
     * Returns the value if successful, or a fallback value if this is an error.
     *
     * @param fallback the value to return if this is an error
     * @return the successful value or the fallback
     */
    default T orElse(T fallback) {
        return this instanceof Ok<T, E>(T value) ? value : fallback;
    }

    /**
     * Applies a transformation function to the value if successful.
     *
     * @param fn  the function to apply
     * @param <U> the type of the transformed value
     * @return a new {@code Result} with the transformed value or the original error
     */
    default <U> Result<U, E> map(Function<? super T, ? extends U> fn) {
        return this instanceof Ok<T, E>(T value)
                ? new Ok<>(fn.apply(value))
                : (Err<U, E>) this;
    }

    /**
     * Represents a successful result.
     *
     * @param value the wrapped value
     * @param <T>   the type of the value
     * @param <E>   the type of the error
     */
    record Ok<T, E>(T value) implements Result<T, E> {
    }

    /**
     * Represents an error result.
     *
     * @param error the wrapped error
     * @param <T>   the type of the value
     * @param <E>   the type of the error
     */
    record Err<T, E>(E error) implements Result<T, E> {
    }
}
