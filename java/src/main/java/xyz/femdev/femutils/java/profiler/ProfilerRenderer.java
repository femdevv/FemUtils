package xyz.femdev.femutils.java.profiler;

import java.io.PrintStream;

/**
 * Renders a ProfilerReport to a text table.
 */
public interface ProfilerRenderer {
    /**
     * Render the given report to the provided PrintStream.
     *
     * @param report the snapshot to render
     * @param out the output stream (e.g. System.out)
     */
    void render(ProfilerReport report, PrintStream out);
}
