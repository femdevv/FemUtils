package xyz.femdev.femutils.java.module;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Provides modules with common services such as DI, logging, configuration, etc.
 */
public final class ModuleContext {

    private final String moduleName;
    private final Injector injector;
    private final Logger logger;

    public ModuleContext(String moduleName, Injector injector, Logger logger) {
        this.moduleName = Objects.requireNonNull(moduleName, "moduleName");
        this.injector = Objects.requireNonNull(injector, "injector");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    /**
     * @return The unique name of the module.
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Retrieve an instance of the given type, optionally constructed via DI.
     *
     * @param clazz The class to instantiate or fetch.
     * @param <T>   Type parameter.
     * @return An instance of T.
     */
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    /**
     * @return A java.util.Logger scoped to this module.
     */
    public Logger getLogger() {
        return logger;
    }
}
