package xyz.femdev.femutils.paper.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Manages GUI menus and their inventories for a plugin.
 */
public final class GuiManager {

    private final Plugin plugin;
    private final Map<Inventory, Menu> menus = new WeakHashMap<>();

    public GuiManager(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        plugin.getServer().getPluginManager().registerEvents(new MenuListener(this), plugin);
    }

    void register(Menu menu, Inventory inv) {
        menus.put(inv, menu);
    }

    Menu menuFor(Inventory inv) {
        return menus.get(inv);
    }

    public Plugin plugin() {
        return plugin;
    }

    /**
     * Opens the given menu for the player.
     */
    public void open(Player player, Menu menu) {
        menu.open(player, this);
    }
}
