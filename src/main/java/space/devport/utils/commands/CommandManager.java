package space.devport.utils.commands;

import com.google.common.base.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;

import java.util.ArrayList;
import java.util.Arrays;
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

            mainCmd.aliases = new String[cmd.getAliases().size()];
            cmd.getAliases().toArray(mainCmd.aliases);

            cmd.setExecutor(this);
            plugin.getConsoleOutput().debug("Added command " + cmd.getName() + " with aliases [" + String.join(", ", mainCmd.aliases) + "]");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        runCommands(sender, label, args);
        return false;
    }

    private void runCommands(CommandSender sender, String label, String[] args) {
        for (MainCommand command : registeredCommands) {
            if (!command.getName().equalsIgnoreCase(label) && !command.getAliases().contains(label)) continue;

            command.runCommand(sender, label, args);
            break;
        }
    }

    /**
     * Execute a registered command for a sender.
     */
    public void executeCommand(@Nullable CommandSender sender, @Nullable String command) {

        if (Strings.isNullOrEmpty(command) || sender == null) return;

        String[] arr = command.split(" ");
        String label = arr[0].contains("/") ? arr[0].replace("/", "") : arr[0];
        List<String> argList = new ArrayList<>(Arrays.asList(arr).subList(1, arr.length));
        runCommands(sender, label, argList.toArray(new String[0]));
    }
}