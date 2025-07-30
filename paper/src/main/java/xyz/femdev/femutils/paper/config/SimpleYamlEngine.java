package xyz.femdev.femutils.paper.config;

import org.simpleyaml.configuration.file.YamlFile;
import xyz.femdev.femutils.java.config.*;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Config engine that reads and writes YAML using SimpleYAML and {@link ReflectMapper}.
 * Supports records, POJOs, and serializer injection.
 */
public final class SimpleYamlEngine implements ConfigHandle.Engine {

    private final ReflectMapper mapper;
    private final TypeRegistry registry;

    /**
     * @param registry type registry for custom serializers
     */
    public SimpleYamlEngine(TypeRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
        this.mapper = new ReflectMapper(registry);
    }

    @Override
    public <T> T load(Path path, Class<T> type, java.util.function.Supplier<T> defaults) throws IOException {
        YamlFile yaml = new YamlFile(path.toFile());

        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            yaml.createNewFile(true);
            T def = defaults.get();
            writeObject(yaml, "", def, type);
            yaml.save();
            return def;
        }

        try {
            yaml.load();
        } catch (Exception e) {
            throw new IOException("Failed to load YAML " + path + ": " + e.getMessage(), e);
        }

        var section = yaml.getConfigurationSection("");
        Object tree = section != null ? section.getMapValues(false) : new LinkedHashMap<>();
        T result = mapper.toObject(tree, type);
        writeObject(yaml, "", result, type);
        yaml.save();
        return result;
    }

    @Override
    public void save(Path path, Object value) throws IOException {
        YamlFile yaml = new YamlFile(path.toFile());
        try {
            yaml.load();
        } catch (Exception ignored) {
        }
        writeObject(yaml, "", value, value.getClass());
        yaml.save();
    }

    private void writeObject(YamlFile yaml, String basePath, Object obj, Class<?> type) {
        if (obj == null) return;

        if (basePath.isEmpty() && type.isAnnotationPresent(Header.class)) {
            yaml.setHeader(String.join("\n", type.getAnnotation(Header.class).value()));
        }

        if (registry.find(type) != null) {
            yaml.set(basePath, mapper.toTree(obj));
            return;
        }

        if (isLeafType(type)) {
            yaml.set(basePath, obj);
            return;
        }

        if (type.isRecord()) {
            for (var rc : type.getRecordComponents()) {
                Object val = getValue(rc, obj);
                String path = concat(basePath, rc.getName());
                commentIfPresent(yaml, path, rc);
                writeObject(yaml, path, val, rc.getType());
            }
            return;
        }

        for (var f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            try {
                f.setAccessible(true);
                Object val = f.get(obj);
                String path = concat(basePath, f.getName());
                commentIfPresent(yaml, path, f);
                writeObject(yaml, path, val, f.getType());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isLeafType(Class<?> type) {
        return type.isPrimitive()
                || String.class.equals(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class.equals(type)
                || Character.class.equals(type)
                || type.isEnum();
    }

    private Object getValue(java.lang.reflect.RecordComponent rc, Object obj) {
        try {
            return rc.getAccessor().invoke(obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void commentIfPresent(YamlFile yaml, String path, AnnotatedElement el) {
        Comment c = el.getAnnotation(Comment.class);
        if (c != null) yaml.setComment(path, String.join("\n", c.value()));
    }

    private String concat(String base, String child) {
        return base.isEmpty() ? child : base + "." + child;
    }
}
