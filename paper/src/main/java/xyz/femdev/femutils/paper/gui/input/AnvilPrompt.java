package xyz.femdev.femutils.paper.gui.input;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Utility for prompting a player for text input using an anvil GUI.
 */
public final class AnvilPrompt {

    private final Plugin plugin;

    public AnvilPrompt(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin);
    }

    /**
     * Opens the anvil input GUI for a player.
     *
     * @param player     the player to prompt
     * @param title      GUI title
     * @param initialText text to pre-fill
     * @param onComplete called with the input string
     * @param onCancel   called if the GUI is closed without input
     */
    public void open(Player player, Component title, String initialText, Consumer<String> onComplete, Runnable onCancel) {
        Location loc = player.getLocation();
        AnvilView view = (AnvilView) player.openAnvil(loc, true);
        if (view == null) {
            if (onCancel != null) onCancel.run();
            return;
        }

        view.setTitle(PlainTextComponentSerializer.plainText().serialize(title));
        AnvilInventory inv = view.getTopInventory();

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.displayName(Component.text(initialText));
        paper.setItemMeta(meta);
        inv.setFirstItem(paper);

        AtomicBoolean finished = new AtomicBoolean(false);

        Listener listener = new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getView() != view) return;
                if (!(e.getWhoClicked() instanceof Player p) || !p.getUniqueId().equals(player.getUniqueId())) return;

                e.setCancelled(true);

                if (e.getRawSlot() == 2 && (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT)) {
                    if (finished.getAndSet(true)) return;
                    String text = "";
                    try {
                        text = view.getRenameText();
                    } catch (Throwable ignored) {
                        try {
                            text = inv.getRenameText();
                        } catch (Throwable ignored2) {
                        }
                    }
                    clear(inv);
                    close(p, this);
                    onComplete.accept(text == null ? "" : text);
                }
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getView() != view) return;
                if (!(e.getPlayer() instanceof Player p) || !p.getUniqueId().equals(player.getUniqueId())) return;
                HandlerList.unregisterAll(this);
                if (!finished.get()) {
                    clear(inv);
                    if (onCancel != null) onCancel.run();
                }
            }

            private void close(Player p, Listener self) {
                HandlerList.unregisterAll(self);
                p.closeInventory();
            }

            private void clear(AnvilInventory inv) {
                try {
                    inv.setFirstItem(null);
                    inv.setSecondItem(null);
                    inv.setResult(null);
                } catch (Throwable ignored) {
                    inv.clear();
                }
            }
        };

        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
