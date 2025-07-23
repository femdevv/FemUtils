package xyz.femdev.femutils.paper.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Task chain system for running logic across sync and async steps.
 * Supports delays, exception handling, and result passing between steps.
 */
public final class TaskChain {

    private final Plugin plugin;
    private final Deque<ChainStep> steps = new ArrayDeque<>();
    private Consumer<Throwable> errorHandler = Throwable::printStackTrace;
    private Runnable finallyHandler = () -> {
    };
    private boolean started;

    TaskChain(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Adds an async step that supplies a value.
     */
    public <T> TaskChain async(Callable<T> supplier) {
        steps.addLast(ChainStep.asyncFunc(prev -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        return this;
    }

    /**
     * Adds an async step that runs a task with no return value.
     */
    public TaskChain asyncRun(Runnable run) {
        steps.addLast(ChainStep.asyncFunc(prev -> {
            run.run();
            return prev;
        }));
        return this;
    }

    /**
     * Adds a sync function step, using the result of the previous step.
     */
    public <T> TaskChain thenSync(Function<Object, T> fn) {
        steps.addLast(ChainStep.syncFunc(fn::apply));
        return this;
    }

    /**
     * Adds a sync step that consumes the result, without modifying it.
     */
    public TaskChain thenSyncRun(Consumer<Object> consumer) {
        steps.addLast(ChainStep.syncFunc(prev -> {
            consumer.accept(prev);
            return prev;
        }));
        return this;
    }

    /**
     * Inserts a delay before the next step.
     */
    public TaskChain delayTicks(long ticks) {
        steps.addLast(ChainStep.delayTicks(ticks));
        return this;
    }

    /**
     * Sets an initial value to start the chain with.
     */
    public TaskChain supply(Object value) {
        steps.addFirst(ChainStep.syncFunc(prev -> value));
        return this;
    }

    /**
     * Sets a handler for exceptions thrown during the chain.
     */
    public TaskChain onError(Consumer<Throwable> handler) {
        this.errorHandler = handler;
        return this;
    }

    /**
     * Sets a final action to always run at the end of the chain.
     */
    public TaskChain onFinally(Runnable r) {
        this.finallyHandler = r;
        return this;
    }

    /**
     * Starts execution of the task chain.
     */
    public void execute() {
        if (started) throw new IllegalStateException("TaskChain already executed");
        started = true;
        runNext(null);
    }

    private void runNext(Object prevResult) {
        ChainStep step = steps.pollFirst();
        if (step == null) {
            finish();
            return;
        }

        switch (step.type()) {
            case SYNC_FUNC -> {
                try {
                    Object out = step.syncFn().apply(prevResult);
                    runNext(out);
                } catch (Throwable t) {
                    handleError(t);
                }
            }
            case ASYNC_FUNC -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    Object out = step.asyncFn().apply(prevResult);
                    Bukkit.getScheduler().runTask(plugin, () -> runNext(out));
                } catch (Throwable t) {
                    Bukkit.getScheduler().runTask(plugin, () -> handleError(t));
                }
            });
            case DELAY_TICKS ->
                    Bukkit.getScheduler().runTaskLater(plugin, () -> runNext(prevResult), step.delayTicks());
        }
    }

    private void handleError(Throwable t) {
        try {
            errorHandler.accept(t);
        } finally {
            finish();
        }
    }

    private void finish() {
        try {
            finallyHandler.run();
        } catch (Throwable t) {
            errorHandler.accept(t);
        }
    }
}
