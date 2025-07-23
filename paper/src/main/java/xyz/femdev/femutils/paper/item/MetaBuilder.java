package xyz.femdev.femutils.paper.item;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent wrapper for editing a specific {@link ItemMeta} type.
 *
 * @param <M> the type of meta being edited
 */
public final class MetaBuilder<M extends ItemMeta> {

    private final M meta;

    private MetaBuilder(M meta) {
        this.meta = meta;
    }

    /**
     * Creates a builder for the given meta.
     */
    public static <M extends ItemMeta> MetaBuilder<M> of(M meta) {
        return new MetaBuilder<>(meta);
    }

    public MetaBuilder<M> displayName(Component name) {
        meta.displayName(name);
        return this;
    }

    public MetaBuilder<M> lore(List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    public MetaBuilder<M> customModelData(Integer data) {
        meta.setCustomModelData(data);
        return this;
    }

    public MetaBuilder<M> unbreakable(boolean b) {
        meta.setUnbreakable(b);
        return this;
    }

    /**
     * Allows custom meta editing via lambda.
     */
    public MetaBuilder<M> edit(Consumer<M> consumer) {
        consumer.accept(meta);
        return this;
    }

    /**
     * Returns the modified {@link ItemMeta}.
     */
    public M build() {
        return meta;
    }
}
