package io.github.angebagui.equilibre;

/**
 * Created by angebagui on 20/08/2015.
 */
/**
 * A {@code DeleteCallback} is used to run code after saving a {@link EquilibreObject} in a background
 * thread.
 * <p>
 * The easiest way to use a {@code DeleteCallback} is through an anonymous inner class. Override the
 * {@code done} function to specify what the callback should do after the delete is complete.
 * The {@code done} function will be run in the UI thread, while the delete happens in a
 * background thread. This ensures that the UI does not freeze while the delete happens.
 * </p>
 * <p>
 * For example, this sample code deletes the object {@code myObject} and calls a different
 * function depending on whether the save succeeded or not.
 * </p>
 * <pre>
 * myObject.deleteInBackground(new DeleteCallback() {
 *   public void done(ParseException e) {
 *     if (e == null) {
 *       myObjectWasDeletedSuccessfully();
 *     } else {
 *       myObjectDeleteDidNotSucceed();
 *     }
 *   }
 * });
 * </pre>
 */
public interface DeleteCallback extends EquilibreCallback1<EquilibreException> {
    /**
     * Override this function with the code you want to run after the delete is complete.
     *
     * @param e
     *           The exception raised by the delete, or {@code null} if it succeeded.
     */
    @Override
    public void done(EquilibreException e);
}
