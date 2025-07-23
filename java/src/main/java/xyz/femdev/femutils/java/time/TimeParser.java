package xyz.femdev.femutils.java.time;

import xyz.femdev.femutils.java.core.Result;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for parsing human-readable duration strings into {@link Duration} objects.
 * <p>
 * Supported time units:
 * <ul>
 *     <li><code>s</code> - seconds</li>
 *     <li><code>m</code> - minutes</li>
 *     <li><code>h</code> - hours</li>
 *     <li><code>d</code> - days</li>
 *     <li><code>w</code> - weeks</li>
 * </ul>
 * Example: {@code "5m30s"} will be parsed into a duration of 5 minutes and 30 seconds.
 */
public final class TimeParser {
    private static final Pattern P = Pattern.compile("(\\d+)([smhdw])", Pattern.CASE_INSENSITIVE);

    private TimeParser() {
    }

    /**
     * Parses a time string into a {@link Duration}. Returns a {@link Result} which may contain
     * either the resulting duration or an error message.
     *
     * @param input the duration string to parse (e.g., "2h30m", "5d", "1w2d3h")
     * @return a {@code Result} containing the {@link Duration} if parsing succeeds, or an error {@code String} if it fails.
     */
    public static Result<Duration, String> parse(String input) {
        Matcher m = P.matcher(input);
        if (!m.find()) return Result.err("Invalid duration: " + input);
        m.reset();
        long seconds = 0;
        while (m.find()) {
            long n = Long.parseLong(m.group(1));
            switch (m.group(2).toLowerCase()) {
                case "s" -> seconds += n;
                case "m" -> seconds += n * 60;
                case "h" -> seconds += n * 3600;
                case "d" -> seconds += n * 86400;
                case "w" -> seconds += n * 604800;
            }
        }
        return Result.ok(Duration.ofSeconds(seconds));
    }
}
