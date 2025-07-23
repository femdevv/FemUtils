package xyz.femdev.femutils.demo;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import xyz.femdev.femutils.java.config.Comment;
import xyz.femdev.femutils.java.config.Header;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Header({
        "FemUtils demo configuration.",
        "Edit and run /demo reload to apply changes."
})
public record DemoConfig(
        @Comment("Duration format: 10s, 5m, 2h, etc.")
        Duration testDuration,

        @Comment("Message sent on /demo ping")
        Component pingMessage,

        @Comment("Example rewards map")
        Map<String, Integer> rewards,

        @Comment("RGB color example")
        Color uiColor,

        @Comment({"Sound key to play", "Vanilla or custom pack key"})
        NamespacedKey pingSound,

        @Comment("Spawn to teleport to with /demo tpspawn")
        Location spawn,

        @Comment("Materials allowed for /demo give")
        List<Material> giveWhitelist,

        @Comment("Plugin owner UUID")
        UUID owner
) {
    public static DemoConfig defaults() {
        return new DemoConfig(
                Duration.ofSeconds(10),
                Component.text("Pong!"),
                Map.of("zombie", 5, "skeleton", 7),
                Color.fromRGB(0, 170, 255),
                NamespacedKey.minecraft("entity.experience_orb.pickup"),
                null,
                List.of(Material.DIAMOND, Material.GOLD_INGOT),
                UUID.randomUUID()
        );
    }
}
