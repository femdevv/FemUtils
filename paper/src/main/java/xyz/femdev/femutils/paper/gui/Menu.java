package xyz.femdev.femutils.paper.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for a GUI menu.
 * Handles button registration, animation, and click handling.
 */
public abstract class Menu {

    protected final Component title;
    protected final int rows;

    protected final Map<Integer, Button> buttons = new HashMap<>();
    protected final Map<Integer, AnimatedItem> animations = new HashMap<>();

    protected Inventory inventory;
    private Plugin plugin;
    private int animTaskId = -1;

    protected Menu(Component title, int rows) {
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("Rows must be 1–6");
        this.title = title;
        this.rows = rows;
    }

    /**
     * Called to build the GUI contents before opening.
     */
    protected abstract void build(Player player);

    /**
     * Called when the GUI is closed by the player.
     */
    protected void onClose(Player player) {
    }

    protected void setButton(int slot, Button button) {
        buttons.put(slot, button);
    }

    protected void animate(int slot, AnimatedItem anim) {
        animations.put(slot, anim);
        if (!buttons.containsKey(slot)) {
            buttons.put(slot, Button.noop(anim.current()));
        }
    }

    protected void clearButtons() {
        buttons.clear();
        animations.clear();
    }

    protected void redraw() {
        if (inventory == null) return;
        inventory.clear();
        buttons.forEach((s, b) -> inventory.setItem(s, b.item()));
    }

    void handleClick(ClickContext ctx) {
        Button btn = buttons.get(ctx.slot());
        if (btn != null) btn.onClick().handle(ctx);
    }

    final void open(Player player, GuiManager mgr) {
        this.plugin = mgr.plugin();
        inventory = Bukkit.createInventory(player, rows * 9, title);
        mgr.register(this, inventory);
        build(player);
        redraw();
        startAnimTaskIfNeeded();
        player.openInventory(inventory);
    }

    final void internalClose(Player p) {
        stopAnimTask();
        onClose(p);
    }

    private void startAnimTaskIfNeeded() {
        if (animations.isEmpty()) return;
        animTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (inventory == null) return;
            animations.forEach((slot, anim) -> {
                if (anim.advance()) {
                    inventory.setItem(slot, anim.current());
                }
            });
        }, 1L, 1L);
    }

    private void stopAnimTask() {
        if (animTaskId != -1) {
            Bukkit.getScheduler().cancelTask(animTaskId);
            animTaskId = -1;
        }
    }
}
