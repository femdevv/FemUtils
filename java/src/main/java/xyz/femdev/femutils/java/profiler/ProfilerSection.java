package xyz.femdev.femutils.java.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a timed section of code, supports nesting.
 * Implements AutoCloseable so that closing marks end.
 */
public final class ProfilerSection implements AutoCloseable {

    private final String name;
    private final ProfilerSection parent;
    private final List<ProfilerSection> children = new ArrayList<>();

    private long startTime;
    private long endTime;
    private boolean closed = false;

    ProfilerSection(String name, ProfilerSection parent) {
        this.name = name;
        this.parent = parent;
    }

    void markStart() {
        this.startTime = System.nanoTime();
    }

    void markEnd() {
        this.endTime = System.nanoTime();
    }

    /**
     * Add a nested child section.
     */
    void addChild(ProfilerSection child) {
        children.add(child);
    }

    /**
     * Close this section, record its end time, and pop it from the stack.
     */
    @Override
    public void close() {
        if (closed) return;
        markEnd();
        closed = true;
        Profiler.pop(this);
    }

    /**
     * @return section name
     */
    public String getName() {
        return name;
    }

    /**
     * @return elapsed time in nanoseconds
     */
    public long getElapsedNanos() {
        if (!closed) {
            throw new IllegalStateException("Section has not been closed yet");
        }
        return endTime - startTime;
    }

    /**
     * @return unmodifiable view of children
     */
    public List<ProfilerSection> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * @return parent section, or null if root
     */
    public ProfilerSection getParent() {
        return parent;
    }

    @Override
    public String toString() {
        long elapsed = closed ? getElapsedNanos() : (System.nanoTime() - startTime);
        return String.format("%s: %.3f ms", name, elapsed / 1_000_000.0);
    }
}
