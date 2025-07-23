package xyz.femdev.femutils.paper.tasks;

import java.util.function.Function;

/**
 * Internal representation of a step in a {@link TaskChain}.
 * Can be a sync/async function or a delay.
 */
final class ChainStep {
    enum Type {
        SYNC_FUNC,
        ASYNC_FUNC,
        DELAY_TICKS
    }

    private final Type type;
    private final Function<Object, Object> syncFn;
    private final Function<Object, Object> asyncFn;
    private final long delayTicks;

    private ChainStep(Type type, Function<Object, Object> syncFn, Function<Object, Object> asyncFn, long delayTicks) {
        this.type = type;
        this.syncFn = syncFn;
        this.asyncFn = asyncFn;
        this.delayTicks = delayTicks;
    }

    static ChainStep syncFunc(Function<Object, Object> fn) {
        return new ChainStep(Type.SYNC_FUNC, fn, null, 0);
    }

    static ChainStep asyncFunc(Function<Object, Object> fn) {
        return new ChainStep(Type.ASYNC_FUNC, null, fn, 0);
    }

    static ChainStep delayTicks(long ticks) {
        return new ChainStep(Type.DELAY_TICKS, null, null, ticks);
    }

    Type type() {
        return type;
    }

    Function<Object, Object> syncFn() {
        return syncFn;
    }

    Function<Object, Object> asyncFn() {
        return asyncFn;
    }

    long delayTicks() {
        return delayTicks;
    }
}
