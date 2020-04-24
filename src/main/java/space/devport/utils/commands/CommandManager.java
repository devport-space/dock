package space.devport.utils.commands;

import com.google.common.base.Strings;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

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

            if (mainCmd.registerTabCompleter()) {
                cmd.setTabCompleter(this);
                plugin.getConsoleOutput().debug("And registered a tab completer for it.");
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        runCommands(sender, label, args);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        for (MainCommand mainCommand : registeredCommands) {
            if (!label.equalsIgnoreCase(mainCommand.getName()) && !mainCommand.getAliases().contains(label)) continue;

            if (args.length == 1) {
                List<String> subCommands = mainCommand.getSubCommands().stream().map(SubCommand::getName).collect(Collectors.toList());

                if (!Strings.isNullOrEmpty(args[0]))
                    subCommands.removeIf(sc -> !sc.toLowerCase().startsWith(args[0].toLowerCase()));
                return subCommands;
            } else {
                SubCommand subCommand = mainCommand.getSubCommands().stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
                if (subCommand != null) return subCommand.requestTabComplete(sender, args);
            }
        }

        return null;
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