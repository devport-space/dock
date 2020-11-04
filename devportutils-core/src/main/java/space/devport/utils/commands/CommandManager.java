package space.devport.utils.commands;

import com.google.common.base.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends DevportManager implements CommandExecutor, TabCompleter {

    public final List<MainCommand> registeredCommands = new ArrayList<>();

    public CommandManager(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        runCommands(sender, label, args);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        for (MainCommand mainCommand : registeredCommands) {
            if (label.equalsIgnoreCase(mainCommand.getName()) || mainCommand.getAliases().contains(label))
                return mainCommand.requestTabComplete(sender, args);
        }
        return new ArrayList<>();
    }

    private void runCommands(CommandSender sender, String label, String[] args) {
        for (MainCommand command : registeredCommands) {
            if (!command.getName().equalsIgnoreCase(label) && !command.getAliases().contains(label)) continue;

            command.runCommand(sender, label, args);
            break;
        }
    }

    /**
     * Execute registered commands directly.
     */
    public void executeCommand(@Nullable CommandSender sender, @Nullable String command) {

        if (Strings.isNullOrEmpty(command) || sender == null) return;

        String[] arr = command.split(" ");
        String label = arr[0].contains("/") ? arr[0].replace("/", "") : arr[0];
        List<String> argList = new ArrayList<>(Arrays.asList(arr).subList(1, arr.length));
        runCommands(sender, label, argList.toArray(new String[0]));
    }

    public void registerAll() {

        // Register commands
        for (MainCommand mainCommand : this.registeredCommands) {
            if (!plugin.getDescription().getCommands().containsKey(mainCommand.getName())) {
                plugin.getConsoleOutput().warn("Command " + mainCommand.getName() + " is not in plugin.yml.");
                continue;
            }

            PluginCommand command = plugin.getCommand(mainCommand.getName());

            if (command == null)
                continue;

            mainCommand.setAliases(command.getAliases().toArray(new String[0]));

            command.setExecutor(this);

            if (mainCommand.registerTabCompleter()) {
                command.setTabCompleter(this);
            }

            mainCommand.addLanguage();

            plugin.getConsoleOutput().debug("Added command " + command.getName() + " with aliases [" + String.join(", ", mainCommand.getAliases()) + "]" + (mainCommand.registerTabCompleter() ? " and with a tab completer." : ""));
        }
    }

    @Override
    public void afterEnable() {
        registerAll();
    }
}