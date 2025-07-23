package xyz.femdev.femutils.paper.tasks;

import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Provides helpers for async/sync execution and task chains.
 */
public final class Tasks {

    private final Plugin plugin;

    /**
     * @param plugin your plugin instance
     */
    public Tasks(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Creates a new {@link TaskChain} bound to this plugin.
     */
    public TaskChain chain() {
        return new TaskChain(plugin);
    }

    /**
     * Returns an async {@link Executor} using the Bukkit scheduler.
     */
    public Executor asyncExecutor() {
        return r -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, r);
    }

    /**
     * Returns a sync {@link Executor} using the Bukkit scheduler.
     */
    public Executor syncExecutor() {
        return r -> plugin.getServer().getScheduler().runTask(plugin, r);
    }
}
