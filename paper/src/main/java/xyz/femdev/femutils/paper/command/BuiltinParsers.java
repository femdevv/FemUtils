package xyz.femdev.femutils.paper.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.femdev.femutils.java.core.Result;
import xyz.femdev.femutils.java.time.TimeParser;

import java.time.Duration;
import java.util.UUID;

/**
 * Common {@link ArgParser} implementations for basic Bukkit and Java types.
 */
public final class BuiltinParsers {

    public static final ArgParser<String> STRING = (s, c) -> Result.ok(s);

    public static final ArgParser<Integer> INT = (s, c) -> {
        try {
            return Result.ok(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Result.err("Not an int: " + s);
        }
    };

    public static final ArgParser<Double> DOUBLE = (s, c) -> {
        try {
            return Result.ok(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return Result.err("Not a double: " + s);
        }
    };

    public static final ArgParser<Duration> DURATION = (s, c) -> TimeParser.parse(s);

    public static final ArgParser<Player> PLAYER = (s, c) -> {
        Player p = Bukkit.getPlayerExact(s);
        return p == null ? Result.err("Player not found: " + s) : Result.ok(p);
    };

    public static final ArgParser<UUID> UUID_PARSER = (s, c) -> {
        try {
            return Result.ok(UUID.fromString(s));
        } catch (IllegalArgumentException e) {
            return Result.err("Invalid UUID: " + s);
        }
    };

    public static final ArgParser<Location> LOCATION = (s, c) -> {
        String[] parts = s.split(",");
        if (parts.length < 4) return Result.err("Use world,x,y,z");
        World w = Bukkit.getWorld(parts[0]);
        if (w == null) return Result.err("World not found: " + parts[0]);
        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            return Result.ok(new Location(w, x, y, z));
        } catch (NumberFormatException e) {
            return Result.err("Invalid coords");
        }
    };

    private BuiltinParsers() {
    }
}
