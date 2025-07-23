package xyz.femdev.femutils.paper.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

/**
 * Bukkit bridge for executing and tab completing commands via {@link FemCommandManager}.
 */
final class PaperCommandExecutor implements CommandExecutor, TabCompleter {
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final FemCommandManager mgr;

    PaperCommandExecutor(FemCommandManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            mgr.execute(cmd.getName(), sender, Arrays.asList(args));
            return true;
        } catch (Exceptions.NoPermission e) {
            sender.sendMessage(MM.deserialize("<red>You lack permission: " + e.getMessage()));
        } catch (Exceptions.WrongSender e) {
            sender.sendMessage(MM.deserialize("<red>" + e.getMessage()));
        } catch (Exceptions.ParseError e) {
            sender.sendMessage(MM.deserialize("<red>" + e.getMessage()));
        } catch (Throwable t) {
            sender.sendMessage(MM.deserialize("<red>Command error. Check console."));
            t.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return mgr.tab(cmd.getName(), sender, Arrays.asList(args));
    }
}
