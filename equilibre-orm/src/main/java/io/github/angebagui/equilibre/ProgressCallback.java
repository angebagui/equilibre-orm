package io.github.angebagui.equilibre;

/**
 * Created by angebagui on 20/08/2015.
 */
/**
 * <p>
 * A {@code ProgressCallback} is used to get upload or download progress of a
 * action.
 * </p>
 * The easiest way to use a {@code ProgressCallback} is through an anonymous inner class.
 */
// FYI, this does not extend EquilibreCallback2 since it does not match the usual signature
// done(T, EquilibreException), but is done(T).
public interface ProgressCallback {
    /**
     * Override this function with your desired callback.
     *@param percentDone  the percentage
     */
    public void done(Integer percentDone);
}
