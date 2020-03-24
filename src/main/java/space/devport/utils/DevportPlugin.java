package space.devport.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.commands.CommandManager;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.menu.MenuHandler;
import space.devport.utils.text.Message;

public abstract class DevportPlugin extends JavaPlugin {

    private static DevportPlugin instance;

    @Getter
    protected PluginManager pluginManager;

    @Getter
    @Setter
    protected Configuration configuration;

    @Getter
    @Setter
    protected ConsoleOutput consoleOutput;

    @Getter
    private CommandManager commandManager;

    @Getter
    private MenuHandler menuHandler;

    @Getter
    private String prefix = "";

    public static DevportPlugin getInstance() {
        return instance;
    }

    public abstract void onPluginEnable();

    public abstract void onPluginDisable();

    public abstract void onReload();

    @Override
    public void onEnable() {
        instance = this;

        pluginManager = getServer().getPluginManager();

        new DevportUtils(this);

        configuration = new Configuration(this, "config");

        // Setup Console Output
        consoleOutput = new ConsoleOutput();
        consoleOutput.setPrefix(getDescription().getPrefix());
        consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug"));

        prefix = getDescription().getPrefix();

        // Fancy intro

        commandManager = new CommandManager(this);
        menuHandler = new MenuHandler();

        onPluginEnable();
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        consoleOutput.addListener(sender);

        configuration.reload();

        onReload();

        consoleOutput.removeListener(sender);

        new Message("&7Done... reload took &f" + (System.currentTimeMillis() - start) + "&7ms.").send(sender);
    }

    @Override
    public void onDisable() {
        onPluginDisable();
    }

    @Override
    public void reloadConfig() {
        configuration.reload();
    }

    @Override
    public void saveConfig() {
        configuration.save();
    }

    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, this);
    }
}