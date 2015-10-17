package com.equilibre;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * <p>
 * The {@code EquilibreQuery} class defines a query that is used to fetch {@link EquilibreObject}s. The most
 * common use case is finding all objects that match a query through the {@link #findInBackground(EquilibreCallback2)}}
 * method, using a {@link FindCallback}. For example, this sample code fetches all objects of class
 * {@code "MyClass"}. It calls a different function depending on whether the fetch succeeded or not.
 * </p>
 * <pre>
 * EquilibreQuery&lt;EquilibreObject&gt; query = EquilibreQuery.getQuery(MyClass.class);
 * query.findInBackground(new FindCallback&lt;EquilibreObject&gt;() {
 *     public void done(List&lt;EquilibreObject&gt; objects, EquilibreException e) {
 *         if (e == null) {
 *             objectsWereRetrievedSuccessfully(objects);
 *         } else {
 *             objectRetrievalFailed();
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * A {@code EquilibreQuery} can also be used to retrieve a single object whose id is known, through the
 * {@link #getInBackground(Integer)} method, using a {@link GetCallback}. For example, this
 * sample code fetches an object of class {@code "MyClass"} and id {@code myId}. It calls
 * a different function depending on whether the fetch succeeded or not.
 * </p>
 * <pre>
 * EquilibreQuery&lt;EquilibreObject&gt; query = EquilibreQuery.getQuery(MyClass.class);
 * query.getInBackground(myId, new GetCallback&lt;EquilibreObject&gt;() {
 *     public void done(EquilibreObject object, EquilibreException e) {
 *         if (e == null) {
 *             objectWasRetrievedSuccessfully(object);
 *         } else {
 *             objectRetrievalFailed();
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * A {@code  EquilibreQuery} can also be used to count the number of objects that match the query without
 * retrieving all of those objects. For example, this sample code counts the number of objects of
 * the class {@code "MyClass"}.
 * </p>
 * <pre>
 *  EquilibreQuery&lt; EquilibreObject&gt; query =  EquilibreQuery.getQuery(MyClass.class);
 * query.countInBackground(new CountCallback() {
 *     public void done(int count, EquilibreException e) {
 *         if (e == null) {
 *             objectsWereCounted(count);
 *         } else {
 *             objectCountFailed();
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * Using the callback methods is usually preferred because the network operation will not block the
 * calling thread. However, in some cases it may be easier to use the {@link #find()},
 * {@link #get(Integer)} or {@link #count()} calls, which do block the calling thread. For example,
 * if your application has already spawned a background task to perform work, that background task
 * could use the blocking calls and avoid the code complexity of callbacks.
 * </p>
 */
public class EquilibreQuery<T extends EquilibreObject> {

    Dao<? extends EquilibreObject, Integer> dao;
    private final Object lock = new Object();
    private boolean isRunning = false;
    private Task<Void>.TaskCompletionSource cts;

     final TaskQueue taskQueue =  new TaskQueue();

    private QueryBuilder queryBuilder;

    /**
     * Constraints for a {@code EquilibreQuery}'s where clause. A map of field names to constraints. The
     * values can either be actual values to compare with for equality, or instances of
     * {@link com.j256.ormlite.stmt.Where}.
     */
    private Where wheres ;

    private boolean isWhereClauses =false;

       Object LOCK = new Object();
       public EquilibreQuery(QueryBuilder queryBuilder){
           this.queryBuilder  = queryBuilder;
           this.wheres = this.queryBuilder.where();


       }

    /**
     *
     * @return the current query builder
     */
       public QueryBuilder<T, Integer> getQueryBuilder(){
           queryBuilder.setWhere(wheres);
           return queryBuilder;
       }

    /**
     *
     * @param builder Assists in building sql query (SELECT) statements for a particular table in a particular database.
     */
       public void setQueryBuilder(QueryBuilder builder){
           this.queryBuilder = builder;
           this.wheres = this.queryBuilder.where();
       }


    /**
     * Constructs a query for a {@link EquilibreObject} subclass type. A default query with no further
     * parameters will retrieve all {@link EquilibreObject}s of the provided class.
     *
     * @param subclass
     *          The {@link EquilibreObject} subclass type to retrieve.
     */

    public EquilibreQuery(Class<T> subclass) {
      EquilibreOpenHelper helper = EquilibreOpenHelper.getHelper(EquilibrePlugins.Android.get().applicationContext());
        try {
            this.dao = helper.getDao(subclass);
            this.queryBuilder = dao.queryBuilder();
            this.wheres = this.queryBuilder.where();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a new query for the given {@link EquilibreObject} subclass type. A default query with no
     * further parameters will retrieve all {@link EquilibreObject}s of the provided class.
     *
     *
     * @param subclass The {@link EquilibreObject} subclass type to retrieve.
     * @return A new {@code EquilibreQuery}.
     * @param <T> the type of model your currently manipulated
     */
    public static <T extends EquilibreObject> EquilibreQuery<T> getQuery(Class<T> subclass) {
        return new EquilibreQuery<>(subclass);
    }

    public Dao getDao() {
        return dao;
    }

    public void refresh(final T t) throws EquilibreException {
        EquilibreTaskUtils.wait(refreshInBackground(t));
    }

    /**
     * Update object with asynchronous method
     *
     * @param t
     *          the object to update
     * @param callback
     *      See{@link RefreshCallback}
     */
    public void refreshInBackground(final T t, RefreshCallback callback){
        EquilibreTaskUtils.callbackOnMainThreadAsync(refreshInBackground(t), callback);
    }

    /**
     * Add update operate in the TaskQueue
     *
     * @param t
     * @return
     */
    private Task<T> refreshInBackground(final T t){
        return taskQueue.enqueue(new Continuation<Void, Task<T>>() {
            @Override
            public Task<T> then(Task<Void> task) throws Exception {
                return task.continueWith(new Continuation<Void, T>() {
                    @Override
                    public T then(Task<Void> task) throws Exception {
                       int result = getDao().update(t);
                        if (result > 0) {
                            Log.d(Equilibre.TAG, t.getClass().getSimpleName() + " updated successfully with Id ==>>> " + t.getId());
                        } else {
                            Log.e(Equilibre.TAG, " echec when saved" + t.getClass().getSimpleName());
                        }

                        return t;
                    }
                });
            }
        });
    }
    /**
     * Count {@link EquilibreObject}'s subclass in asynchronuously
     *
     *
     * @param callback
     *          See {@link DeleteCallback}
     */
    public void countInBackground(final CountCallback callback){
        final Task<Long>.TaskCompletionSource cts = Task.create();
        countInBackground().continueWith(new Continuation<Long, Void>() {
            @Override
            public Void then(final Task<Long> task) throws Exception {
                if (task.isCancelled()) {
                    cts.setCancelled();
                    return null;
                }

                EquilibreExecutors.main().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Exception error = task.getError();
                            if (error != null && !(error instanceof EquilibreException)) {
                                error = new EquilibreException(error);
                            }
                            callback.done(task.getResult(), (EquilibreException) error);
                        } finally {
                            if (task.isCancelled()) {
                                cts.setCancelled();
                            } else if (task.isFaulted()) {
                                cts.setError(task.getError());
                            } else {
                                cts.setResult(task.getResult());
                            }
                        }
                    }
                });
                return null;
            }
        });

    }
    /**
     * Count {@link EquilibreObject}'s subclass in a background thread
     *
     */
    private Task<Long> countInBackground(){
        return Task.callInBackground(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return getQueryBuilder().countOf();
            }
        });
    }

    public void delete(T t) throws EquilibreException {
        EquilibreTaskUtils.wait(deleteInBackground(t));
    }
    /**
     * Delete {@link EquilibreObject}'s subclass in asynchronuously
     *
     * @param id
     *        the object to save
     *
     * @param callback
     *          See {@link DeleteCallback}
     */
    public void deleteInBackground(Integer id, DeleteCallback callback){
        EquilibreTaskUtils.callbackOnMainThreadAsync(deleteInBackground(id), callback);
    }

    /**
     * Delete {@link EquilibreObject}'s subclass in asynchronuously
     *
     * @param t
     *        the object to save
     * @param callback
     *          See {@link DeleteCallback}
     */
    public void deleteInBackground(final T t, DeleteCallback callback){
        EquilibreTaskUtils.callbackOnMainThreadAsync(deleteInBackground(t), callback);
    }
    /**
     * Delete {@link EquilibreObject}'s subclass in a background thread
     *
     * @param t
     *        the object to save
     */
    private Task<Void> deleteInBackground(final T t){
        checkIfRunning(true);
        return taskQueue.enqueue(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return task;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                int result = getDao().delete(t);
                if (result>0){
                    Log.d(Equilibre.TAG, t.getClass().getSimpleName()+" deleted successfully with Id ==>>> "+t.getId());
                }else{
                    Log.e(Equilibre.TAG, " echec when deleted "+t.getClass().getSimpleName());
                }

                return null;
            }
        });
    }
    /**
     * Delete {@link EquilibreObject}'s subclass in a background thread
     *
     * @param id
     *        the id object to save
     */
    private Task<Void> deleteInBackground(final Integer id){
        checkIfRunning(true);
        return taskQueue.enqueue(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return task;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                int result = getDao().deleteById(id);
                if (result>0){
                    Log.d(Equilibre.TAG, getDao().getClass().getSimpleName()+" deleted successfully with Id ==>>> "+id);
                }else{
                    Log.e(Equilibre.TAG, " echec when deleted "+getDao().getClass().getSimpleName());
                }

                return null;
            }
        });
    }


    /**
     * Find List {@link EquilibreObject}'s subclass asynchronuously
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
     * @param callback this callback run on the mainthread
     *
     */
    public void findInBackground(EquilibreCallback2 callback){
        if (callback instanceof FindCallback){
            EquilibreTaskUtils.callbackOnMainThreadAsync(findInBackground(), callback);
        }else {
            EquilibreTaskUtils.callbackOnMainThreadAsync(findOneInBackground(), callback);
        }

    }

    /**
     * Find List of {@link EquilibreObject}'s subclass in a background thread
     *

     * @return A {@link bolts.Task} that is resolved when finding completes.
     */
    private Task<List<T>> findInBackground(){
        checkIfRunning(true);
        return Task.callInBackground(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {

                if (isWhereClauses) {
                    return getQueryBuilder().query();
                } else {
                    return getDao().queryForAll();
                }

            }
        });
    }
    private Task<T> findOneInBackground(){
        checkIfRunning(true);
        return Task.callInBackground(new Callable<T>() {
            @Override
            public T call() throws Exception {

                if (isWhereClauses) {
                    return getQueryBuilder().queryForFirst();
                } else {
                    throw new RuntimeException("None clause used");
                }
            }
        });
    }

    /**
     *
     * Get a model by id synchronously
     *
     * @param id id of the object
     * @return the object found
     * @throws EquilibreException an exception can be thrown if something is wrong
     */
    public T get(Integer id) throws EquilibreException {
        return EquilibreTaskUtils.wait(getInBackground(id));
    }

    /**
     *
     * @return List of Object found synchronously
     * @throws EquilibreException an exception can be thrown if something is wrong
     */
    public List<T> find() throws EquilibreException {
        return EquilibreTaskUtils.wait(findInBackground());
    }

    /**
     *
     * @return the count in the database
     * @throws EquilibreException an exception can be thrown if something is wrong
     */
    public long count() throws EquilibreException {
        return EquilibreTaskUtils.wait(countInBackground());
    }
    /**
     * Find {@link EquilibreObject}'s subclass asynchronuously
     *
     * @param id
     *        the id of object
     * @param callback
     *          the callback to get the feedback after saving
     */
    public void getInBackground(final Integer id, GetCallback callback){
        EquilibreTaskUtils.callbackOnMainThreadAsync(getInBackground(id), callback);
    }

    /**
     * Find {@link EquilibreObject}'s subclass in a background thread
     *
     * @param id
     *        the object to save
     * @return A {@link bolts.Task} that is resolved when getting all completes.
     */
    private Task<T> getInBackground(final Integer id){
        checkIfRunning(true);
        return Task.callInBackground(new Callable<T>() {
            @Override
            public T call() throws Exception {
                T t = (T) getDao().queryForId(id);

                return t;
            }
        });
    }


    /**
     *
     * @param t the data
     * @throws EquilibreException an Equilibre exception can be thrown if something is wrong
     */
    public void save(T t) throws EquilibreException {
        EquilibreTaskUtils.wait(saveInBackground(t));
    }

    /**
     * Save {@link EquilibreObject}'s subclass asynchronuously
     *
     * @param t
     *        the object to save
     * @param callback
     *          the callback to get the feedback after saving
     */
    public void saveInBackground(T t, SaveCallback callback){
        EquilibreTaskUtils.callbackOnMainThreadAsync(saveInBackground(t), callback);
    }

    /**
     *
     * We add a task created for saving {@link EquilibreObject's subclass} in the The TaskQueue.
     * The goal is to create a chain of task that will be managed one after one.
     *
     * @param t
     *      the object to save
     * @return A {@link bolts.Task} that is resolved when saving all completes.
     */
    private  Task<Void> saveInBackground(final T t ){
       return taskQueue.enqueue(new Continuation<Void, Task<Void>>() {
           @Override
           public Task<Void> then(Task<Void> task) throws Exception {
               return task;
           }
       }).continueWith(new Continuation<Void, Void>() {
           @Override
           public Void then(Task<Void> task) throws Exception {
               int result = getDao().create(t);
               if (result > 0) {
                   Log.d(Equilibre.TAG, t.getClass().getSimpleName() + " saved successfully with Id ==>>> " + t.getId());
               } else {
                   Log.e(Equilibre.TAG, " echec when saved" + t.getClass().getSimpleName());
               }
               return null;
           }
       }, Task.BACKGROUND_EXECUTOR);

    }
    private void checkIfRunning() {
        checkIfRunning(false);
    }

    private void checkIfRunning(boolean grabLock) {
        synchronized (lock) {
            if (isRunning) {
                throw new RuntimeException(
                        "This query has an outstanding network connection. You have to wait until it's done.");
            } else if (grabLock) {
                isRunning = true;
                cts = Task.create();
            }
        }
    }

    /**
     *See {@code getInBackground}
     *
     * @param id the value of the EquilibreObject subclass
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
       public EquilibreQuery<T> whereObjectIdEquals(Integer id) throws SQLException {
           wheres = wheres.idEq(id);
           queryBuilder.setWhere(wheres);
           return this;
       }

    /**
     *
     * @param key the Column name
     * @param value the value
     * @return the current query for request
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
       public EquilibreQuery<T> whereEqualTo(String key, Object value) throws SQLException {
           synchronized (LOCK){
               wheres = wheres.eq(key, value);
               this.queryBuilder.setWhere(wheres);
               isWhereClauses = true;
           }
           return this;
       }

    /**
     * Add a constraint to the query that requires a particular key's value to be not equal to the
     * provided value.
     *
     * @param key
     *          The key to check.
     * @param value
     *          The value that must not be equalled.
     * @return this, so you can chain this call.
     *
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereNotEqualTo(String key, Object value) throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.ne(key, value);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }
       /**
        * Sorts the results in ascending order by the given key.
        *
        * @param key
        *          The key to order by.
        * @return this, so you can chain this call.
        */
       public EquilibreQuery<T> orderByAscending(String key){
           checkIfRunning();
           synchronized (LOCK) {
               this.setQueryBuilder(queryBuilder.orderBy(key, true));
           }
           return this;

       }
       /**
        * Sorts the results in descending order by the given key.
        *
        * @param key
        *          The key to order by.
        * @return this, so you can chain this call.
        */
       public EquilibreQuery<T> orderByDescending(String key) {
           checkIfRunning();
           this.setQueryBuilder(queryBuilder.orderBy(key, false));
           return this;
       }

    /**
     * Controls the maximum number of results that are returned.
     * <p>
     * Setting a negative limit denotes retrieval without a limit. The default limit is {@code 100},
     * with a maximum of {@code 1000} results being returned at a time.
     *</p>
     * @param newLimit The new limit.
     * @return this, so you can chain this call.
     */
       public EquilibreQuery<T> setLimit(Long newLimit){
           checkIfRunning();
           this.setQueryBuilder(queryBuilder.limit(newLimit));
           return this;
       }

    /**
     *
     * @param key the column name
     * @param value the value
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
       public EquilibreQuery<T> whereLessThan(String key, Object value) throws SQLException {
           checkIfRunning();
           synchronized (LOCK) {
               wheres = wheres.lt(key, value);
               this.queryBuilder.setWhere(wheres);
               isWhereClauses = true;
           }
           return this;
       }

    /**
     *
     * @param key the column name
     * @param value the value
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
       public EquilibreQuery<T> whereGreaterThan(String key, Object value) throws SQLException {
           checkIfRunning();
           synchronized (LOCK) {
               wheres = wheres.gt(key, value);
               this.queryBuilder.setWhere(wheres);
           }
           return this;
       }

    /**
     *
     * @param key the column name
     * @param value the value
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereLessThanOrEqualTo(String key, Object value) throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.le(key, value);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to be greater than or
     * equal to the provided value.
     *
     * @param key
     *          The key to check.
     * @param value
     *          The value that provides an lower bound.
     * @return this, so you can chain this call.
     *
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereGreaterThanOrEqualTo(String key, Object value) throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.ge(key, value);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     * Add a constraint to the query that requires a particular key's value to be contained in the
     * provided list of values.
     *
     * @param key
     *          The key to check.
     * @param values
     *          The values that will match.
     * @return this, so you can chain this call.
     *
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereContainedIn(String key, Iterable<?> values) throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.in(key, values);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }
    /**
     * Add a constraint to the query that requires a particular key's value to be contained in the
     * provided list of values.
     *
     * @param key
     *          The key to check.
     * @param values
     *          The values that will match.
     * @return this, so you can chain this call.
     *
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereContainedIn(String key,Object... values ) throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.in(key, values);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }
    /**
     * Add a constraint to the query that requires a particular key's value to be contained in the
     * provided list of values.
     *
     * @param key
     *          The key to check.
     * @param values
     *          The values that will match.
     * @return this, so you can chain this call.
     *
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereContainedIn(String key,QueryBuilder values ) throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.in(key, values);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }



    /**
     * Add a constraint for finding objects that contain the given key.
     *
     * @param queryBuilder
     *          The key that should exist.
     *
     * @return this, so you can chain this call.
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereExists(QueryBuilder queryBuilder) throws SQLException {
        checkIfRunning();

        synchronized (LOCK){
            wheres = wheres.exists(queryBuilder);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     *
     * @param key the column name
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> groupBy(String key) throws SQLException {
        synchronized (LOCK){
           this.setQueryBuilder(queryBuilder.groupBy(key)) ;
        }
        return this;
    }

    /**
     *
     * @param key the column name
     * @param low the low
     * @param high the high
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereBetween(String key, Object low, Object high)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.between(key, low, high);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     *
     * @param key the column name
     * @param value the value
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereLike(String key, Object value)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.like(key, value);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     *
     * @param key the column name
     * @return the current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> whereIsNotNull(String key)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.isNotNull(key);
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     *
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> or()throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.or();
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     *
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> and()throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            wheres = wheres.and();
            this.queryBuilder.setWhere(wheres);
            isWhereClauses = true;
        }
        return this;
    }

    /**
     *
     * @param builder Assists in building sql query (SELECT) statements for a particular table in a particular database.
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> withJoin(QueryBuilder builder)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.join(builder));
        }
        return this;
    }

    /**
     *
     * @param builder Assists in building sql query (SELECT) statements for a particular table in a particular database.
     * @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> withJoinOr(QueryBuilder builder)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.joinOr(builder));;
        }
        return this;
    }

    /**
     *
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> distinct()throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.distinct());;
        }
        return this;
    }

    /**
     *
     * @param builder Assists in building sql query (SELECT) statements for a particular table in a particular database.
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> withLeftJoin(QueryBuilder builder)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.leftJoin(builder));;
        }
        return this;
    }

    /**
     *
     * @param builder  Assists in building sql query (SELECT) statements for a particular table in a particular database.
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> withLeftJoinOr(QueryBuilder builder)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.leftJoinOr(builder));;
        }
        return this;
    }

    /**
     *
     * @param having your having
     * @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */

    public EquilibreQuery<T> having(String having)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.having(having));;
        }
        return this;
    }

    /**
     *
     * @param rawSql a raw sql
     * @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> groupByRaw(String rawSql)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.groupByRaw(rawSql));;
        }
        return this;
    }

    /**
     *
     * @param startRow your start row
     *  @return current query
     * @throws SQLException an SQL exception can be thrown if something is wrong
     */
    public EquilibreQuery<T> setOffSet(Long startRow)throws SQLException {
        checkIfRunning();
        synchronized (LOCK){
            this.setQueryBuilder(queryBuilder.offset(startRow));;
        }
        return this;
    }
    /**
     * Accessor for the class name.
     *
     * @return the name the class concerned
     */
    public String getClassName() {
        return dao.getDataClass().getSimpleName();
    }




}
