package io.github.angebagui.equilibre;

/**
 * Created by angebagui on 20/08/2015.
 */
/**
 * A {@code EquilibreCallback} is used to do something after a background task completes. End users will
 * use a specific subclass of {@code EquilibreCallback}.
 */
 interface EquilibreCallback2<T1,T2 extends Throwable> {
    /**
     * <p>
     * {@code done(t1, t2)} must be overridden when you are doing a background operation. It is called
     * when the background operation completes.
     * </p>
     * <p>
     * If the operation is successful, {@code t1} will contain the results and {@code t2} will be
     * {@code null}.
     * </p>
     * If the operation was unsuccessful, {@code t1} will be {@code null} and {@code t2} will contain
     * information about the operation failure.
     *
     * @param t1
     *          Generally the results of the operation.
     * @param t2
     *          Generally an {@link Throwable} that was thrown by the operation, if there was any.
     */
    public void done(T1 t1, T2 t2);
}
