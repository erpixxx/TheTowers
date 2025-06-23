package dev.erpix.thetowers.util;

import org.jetbrains.annotations.NotNull;

/**
 * Consumer that accepts three input arguments and returns no result.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 */
public interface TriConsumer<T, U, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     */
    void accept(T t, U u, V v);

    /**
     * Returns a composed {@code TriConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation. If performing either operation throws an exception, it is relayed to the caller
     * of the composed operation.
     *
     * @param after the operation to perform after this operation.
     * @return a composed {@code TriConsumer} that performs in sequence this operation followed by the {@code after}
     *         operation.
     */
    default TriConsumer<T, U, V> andThen(@NotNull TriConsumer<? super T, ? super U, ? super V> after) {
        return (t, u, v) -> {
            accept(t, u, v);
            after.accept(t, u, v);
        };
    }

}
