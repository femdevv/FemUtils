package xyz.femdev.femutils.paper.item;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Fluent builder for setting values in a {@link PersistentDataContainer}.
 */
public final class PdcBuilder {

    private final PersistentDataContainer pdc;

    PdcBuilder(PersistentDataContainer pdc) {
        this.pdc = pdc;
    }

    public PdcBuilder string(Plugin plugin, String key, String value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.STRING, value);
        return this;
    }

    public PdcBuilder intVal(Plugin plugin, String key, int value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.INTEGER, value);
        return this;
    }

    public PdcBuilder longVal(Plugin plugin, String key, long value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.LONG, value);
        return this;
    }

    public PdcBuilder floatVal(Plugin plugin, String key, float value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.FLOAT, value);
        return this;
    }

    public PdcBuilder doubleVal(Plugin plugin, String key, double value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.DOUBLE, value);
        return this;
    }

    public PdcBuilder byteArray(Plugin plugin, String key, byte[] value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.BYTE_ARRAY, value);
        return this;
    }

    public PdcBuilder intArray(Plugin plugin, String key, int[] value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.INTEGER_ARRAY, value);
        return this;
    }

    public PdcBuilder bool(Plugin plugin, String key, boolean value) {
        pdc.set(ItemBuilder.key(plugin, key), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
        return this;
    }

    public PdcBuilder remove(Plugin plugin, String key) {
        pdc.remove(ItemBuilder.key(plugin, key));
        return this;
    }

    /**
     * Sets a raw value using any {@link PersistentDataType}.
     */
    public PdcBuilder raw(NamespacedKey key, PersistentDataType<?, ?> type, Object value) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        PersistentDataType<Object, Object> t = (PersistentDataType) type;
        pdc.set(key, t, value);
        return this;
    }
}
