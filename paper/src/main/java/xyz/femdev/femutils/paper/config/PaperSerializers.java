package xyz.femdev.femutils.paper.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import xyz.femdev.femutils.java.config.ReflectMapper;
import xyz.femdev.femutils.java.config.TypeRegistry;
import xyz.femdev.femutils.java.config.TypeSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Built-in type serializers for common Bukkit/Adventure types used in configs.
 */
public final class PaperSerializers {

    public static final TypeSerializer<UUID> UUID_SER = new TypeSerializer<>() {
        @Override
        public UUID deserialize(Object raw, ReflectMapper ctx, Class<UUID> type) {
            return raw == null ? null : UUID.fromString(raw.toString());
        }

        @Override
        public Object serialize(UUID value, ReflectMapper ctx) {
            return value.toString();
        }
    };

    public static final TypeSerializer<Color> COLOR = new TypeSerializer<>() {
        @Override
        public Color deserialize(Object raw, ReflectMapper ctx, Class<Color> type) {
            if (raw instanceof Map<?, ?> m)
                return Color.fromRGB(num(m, "r", 0), num(m, "g", 0), num(m, "b", 0));
            if (raw instanceof String s && s.startsWith("#"))
                return Color.fromRGB(Integer.parseInt(s.substring(1), 16));
            throw new IllegalArgumentException("Color must be {r,g,b} or \"#RRGGBB\"");
        }

        @Override
        public Object serialize(Color value, ReflectMapper ctx) {
            return Map.of("r", value.getRed(), "g", value.getGreen(), "b", value.getBlue());
        }

        private int num(Map<?, ?> m, String key, int def) {
            Object v = m.get(key);
            if (v == null) return def;
            return v instanceof Number n ? n.intValue() : Integer.parseInt(v.toString());
        }
    };

    public static final TypeSerializer<NamespacedKey> SOUND_KEY = new TypeSerializer<>() {
        @Override
        public NamespacedKey deserialize(Object raw, ReflectMapper ctx, Class<NamespacedKey> type) {
            if (raw == null) return null;
            String s = raw.toString();
            return NamespacedKey.fromString(s.contains(":") ? s : "minecraft:" + s);
        }

        @Override
        public Object serialize(NamespacedKey value, ReflectMapper ctx) {
            return value.asString();
        }
    };

    public static final TypeSerializer<Location> LOCATION = new TypeSerializer<>() {
        @Override
        public Location deserialize(Object raw, ReflectMapper ctx, Class<Location> type) {
            if (!(raw instanceof Map<?, ?> m)) throw new IllegalArgumentException("Location must be a map");
            String world = Objects.toString(m.get("world"), null);
            if (world == null) throw new IllegalArgumentException("Missing world");
            World w = Bukkit.getWorld(world);
            if (w == null) throw new IllegalArgumentException("Unknown world: " + world);
            return new Location(w,
                    get(m, "x"), get(m, "y"), get(m, "z"),
                    (float) get(m, "yaw", 0f), (float) get(m, "pitch", 0f));
        }

        @Override
        public Object serialize(Location loc, ReflectMapper ctx) {
            return Map.of(
                    "world", loc.getWorld().getName(),
                    "x", loc.getX(),
                    "y", loc.getY(),
                    "z", loc.getZ(),
                    "yaw", loc.getYaw(),
                    "pitch", loc.getPitch()
            );
        }

        private double get(Map<?, ?> m, String key) {
            return get(m, key, 0.0);
        }

        private double get(Map<?, ?> m, String key, double def) {
            Object v = m.get(key);
            return v instanceof Number n ? n.doubleValue() : (v != null ? Double.parseDouble(v.toString()) : def);
        }
    };

    public static final TypeSerializer<Material> MATERIAL = new TypeSerializer<>() {
        @Override
        public Material deserialize(Object raw, ReflectMapper ctx, Class<Material> type) {
            return raw == null ? null : Material.matchMaterial(raw.toString());
        }

        @Override
        public Object serialize(Material value, ReflectMapper ctx) {
            return value.getKey().asString();
        }
    };

    private static final MiniMessage MM = MiniMessage.miniMessage();
    public static final TypeSerializer<Component> COMPONENT = new TypeSerializer<>() {
        @Override
        public Component deserialize(Object raw, ReflectMapper ctx, Class<Component> type) {
            return raw == null ? Component.empty() : MM.deserialize(raw.toString());
        }

        @Override
        public Object serialize(Component value, ReflectMapper ctx) {
            return MM.serialize(value);
        }
    };

    private static final Pattern DUR = Pattern.compile("(\\d+)([smhdw])", Pattern.CASE_INSENSITIVE);
    public static final TypeSerializer<Duration> DURATION = new TypeSerializer<>() {
        @Override
        public Duration deserialize(Object raw, ReflectMapper ctx, Class<Duration> type) {
            if (raw == null) return Duration.ZERO;
            if (raw instanceof Number n) return Duration.ofSeconds(n.longValue());
            Matcher m = DUR.matcher(raw.toString().trim());
            if (!m.find()) throw new IllegalArgumentException("Bad duration: " + raw);
            m.reset();
            long secs = 0;
            while (m.find()) {
                long n = Long.parseLong(m.group(1));
                secs += switch (m.group(2).toLowerCase()) {
                    case "s" -> n;
                    case "m" -> n * 60;
                    case "h" -> n * 3600;
                    case "d" -> n * 86400;
                    case "w" -> n * 604800;
                    default -> 0;
                };
            }
            return Duration.ofSeconds(secs);
        }

        @Override
        public Object serialize(Duration value, ReflectMapper ctx) {
            long secs = value.getSeconds();
            return (secs % 60 == 0) ? (secs / 60) + "m" : secs + "s";
        }
    };

    private PaperSerializers() {
    }

    /**
     * Registers all built-in serializers into the given registry.
     */
    public static void registerAll(TypeRegistry reg) {
        reg.register(Duration.class, DURATION);
        reg.register(UUID.class, UUID_SER);
        reg.register(Component.class, COMPONENT);
        reg.register(Color.class, COLOR);
        reg.register(NamespacedKey.class, SOUND_KEY);
        reg.register(Location.class, LOCATION);
        reg.register(Material.class, MATERIAL);
    }

    /**
     * Parses a list of {@link Material}s from a raw list.
     */
    public static List<Material> materialList(Object raw) {
        if (raw == null) return List.of();
        if (!(raw instanceof List<?> list)) throw new IllegalArgumentException("Expected list");
        return list.stream()
                .map(o -> Material.matchMaterial(o.toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
