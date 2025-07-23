package xyz.femdev.femutils.java.module;

/**
 * Represents a self-contained unit of functionality with a well-defined lifecycle.
 */
public interface Module {

    /**
     * Called once when the module is being initialized.
     *
     * @param context Provides access to DI, logging, config, etc.
     */
    void init(ModuleContext context);

    /**
     * Called when the module should start its active behavior.
     * Guaranteed to be called after init().
     */
    void start();

    /**
     * Called when the module should stop and clean up resources.
     * After this, no other lifecycle methods will be called.
     */
    void stop();
}
