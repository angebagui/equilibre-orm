package com.equilibre;

/**
 * Created by angebagui on 20/08/2015.
 */
/**
 * A {@code CountCallback} is used to run code after a {@link EquilibreQuery} is used to count objects
 * matching a query in a background thread.
 * <p>
 * The easiest way to use a {@code CountCallback} is through an anonymous inner class. Override the
 * {@code done} function to specify what the callback should do after the count is complete.
 * The {@code done} function will be run in the UI thread, while the count happens in a
 * background thread. This ensures that the UI does not freeze while the fetch happens.
 * </p>
 * <p>
 * For example, this sample code counts objects of class {@code "MyClass"}. It calls a
 * different function depending on whether the count succeeded or not.
 * </p>
 * <pre>
 * EquilibreQuery&lt;ParseObject&gt; query = EquilibreQuery.getQuery(&quot;MyClass&quot;);
 * query.countInBackground(new CountCallback() {
 *   public void done(int count, ParseException e) {
 *     if (e == null) {
 *       objectsWereCountedSuccessfully(count);
 *     } else {
 *       objectCountingFailed();
 *     }
 *   }
 * });
 * </pre>
 */
// FYI, this does not extend EquilibreCallback2 since the first param is `int`, which can't be used
// in a generic.
public interface CountCallback {
    /**
     *
     * @param count
     * @param e EquilibreException an exception
     */
    public void done(long count, EquilibreException e);
}
