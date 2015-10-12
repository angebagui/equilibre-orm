package com.equilibre;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import bolts.Task;

/**
 * Created by angebagui on 20/08/2015.
 */
public class EquilibreExecutors {

    private static ScheduledExecutorService scheduledExecutor;
    private static final Object SCHEDULED_EXECUTOR_LOCK = new Object();
    /**
     * Long running operations should NOT be put onto SCHEDULED_EXECUTOR.
     *
     * @return ScheduledExecutorService
     */
    public static ScheduledExecutorService scheduled(){
        synchronized (SCHEDULED_EXECUTOR_LOCK){
            if (scheduledExecutor==null){
                scheduledExecutor = Executors.newScheduledThreadPool(1);
            }
        }
        return scheduledExecutor;
    }

    /**
     *
     * @return UI_THREAD_EXECUTOR
     */
    public static Executor main(){
        return Task.UI_THREAD_EXECUTOR;
    }

    /**
     *
     * @return BACKGROUND_EXECUTOR
     */
    public static Executor io(){
        return Task.BACKGROUND_EXECUTOR;
    }
}
