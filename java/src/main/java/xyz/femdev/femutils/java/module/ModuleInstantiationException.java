package xyz.femdev.femutils.java.module;

/**
 * Thrown when a module or dependency cannot be instantiated by DI.
 */
public class ModuleInstantiationException extends RuntimeException {
    public ModuleInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
