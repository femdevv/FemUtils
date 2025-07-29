package xyz.femdev.femutils.java.profiler;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Default console implementation of ProfilerRenderer.
 */
public class ConsoleProfilerRenderer implements ProfilerRenderer {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    @Override
    public void render(ProfilerReport report, PrintStream out) {
        out.println("Profiler Report @ " + TIME_FMT.format(Instant.ofEpochMilli(report.timestamp())));
        out.println("------------------------------------------------------------");
        out.printf("%-40s %12s%n", "Section", "Duration(ms)");
        out.println("------------------------------------------------------------");
        for (var root : report.roots()) {
            renderSection(root, out, 0);
        }
        out.println("------------------------------------------------------------");
    }

    private void renderSection(ProfilerReportSection section, PrintStream out, int indent) {
        String indentStr = "  ".repeat(indent);
        double ms = section.elapsedNanos() / 1_000_000.0;
        out.printf("%-40s %12.3f%n", indentStr + section.name(), ms);
        for (var child : section.children()) {
            renderSection(child, out, indent + 1);
        }
    }
}
