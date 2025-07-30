package xyz.femdev.femutils.java.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manages loading, saving, and watching a config object.
 *
 * @param <T> the config data type
 */
public final class ConfigHandle<T> {

    private final Path path;
    private final Class<T> type;
    private final Supplier<T> defaults;
    private final Engine engine;
    private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();
    private volatile T value;

    /**
     * Creates a config handle for the given file and type.
     *
     * @param path     config file path
     * @param type     config class
     * @param defaults fallback supplier if the config is missing or invalid
     * @param engine   serialization backend
     */
    public ConfigHandle(Path path, Class<T> type, Supplier<T> defaults, Engine engine) throws IOException {
        this.path = path;
        this.type = type;
        this.defaults = defaults;
        this.engine = engine;
        this.value = engine.load(path, type, defaults);
    }

    /**
     * @return the current in-memory config value
     */
    public T get() {
        return value;
    }

    /**
     * Reloads the config from disk and notifies listeners.
     */
    public void reload() throws IOException {
        T newVal = engine.load(path, type, defaults);
        this.value = newVal;
        for (Consumer<T> l : listeners) l.accept(newVal);
    }

    /**
     * Saves the current config to disk.
     */
    public void save() throws IOException {
        engine.save(path, value);
    }

    /**
     * Sets the config to a new value and saves it.
     */
    public void setAndSave(T newValue) throws IOException {
        this.value = newValue;
        save();
    }

    /**
     * Registers a listener that runs when the config is reloaded.
     *
     * @param listener handler for updated config values
     */
    public void onReload(Consumer<T> listener) {
        listeners.add(listener);
    }

    /**
     * @return the config file path
     */
    public Path path() {
        return path;
    }

    /**
     * Backend interface for loading and saving config data.
     */
    public interface Engine {
        <T> T load(Path path, Class<T> type, Supplier<T> defaults) throws IOException;
        void save(Path path, Object value) throws IOException;
    }
}
