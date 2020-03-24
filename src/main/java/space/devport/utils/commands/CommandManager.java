package space.devport.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private DevportPlugin plugin;

    public final List<MainCommand> registeredCommands = new ArrayList<>();

    public CommandManager(DevportPlugin plugin) {
        this.plugin = plugin;

        // Register commands
        for (MainCommand cmd : registeredCommands) {
            if (plugin.getDescription().getCommands().keySet().contains(cmd.getName())) {
                plugin.getCommand(cmd.getName()).setExecutor(this);
                plugin.getConsoleOutput().debug("Added command " + cmd.getName());
            } else plugin.getConsoleOutput().warn("Command " + cmd.getName() + " is not in plugin.yml");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (MainCommand command : registeredCommands) {
            if (!command.getName().equalsIgnoreCase(label) && !command.getAliases().contains(label)) continue;

            command.runCommand(sender, args);
            break;
        }

        return false;
    }
}