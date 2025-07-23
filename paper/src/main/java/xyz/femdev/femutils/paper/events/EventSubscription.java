package xyz.femdev.femutils.paper.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Represents an active event subscription that can be cancelled.
 *
 * @param <T> the event type
 */
public final class EventSubscription<T> {
    private final Listener listener;

    EventSubscription(Listener listener) {
        this.listener = listener;
    }

    /**
     * Unsubscribes from the event.
     */
    public void unsubscribe() {
        HandlerList.unregisterAll(listener);
    }
}
