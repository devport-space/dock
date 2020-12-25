package space.devport.utils.commands;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends DevportManager implements CommandExecutor, TabCompleter {

    public final List<MainCommand> registeredCommands = new ArrayList<>();
    private final CommandMap commandMap;

    public CommandManager(DevportPlugin plugin) {
        super(plugin);
        try {
            Class<?> serverClazz = Bukkit.getServer().getClass();
            Field field = serverClazz.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
            field.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        runCommand(sender, label, args);
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

    private void runCommand(CommandSender sender, String label, String[] args) {
        for (MainCommand command : registeredCommands) {
            if (!command.getName().equalsIgnoreCase(label) && !command.getAliases().contains(label))
                continue;

            command.runCommand(sender, label, args);
            break;
        }
    }

    /**
     * Execute registered commands directly.
     */
    public void executeCommand(@Nullable CommandSender sender, @Nullable String command) {

        if (Strings.isNullOrEmpty(command) || sender == null)
            return;

        String[] arr = command.split(" ");
        String label = arr[0].contains("/") ? arr[0].replace("/", "") : arr[0];
        List<String> argList = new ArrayList<>(Arrays.asList(arr).subList(1, arr.length));
        runCommand(sender, label, argList.toArray(new String[0]));
    }

    public void registerAll() {

        // Register commands
        for (MainCommand mainCommand : this.registeredCommands) {

            PluginCommand command = plugin.getCommand(mainCommand.getName());

            if (command == null) {
                plugin.getConsoleOutput().debug(String.format("Command %s is not in plugin.yml, injecting.", mainCommand.getName()));

                try {
                    Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                    constructor.setAccessible(true);
                    command = constructor.newInstance(mainCommand.getName(), plugin);
                    constructor.setAccessible(false);
                } catch (Exception e) {
                    plugin.getConsoleOutput().err(String.format("Could not inject command %s", mainCommand.getName()));
                    e.printStackTrace();
                    continue;
                }

                command.setAliases(mainCommand.getAliases());
            }

            mainCommand.setAliases(command.getAliases().toArray(new String[0]));

            command.setExecutor(this);

            if (mainCommand.registerTabCompleter()) {
                command.setTabCompleter(this);
            }

            commandMap.register(command.getName(), command);

            mainCommand.addLanguage();

            plugin.getConsoleOutput().debug(String.format("Added command %s with aliases [%s]%s", command.getName(), String.join(", ", mainCommand.getAliases()), mainCommand.registerTabCompleter() ? " and with a tab completer." : ""));
        }
    }

    @Override
    public void afterEnable() {
        registerAll();
    }
}