package xyz.femdev.femutils.paper.gui;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Represents a clickable button in a GUI with an associated item and click handler.
 */
public final class Button {
    private final ItemStack item;
    private final ClickHandler onClick;

    private Button(ItemStack item, ClickHandler onClick) {
        this.item = Objects.requireNonNull(item);
        this.onClick = Objects.requireNonNull(onClick);
    }

    /**
     * Creates a button with a click handler.
     */
    public static Button of(ItemStack item, ClickHandler handler) {
        return new Button(item, handler);
    }

    /**
     * Creates a button that does nothing when clicked.
     */
    public static Button noop(ItemStack item) {
        return new Button(item, ctx -> {
        });
    }

    public ItemStack item() {
        return item;
    }

    public ClickHandler onClick() {
        return onClick;
    }
}
