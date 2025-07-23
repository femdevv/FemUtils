package xyz.femdev.femutils.paper.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

/**
 * Provides context for a GUI click event.
 *
 * @param player    the clicking player
 * @param inventory the inventory being interacted with
 * @param slot      the clicked slot index
 * @param clickType the type of click performed
 */
public record ClickContext(Player player, Inventory inventory, int slot, ClickType clickType) {
}
