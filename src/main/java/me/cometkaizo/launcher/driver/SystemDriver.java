package me.cometkaizo.launcher.driver;

import me.cometkaizo.launcher.app.App;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public abstract class SystemDriver {

    protected final App app;


    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final List<Consumer<? super ScheduledExecutorService>> taskQueue = new ArrayList<>(1);
    private final List<ScheduledFuture<?>> tasks = new ArrayList<>(1);
    private boolean isRunning = false;

    protected SystemDriver(App app) {
        this.app = app;
    }

    public void start() {
        if (isRunning) return;

        setup();

        isRunning = true;
    }

    public void stop() {
        if (!isRunning) return;

        cleanup();
        tasks.forEach(loop -> loop.cancel(false));
        tasks.clear();

        isRunning = false;
    }

    protected void setup() {
        taskQueue.forEach(t -> t.accept(executor));
        taskQueue.clear();
    }

    protected void cleanup() {

    }


    protected void addLoop(Runnable task, long delay, long period, TimeUnit unit, ExceptionManager exceptionManager) {
        taskQueue.add(executor -> executor.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Throwable e) {
                exceptionManager.handle(e);
            }
        }, delay, period, unit));
    }

    protected void addLoop(Runnable task, long delay, long period, TimeUnit unit) {
        addLoop(task, delay, period, unit, ExceptionManager.PRINT);
    }

    protected void addTask(Consumer<? super ScheduledExecutorService> task, ExceptionManager exceptionManager) {
        taskQueue.add(executor -> {
            try {
                task.accept(executor);
            } catch (Throwable e) {
                exceptionManager.handle(e);
            }
        });
    }
    protected void addTask(Consumer<? super ScheduledExecutorService> task) {
        addTask(task, ExceptionManager.PRINT);
    }

    public boolean isRunning() {
        return isRunning;
    }
    protected App app() {
        return app;
    }
}
