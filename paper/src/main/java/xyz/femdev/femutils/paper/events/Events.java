package xyz.femdev.femutils.paper.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Utility for subscribing to and awaiting Bukkit events.
 * Provides CompletableFuture-based waiting and simple subscriptions.
 */
public final class Events {

    private final Plugin plugin;

    /**
     * @param plugin the plugin using this utility
     */
    public Events(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Waits for a single occurrence of the given event type, optionally filtered and with a timeout.
     *
     * @param type         the event class
     * @param filter       optional filter, may be null
     * @param timeoutTicks ticks until timeout (0 = no timeout)
     * @param <T>          event type
     * @return a future that completes when the event is received
     */
    public <T extends Event> CompletableFuture<T> waitFor(Class<T> type,
                                                          Predicate<T> filter,
                                                          long timeoutTicks) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Listener listener = new Listener() {
        };
        EventExecutor exec = (l, e) -> {
            T ev = type.cast(e);
            if (filter == null || filter.test(ev)) {
                future.complete(ev);
                HandlerList.unregisterAll(listener);
            }
        };

        Bukkit.getPluginManager().registerEvent(type, listener, EventPriority.NORMAL, exec, plugin, true);

        if (timeoutTicks > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!future.isDone()) {
                    future.completeExceptionally(new TimeoutException("Event " + type.getSimpleName() + " timed out"));
                    HandlerList.unregisterAll(listener);
                }
            }, timeoutTicks);
        }

        return future;
    }

    /**
     * Subscribes to an event with full control over priority, cancellation, and filtering.
     *
     * @param type            the event class
     * @param priority        event priority
     * @param ignoreCancelled whether to ignore cancelled events
     * @param filter          optional filter, may be null
     * @param handler         the consumer to handle the event
     * @param <T>             event type
     * @return an {@link EventSubscription} that can be used to unsubscribe
     */
    public <T extends Event> EventSubscription<T> subscribe(Class<T> type,
                                                            EventPriority priority,
                                                            boolean ignoreCancelled,
                                                            Predicate<T> filter,
                                                            Consumer<T> handler) {
        Listener listener = new Listener() {
        };
        EventExecutor exec = (l, e) -> {
            T ev = type.cast(e);
            if (filter == null || filter.test(ev)) handler.accept(ev);
        };
        Bukkit.getPluginManager().registerEvent(type, listener, priority, exec, plugin, ignoreCancelled);
        return new EventSubscription<>(listener);
    }

    /**
     * Subscribes to an event with default settings (normal priority, ignoreCancelled = true).
     */
    public <T extends Event> EventSubscription<T> subscribe(Class<T> type, Consumer<T> handler) {
        return subscribe(type, EventPriority.NORMAL, true, null, handler);
    }

    /**
     * Waits for the next occurrence of the given event type (no filter, no timeout).
     */
    public <T extends Event> CompletableFuture<T> waitFor(Class<T> type) {
        return waitFor(type, null, 0);
    }
}
