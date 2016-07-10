package io.github.angebagui.equilibre;

import java.util.concurrent.CancellationException;

import bolts.AggregateException;
import bolts.Continuation;
import bolts.Task;

/**
 * Created by angebagui on 19/08/2015.
 */
public class EquilibreTaskUtils {

    /**
     * Converts a task execution into a synchronous action.
     * @param task a task that is executed in synchronous action
     */
    //TODO (grantland): Task.cs actually throws an AggregateException if the task was cancelled with
    // TaskCancellationException as an inner exception or an AggregateException with the original
    // exception as an inner exception if task.isFaulted().
    // https://msdn.microsoft.com/en-us/library/dd235635(v=vs.110).aspx

    /**
     *
     * @param task the task to execute in background thread
     * @param <T> T is the EquilibreObject's subclass
     * @return T is the EquilibreObject's subclass
     * @throws EquilibreException See {@link EquilibreException}
     */
    public static <T> T wait(Task<T> task) throws EquilibreException{
        try {
            task.waitForCompletion();
            if (task.isFaulted()){
                Exception error = task.getError();
                if (error instanceof EquilibreException){
                    throw (EquilibreException)error;
                }
                if (error instanceof AggregateException){
                    throw new EquilibreException(error);
                }
                if (error instanceof RuntimeException){
                    throw (RuntimeException)error;
                }
                throw new RuntimeException(error);
            }else if (task.isCancelled()){
                throw new RuntimeException(new CancellationException());
            }
            return task.getResult();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

    }
    //region Task to Callbacks
    /**
     * Calls the callback after a task completes on the main thread, returning a Task that completes
     * with the same result as the input task after the callback has been run.
     * @param task the task to execute in background thread
     *
     * @param callback the callback for asynchronous execution
     *
     * @return a task
     */
    public static Task<Void> callbackOnMainThreadAsync(Task<Void> task,
                                                       final EquilibreCallback1<EquilibreException> callback){
        return callbackOnMainThreadAsync(task, callback, false);
    }
    /**
     * Calls the callback after a task completes on the main thread, returning a Task that completes
     * with the same result as the input task after the callback has been run. If reportCancellation
     * is false, the callback will not be called if the task was cancelled.
     *
     * @param task the task to execute in background thread
     * @param callback the callback for asynchronous execution
     * @param reportCancellation If reportCancellation
     * is false, the callback will not be called if the task was cancelled.
     * @return a task
     */
    private static Task<Void> callbackOnMainThreadAsync(Task<Void> task,
                                                       final EquilibreCallback1<EquilibreException> callback,
                                                       final boolean reportCancellation){
        if (callback==null){
            return task;
        }
        return callbackOnMainThreadAsync(task, new EquilibreCallback2<Void, EquilibreException>() {
            @Override
            public void done(Void aVoid, EquilibreException e) {
                callback.done(e);
            }
        }, reportCancellation);
    }
    /**
     * Calls the callback after a task completes on the main thread, returning a Task that completes
     * with the same result as the input task after the callback has been run.
     * @param task the task to execute in background thread
     * @param callback See{@link EquilibreCallback2}
     * @param <T> T is the EquilibreObject's subclass
     * @return a task
     */
    public static <T> Task<T> callbackOnMainThreadAsync(Task<T> task,
                                                        final EquilibreCallback2<T, EquilibreException> callback){
        return callbackOnMainThreadAsync(task,callback, false);
    }
    /**
     * Calls the callback after a task completes on the main thread, returning a Task that completes
     * with the same result as the input task after the callback has been run. If reportCancellation
     * is false, the callback will not be called if the task was cancelled.
     * @param task the task to execute in background thread
     * @param callback the callback for asynchronous execution
     * @param reportCancellation If reportCancellation
     * is false, the callback will not be called if the task was cancelled.
     * @param <T> T is the EquilibreObject's subclass
     * @return a task
     */
     static <T>Task<T> callbackOnMainThreadAsync(Task<T> task,
        final EquilibreCallback2<T, EquilibreException> callback, final boolean reportCancellation){
        if (callback==null){
            return task;
        }
        final Task<T>.TaskCompletionSource tcs = Task.create();
        task.continueWith(new Continuation<T, Void>() {
            @Override
            public Void then(final Task<T> task) throws Exception {
                if (task.isCancelled() && !reportCancellation){
                    tcs.setCancelled();
                    return null;
                }
                EquilibreExecutors.main().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Exception error = task.getError();
                            if (error !=null && !(error instanceof EquilibreException)){
                                error = new EquilibreException(error);
                            }
                            callback.done(task.getResult(), (EquilibreException)error);
                        }finally {
                            if (task.isCancelled()){
                                tcs.setCancelled();
                            }else if(task.isFaulted()){
                                tcs.setError(task.getError());
                            }else {
                                tcs.setResult(task.getResult());
                            }
                        }
                    }
                });

                return null;
            }

        });

        return tcs.getTask();
    }
}
