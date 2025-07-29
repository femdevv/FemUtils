// File: xyz/femdev/femutils/paper/profiler/PaperProfilerRenderer.java
package xyz.femdev.femutils.paper.profiler;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import xyz.femdev.femutils.java.profiler.ProfilerReport;
import xyz.femdev.femutils.java.profiler.ProfilerReportSection;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Sends a ProfilerReport to a player or console.
 */
public class PaperProfilerRenderer {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private final CommandSender sender;
    private final String prefix;

    /**
     * @param sender the recipient (player or console)
     * @param prefix a chat‐prefix for each line (e.g. "&7[Profiler]&r ")
     */
    public PaperProfilerRenderer(CommandSender sender, String prefix) {
        this.sender = sender;
        this.prefix = prefix;
    }

    /**
     * Render the report with timestamp header, each section and its children.
     */
    public void render(ProfilerReport report) {
        sender.sendMessage(MM.deserialize(prefix + "<gray>Profiler Report @ " + TIME_FMT.format(Instant.ofEpochMilli(report.timestamp())) + "</gray>"));
        renderSections(report.roots(), 0);
    }

    private void renderSections(java.util.List<ProfilerReportSection> sections, int indent) {
        for (var sec : sections) {
            sendSectionLine(sec, indent);
            renderSections(sec.children(), indent + 1);
        }
    }

    private void sendSectionLine(ProfilerReportSection sec, int indent) {
        double ms = sec.elapsedNanos() / 1_000_000.0;
        String indentSpaces = "  ".repeat(indent);
        String msg = prefix
                + "<yellow>" + indentSpaces + sec.name() + ": "
                + String.format("%.3f", ms) + " ms</yellow>";
        sender.sendMessage(MM.deserialize(msg));
    }
}
