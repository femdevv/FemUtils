package xyz.femdev.femutils.java.profiler;

import java.time.Instant;
import java.util.List;

/**
 * Immutable snapshot of profiling data at a point in time.
 *
 * @param timestamp epoch milliseconds when the snapshot was taken
 * @param roots     list of root sections
 */
public record ProfilerReport(long timestamp, List<ProfilerReportSection> roots) {

    /**
     * Capture a ProfilerReport of the current thread's root sections.
     *
     * @return a fresh ProfilerReport
     */
    public static ProfilerReport capture() {
        long now = Instant.now().toEpochMilli();
        var roots = Profiler.getRootsForCurrentThread().stream()
                .map(ProfilerReportSection::fromSection)
                .toList();
        return new ProfilerReport(now, roots);
    }
}
