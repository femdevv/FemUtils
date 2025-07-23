package xyz.femdev.femutils.java.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry for type-specific serializers used by a config engine.
 */
public final class TypeRegistry {
    private final Map<Class<?>, TypeSerializer<?>> exact = new LinkedHashMap<>();

    /**
     * Registers a serializer for the given type.
     */
    public <T> void register(Class<T> type, TypeSerializer<T> ser) {
        exact.put(type, ser);
    }

    /**
     * Finds a matching serializer for the given type.
     * Supports exact and assignable matches.
     */
    @SuppressWarnings("unchecked")
    public <T> TypeSerializer<T> find(Class<T> type) {
        TypeSerializer<?> ser = exact.get(type);
        if (ser != null) return (TypeSerializer<T>) ser;

        for (Map.Entry<Class<?>, TypeSerializer<?>> e : exact.entrySet()) {
            if (e.getKey().isAssignableFrom(type)) {
                return (TypeSerializer<T>) e.getValue();
            }
        }
        return null;
    }
}
