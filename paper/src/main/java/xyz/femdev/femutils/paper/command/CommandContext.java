package xyz.femdev.femutils.paper.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Holds context for a command execution: sender, arguments, and utilities.
 */
public final class CommandContext {
    private final CommandSender sender;
    private final Map<String, Object> args = new HashMap<>();

    CommandContext(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender sender() {
        return sender;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player player() {
        return (Player) sender;
    }

    /**
     * Stores a parsed argument by name.
     */
    public <T> void put(String name, T value) {
        args.put(name, value);
    }

    /**
     * Gets a typed argument by name.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) args.get(name);
    }

    /**
     * Gets a typed argument as an {@link Optional}.
     */
    public <T> Optional<T> getOptional(String name) {
        return Optional.ofNullable(get(name));
    }

    /**
     * Sends a message to the sender.
     */
    public void msg(Component component) {
        sender.sendMessage(component);
    }

    /**
     * Gets the UUID of the sender if they are a player.
     */
    public UUID playerUUID() {
        return isPlayer() ? player().getUniqueId() : null;
    }
}
