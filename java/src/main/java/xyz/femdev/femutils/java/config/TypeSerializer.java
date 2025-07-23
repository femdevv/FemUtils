package xyz.femdev.femutils.java.config;

/**
 * Interface for custom serializers that convert between raw data and typed objects.
 *
 * @param <T> the target type to serialize/deserialize
 */
public interface TypeSerializer<T> {

    /**
     * Converts raw data (from config or tree) into a typed object.
     *
     * @param raw  raw input value
     * @param ctx  mapping context
     * @param type target class
     * @return the deserialized value
     */
    T deserialize(Object raw, ReflectMapper ctx, Class<T> type);

    /**
     * Converts a typed object to a raw value (e.g., for saving to config).
     *
     * @param value the value to serialize
     * @param ctx   mapping context
     * @return the raw tree representation
     */
    Object serialize(T value, ReflectMapper ctx);
}
