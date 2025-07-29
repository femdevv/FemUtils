package xyz.femdev.femutils.java.profiler;

import java.util.List;

/**
 * Data Transfer Object representing a single section in a ProfilerReport.
 *
 * @param name         the section name
 * @param elapsedNanos elapsed time in nanoseconds
 * @param children     nested child sections
 */
public record ProfilerReportSection(
        String name,
        long elapsedNanos,
        List<ProfilerReportSection> children
) {
    /**
     * Convert a live ProfilerSection into its immutable DTO form (recursive).
     */
    public static ProfilerReportSection fromSection(ProfilerSection section) {
        var childDtos = section.getChildren().stream()
                .map(ProfilerReportSection::fromSection)
                .toList();
        return new ProfilerReportSection(
                section.getName(),
                section.getElapsedNanos(),
                childDtos
        );
    }
}
