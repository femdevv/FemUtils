package xyz.femdev.femutils.java.profiler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Central API for starting and managing profiling sections.
 * <p>
 * Each thread profiles independently.
 * Provides methods to start sections, retrieve results, and reset state.
 * </p>
 */
public final class Profiler {
    private Profiler() {
    }

    // stack of active ProfilerSection instances
    private static final ThreadLocal<Deque<ProfilerSection>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    // registry of root sections per thread ID
    private static final ConcurrentMap<Long, List<ProfilerSection>> ROOT_SECTIONS = new ConcurrentHashMap<>();

    /**
     * Start a new profiling section with the given name.
     * <p>
     * Returned ProfilerSection implements AutoCloseable, use it in a try-with-resources
     * block. When closed, the section's end time is recorded and it is popped from the stack.
     * </p>
     *
     * @param name a human-readable identifier for the section
     * @return a new ProfilerSection representing the started section
     */
    public static ProfilerSection start(String name) {
        ProfilerSection parent = STACK.get().peek();
        ProfilerSection section = new ProfilerSection(name, parent);
        STACK.get().push(section);

        if (parent == null) {
            ROOT_SECTIONS
                    .computeIfAbsent(Thread.currentThread().threadId(), id -> new ArrayList<>())
                    .add(section);
        } else {
            parent.addChild(section);
        }

        section.markStart();
        return section;
    }

    /**
     * Retrieve all top-level profiling sections recorded on the current thread.
     * <p>
     * Use this to obtain completed root sections for reporting or analysis.
     * </p>
     *
     * @return an immutable list of root ProfilerSection instances for this thread
     */
    public static List<ProfilerSection> getRootsForCurrentThread() {
        return ROOT_SECTIONS.getOrDefault(Thread.currentThread().threadId(), List.of());
    }

    /**
     * Clear all profiling data and active stacks for all threads.
     * <p>
     * After calling, no sections remain recorded. Useful for resetting between runs.
     * </p>
     */
    public static void reset() {
        STACK.get().clear();
        ROOT_SECTIONS.clear();
    }

    static void pop(ProfilerSection section) {
        Deque<ProfilerSection> stack = STACK.get();
        if (stack.peek() != section) {
            throw new IllegalStateException(
                    "Profiling sections must be closed in LIFO order. Expected "
                            + section.getName() + " but found "
                            + (stack.peek() != null ? stack.peek().getName() : "none")
            );
        }
        stack.pop();
    }
}
