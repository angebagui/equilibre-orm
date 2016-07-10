package io.github.angebagui.equilibre;

/**
 * Created by angebagui on 20/08/2015.
 */
/**
 * A {@code RefreshCallback} is used to run code after refresh is used to update a {@link EquilibreObject} in a
 * background thread.
 * <p>
 * The easiest way to use a {@code RefreshCallback} is through an anonymous inner class. Override
 * the {@code done} function to specify what the callback should do after the refresh is complete.
 * The {@code done} function will be run in the UI thread, while the refresh happens in a
 * background thread. This ensures that the UI does not freeze while the refresh happens.
 * </p>
 * <p>
 * For example, this sample code refreshes an object of class {@code "MyClass"} and id
 * {@code myId}. It calls a different function depending on whether the refresh succeeded or
 * not.
 * </p>
 * <pre>
 * object.refreshInBackground(new RefreshCallback() {
 *   public void done(EquilibreObject object, EquilibreException e) {
 *     if (e == null) {
 *       objectWasRefreshedSuccessfully(object);
 *     } else {
 *       objectRefreshFailed();
 *     }
 *   }
 * });
 * </pre>
 */
public interface RefreshCallback<T extends EquilibreObject> extends EquilibreCallback2<T, EquilibreException> {
    /**
     * Override this function with the code you want to run after the save is complete.
     *
     * @param object
     *              The object that was refreshed, or {@code null} if it did not succeed.
     * @param e
     *      The exception raised by the login, or {@code null} if it succeeded.
     */
    @Override
    public void done(T object, EquilibreException e);
}
