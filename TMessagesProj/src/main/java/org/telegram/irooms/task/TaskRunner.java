package org.telegram.irooms.task;

import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Handler;

public class TaskRunner {

    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements

    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface TaskCompletionListener<R> {
        void onComplete(R result);
    }

    public <R> void executeAsync(Callable<R> callable, TaskCompletionListener<R> callback) {
        executor.execute(() -> {
            final R result;
            try {
                result = callable.call();
                handler.post(() -> {
                    callback.onComplete(result);
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}