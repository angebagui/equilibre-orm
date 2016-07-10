package io.github.angebagui.equilibre;

import java.util.List;

/**
 * Created by angebagui on 20/08/2015.
 */

/**
 * A {@code FindCallback} is used to run code after a {@link EquilibreQuery} is used to fetch a list of
 * {@link EquilibreObject}s in a background thread.
 * <p>
 * The easiest way to use a {@code FindCallback} is through an anonymous inner class. Override the
 * {@code done} function to specify what the callback should do after the fetch is complete.
 * The {@code done} function will be run in the UI thread, while the fetch happens in a
 * background thread. This ensures that the UI does not freeze while the fetch happens.
 * </p>
 * <p>
 * For example, this sample code fetches all objects of class {@code "MyClass"}. It calls a
 * different function depending on whether the fetch succeeded or not.
 * </p>
 * <pre>
 * EquilibreQuery&lt;EquilibreObject&gt; query = EquilibreQuery.getQuery(&quot;MyClass&quot;);
 * query.findInBackground(new FindCallback&lt;EquilibreObject&gt;() {
 *   public void done(List&lt;EquilibreObject&gt; objects, EquilibreException e) {
 *     if (e == null) {
 *       objectsWereRetrievedSuccessfully(objects);
 *     } else {
 *       objectRetrievalFailed();
 *     }
 *   }
 * });
 * </pre>
 */
public interface FindCallback<T extends EquilibreObject> extends EquilibreCallback2<List<T>, EquilibreException> {
    /**
     * Override this function with the code you want to run after the fetch is complete.
     *
     * @param objects
     *             The objects that were retrieved, or null if it did not succeed.
     * @param e
     *       The exception raised by the save, or null if it succeeded.
     */
    @Override
    public void done(List<T> objects, EquilibreException e);
}
