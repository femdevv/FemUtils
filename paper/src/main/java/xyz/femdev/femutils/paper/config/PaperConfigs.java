package xyz.femdev.femutils.paper.config;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.femdev.femutils.java.config.ConfigHandle;
import xyz.femdev.femutils.java.config.TypeRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility for managing plugin configuration files.
 */
public final class PaperConfigs {
    private final JavaPlugin plugin;
    private final TypeRegistry registry = new TypeRegistry();
    private final SimpleYamlEngine engine;

    /**
     * @param plugin the plugin using this config system
     */
    public PaperConfigs(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        PaperSerializers.registerAll(registry);
        this.engine = new SimpleYamlEngine(registry);
    }

    /**
     * @return the shared type registry for serializers
     */
    public TypeRegistry registry() {
        return registry;
    }

    /**
     * Creates and loads a config file with the given path and type.
     * If the file does not exist, default values will be saved.
     *
     * @param fileName config file name (relative to plugin folder)
     * @param type     class of the config model
     * @param defaults supplier for default instance
     */
    public <T> ConfigHandle<T> create(String fileName, Class<T> type, Supplier<T> defaults) throws IOException {
        Path path = plugin.getDataFolder().toPath().resolve(fileName);
        if (Files.notExists(path)) {
            plugin.getDataFolder().mkdirs();
            try (InputStream in = plugin.getResource(fileName)) {
                if (in != null) Files.copy(in, path);
            }
        }
        return new ConfigHandle<>(path, type, defaults, engine);
    }
}
