package xyz.femdev.femutils.paper.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Internal listener for handling menu click and close events.
 */
final class MenuListener implements Listener {

    private final GuiManager mgr;

    MenuListener(GuiManager mgr) {
        this.mgr = mgr;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (e.getView().getTopInventory() != e.getClickedInventory()) return;

        var menu = mgr.menuFor(e.getView().getTopInventory());
        if (menu == null) return;

        e.setCancelled(true);

        int raw = e.getRawSlot();
        menu.handleClick(new ClickContext(p, e.getView().getTopInventory(), raw, e.getClick()));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        var menu = mgr.menuFor(e.getInventory());
        if (menu == null) return;
        if (e.getPlayer() instanceof Player p) menu.internalClose(p);
    }
}
