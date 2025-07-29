package xyz.femdev.femutils.java.profiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for exporting ProfilerReport instances.
 */
public final class ProfilerExporters {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private ProfilerExporters() {
    }

    /**
     * Serialize a ProfilerReport to a JSON string.
     *
     * @param report the report to serialize
     * @return pretty-printed JSON
     */
    public static String toJson(ProfilerReport report) {
        return GSON.toJson(report);
    }

    /**
     * Write a ProfilerReport out to a file in JSON format.
     *
     * @param report the report to write
     * @param path   the file path
     * @throws IOException if writing fails
     */
    public static void toJsonFile(ProfilerReport report, Path path) throws IOException {
        String json = toJson(report);
        Files.writeString(path, json);
    }
}
