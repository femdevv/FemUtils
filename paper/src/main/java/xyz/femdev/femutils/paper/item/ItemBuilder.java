package xyz.femdev.femutils.paper.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for creating and customizing {@link ItemStack}s.
 * Supports Adventure components, unsafe enchantments, PDC, and meta editing.
 */
public final class ItemBuilder {

    private final ItemStack stack;

    private ItemBuilder(Material material, int amount) {
        this.stack = new ItemStack(material, amount);
    }

    /**
     * Creates a builder with the given material and amount 1.
     */
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    /**
     * Creates a builder with the given material and amount.
     */
    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder name(Component component) {
        return editMeta(meta -> meta.displayName(component));
    }

    public ItemBuilder nameLegacy(String legacy) {
        Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(legacy);
        return name(comp);
    }

    public ItemBuilder lore(List<Component> lore) {
        return editMeta(meta -> meta.lore(lore));
    }

    public ItemBuilder loreLines(Component... lines) {
        return lore(Arrays.asList(lines));
    }

    public ItemBuilder addLoreLines(Component... lines) {
        return editMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) lore = new ArrayList<>();
            lore.addAll(Arrays.asList(lines));
            meta.lore(lore);
        });
    }

    public ItemBuilder enchant(Enchantment ench, int level) {
        stack.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder enchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        if (ignoreLevelRestriction) {
            stack.addUnsafeEnchantment(ench, level);
        } else {
            stack.addEnchantment(ench, level);
        }
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags) {
        return editMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder unbreakable(boolean value) {
        return editMeta(meta -> meta.setUnbreakable(value));
    }

    public ItemBuilder customModelData(Integer data) {
        return editMeta(meta -> meta.setCustomModelData(data));
    }

    /**
     * Direct access to edit {@link ItemMeta}.
     */
    public ItemBuilder meta(Consumer<ItemMeta> consumer) {
        return editMeta(consumer);
    }

    /**
     * Type-safe meta editing for specific {@link ItemMeta} subtypes.
     */
    public <M extends ItemMeta> ItemBuilder meta(Class<M> type, Consumer<M> consumer) {
        return editMeta(meta -> {
            if (type.isInstance(meta)) {
                consumer.accept(type.cast(meta));
            }
        });
    }

    /**
     * Allows editing the item's persistent data container.
     */
    public ItemBuilder pdc(Consumer<PdcBuilder> consumer) {
        return editMeta(meta -> {
            PdcBuilder p = new PdcBuilder(meta.getPersistentDataContainer());
            consumer.accept(p);
        });
    }

    private ItemBuilder editMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = stack.getItemMeta();
        consumer.accept(meta);
        stack.setItemMeta(meta);
        return this;
    }

    /**
     * Builds and returns the final {@link ItemStack}.
     */
    public ItemStack build() {
        return stack.clone();
    }

    /**
     * Helper for creating {@link NamespacedKey}s.
     */
    public static NamespacedKey key(Plugin plugin, String key) {
        Objects.requireNonNull(plugin, "plugin");
        return new NamespacedKey(plugin, key);
    }
}
