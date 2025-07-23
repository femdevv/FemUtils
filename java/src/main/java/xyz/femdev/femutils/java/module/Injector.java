package xyz.femdev.femutils.java.module;

/**
 * Simple DI abstraction. Users may provide their own implementation
 * (e.g., Guice, Dagger) or fall back to the provided DefaultInjector.
 */
public interface Injector {
    /**
     * Obtain an instance of the given type.
     *
     * @param type The class to instantiate or retrieve.
     * @param <T>  The type.
     * @return An instance of T.
     * @throws ModuleInstantiationException on failure.
     */
    <T> T getInstance(Class<T> type) throws ModuleInstantiationException;
}
