package space.devport.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.DevportPlugin;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final DevportPlugin plugin;

    public final List<MainCommand> registeredCommands = new ArrayList<>();

    public CommandManager(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // Register commands
        for (MainCommand mainCmd : this.registeredCommands) {
            if (!plugin.getDescription().getCommands().containsKey(mainCmd.getName())) {
                plugin.getConsoleOutput().warn("Command " + mainCmd.getName() + " is not in plugin.yml");
                continue;
            }

            PluginCommand cmd = plugin.getCommand(mainCmd.getName());

            if (cmd == null) continue;

            cmd.setExecutor(this);
            plugin.getConsoleOutput().debug("Added command " + cmd.getName());
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (MainCommand command : registeredCommands) {
            if (!command.getName().equalsIgnoreCase(label) && !command.getAliases().contains(label)) continue;

            command.runCommand(sender, args);
            break;
        }

        return false;
    }
}