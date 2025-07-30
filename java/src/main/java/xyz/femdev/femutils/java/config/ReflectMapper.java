package xyz.femdev.femutils.java.config;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Maps objects to and from generic trees (maps/lists), using reflection.
 * Supports records, POJOs, and custom serializers via {@link TypeRegistry}.
 */
public final class ReflectMapper {

    private final TypeRegistry registry;

    /**
     * @param registry the type registry for custom serializers
     */
    public ReflectMapper(TypeRegistry registry) {
        this.registry = registry;
    }

    /**
     * Converts raw data into an object of the given type.
     */
    public <T> T toObject(Object raw, Class<T> type) {
        if (raw == null) return null;

        if (type.isPrimitive()
                || type == Boolean.class
                || type == Byte.class
                || type == Short.class
                || type == Integer.class
                || type == Long.class
                || type == Float.class
                || type == Double.class
                || type == Character.class
                || type == String.class
        ) {
            //noinspection unchecked
            return (T) raw;
        }

        TypeSerializer<T> ser = registry.find(type);
        if (ser != null) return ser.deserialize(raw, this, type);

        if (List.class.isAssignableFrom(type)) {
            //noinspection unchecked
            return (T) list(raw, Object.class);
        }
        if (Map.class.isAssignableFrom(type)) {
            //noinspection unchecked
            return (T) map(raw, Object.class, Object.class);
        }
        if (type.isRecord()) {
            return fromRecord(raw, type);
        }
        return fromPojo(raw, type);
    }

    /**
     * Converts an object to a tree-like structure (map or list) suitable for serialization.
     */
    public Object toTree(Object obj) {
        if (obj == null) return null;

        @SuppressWarnings("unchecked")
        Class<Object> type = (Class<Object>) obj.getClass();
        TypeSerializer<Object> ser = registry.find(type);
        if (ser != null) return ser.serialize(obj, this);

        if (type.isRecord()) return recordToMap(obj);
        return pojoToMap(obj);
    }

    private <T> T fromRecord(Object raw, Class<T> type) {
        if (!(raw instanceof Map<?, ?> map))
            throw new IllegalArgumentException("Expected map for " + type.getName());

        RecordComponent[] comps = type.getRecordComponents();
        Object[] args = new Object[comps.length];
        for (int i = 0; i < comps.length; i++) {
            var rc = comps[i];
            Object rawVal = map.get(rc.getName());
            Class<?> compType = rc.getType();
            Object converted;

            if (List.class.isAssignableFrom(compType)) {
                Type generic = rc.getGenericType();
                Class<?> elemType = Object.class;
                if (generic instanceof ParameterizedType pt) {
                    Type arg = pt.getActualTypeArguments()[0];
                    if (arg instanceof Class<?> c) elemType = c;
                }
                converted = list(rawVal, elemType);

            } else if (Map.class.isAssignableFrom(compType)) {
                Type generic = rc.getGenericType();
                Class<?> keyType = Object.class, valType = Object.class;
                if (generic instanceof ParameterizedType pt) {
                    Type[] args0 = pt.getActualTypeArguments();
                    if (args0[0] instanceof Class<?> ck) keyType = ck;
                    if (args0[1] instanceof Class<?> cv) valType = cv;
                }
                converted = map(rawVal, keyType, valType);

            } else {
                converted = toObject(rawVal, compType);
            }

            args[i] = converted;
        }

        try {
            var ctor = type.getDeclaredConstructor(
                    Arrays.stream(comps).map(RecordComponent::getType).toArray(Class[]::new)
            );
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object recordToMap(Object obj) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (var rc : obj.getClass().getRecordComponents()) {
            try {
                Object val = rc.getAccessor().invoke(obj);
                out.put(rc.getName(), toTree(val));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return out;
    }

    private <T> T fromPojo(Object raw, Class<T> type) {
        if (!(raw instanceof Map<?, ?> map))
            throw new IllegalArgumentException("Expected map for " + type.getName());

        T instance;
        try {
            var ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            instance = ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("No no-arg constructor for " + type.getName(), e);
        }

        for (var f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            Object val = toObject(map.get(f.getName()), f.getType());
            try {
                f.setAccessible(true);
                f.set(instance, val);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private Object pojoToMap(Object obj) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (var f : obj.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            try {
                f.setAccessible(true);
                out.put(f.getName(), toTree(f.get(obj)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return out;
    }

    /**
     * Converts a raw list to a typed Java {@link List}.
     */
    public <T> List<T> list(Object raw, Class<T> elementType) {
        if (raw == null) return List.of();
        if (!(raw instanceof List<?> rawList)) throw new IllegalArgumentException("Expected list");

        List<T> out = new ArrayList<>(rawList.size());
        for (Object o : rawList) out.add(toObject(o, elementType));
        return out;
    }

    /**
     * Converts a raw map to a typed Java {@link Map}.
     */
    public <K, V> Map<K, V> map(Object raw, Class<K> keyType, Class<V> valType) {
        if (raw == null) return Map.of();
        if (!(raw instanceof Map<?, ?> rawMap)) throw new IllegalArgumentException("Expected map");

        Map<K, V> out = new LinkedHashMap<>();
        for (var e : rawMap.entrySet()) {
            K k = toObject(e.getKey(), keyType);
            V v = toObject(e.getValue(), valType);
            out.put(k, v);
        }
        return out;
    }
}
