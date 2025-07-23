package xyz.femdev.femutils.paper.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central manager for registering and routing command execution and tab completion.
 */
public final class FemCommandManager {
    private final JavaPlugin plugin;
    private final Map<String, CommandNode> roots = new HashMap<>();
    private final PaperCommandExecutor executor = new PaperCommandExecutor(this);

    public FemCommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a root command node for the given label.
     * The command must be defined in plugin.yml.
     */
    public void registerRoot(String label, CommandNode node) {
        roots.put(label.toLowerCase(), node);
        PluginCommand pc = plugin.getCommand(label);
        if (pc == null) throw new IllegalStateException("Command not defined in plugin.yml: " + label);
        pc.setExecutor(executor);
        pc.setTabCompleter(executor);
    }

    CommandNode root(String name) {
        return roots.get(name.toLowerCase());
    }

    List<String> tab(String root, org.bukkit.command.CommandSender sender, List<String> args) {
        CommandNode n = root(root);
        return n == null ? List.of() : n.tabComplete(sender, args);
    }

    void execute(String root, org.bukkit.command.CommandSender sender, List<String> args) throws Exception {
        CommandNode n = root(root);
        if (n == null) throw new Exceptions.ParseError("Unknown command root.");
        n.execute(sender, args);
    }
}
