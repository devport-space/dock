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
import space.devport.utils.utility.reflection.ServerType;
import space.devport.utils.utility.reflection.ServerVersion;

public abstract class DevportPlugin extends JavaPlugin {

    private static DevportPlugin instance;

    // TODO: Modules

    @Getter
    private DevportUtils utils;

    @Getter
    protected PluginManager pluginManager;

    @Getter
    @Setter
    protected ConsoleOutput consoleOutput;

    @Getter
    private CommandManager commandManager;

    @Getter
    private MenuHandler menuHandler;

    @Getter
    @Setter
    protected Configuration configuration;

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
        long start = System.currentTimeMillis();

        instance = this;

        ServerVersion.loadServerVersion();
        ServerType.loadServerType();

        pluginManager = getServer().getPluginManager();

        utils = new DevportUtils(this);

        configuration = new Configuration(this, "config");

        // Setup Console Output
        consoleOutput = new ConsoleOutput(this);
        consoleOutput.setColors(true);
        consoleOutput.setPrefix(getDescription().getPrefix() != null ? getDescription().getPrefix() : "");
        consoleOutput.setDebug(configuration.getFileConfiguration().getBoolean("debug-enabled"));

        prefix = getDescription().getPrefix();

        consoleOutput.info("Starting up " + getDescription().getName() + " v" + getDescription().getVersion());
        consoleOutput.info("Running on " + ServerType.getCurrentServerType().getName() + " " + ServerVersion.getCurrentVersion().toString());
        consoleOutput.info("&3~~~~~~~~~~~~ &7Devport &3~~~~~~~~~~~~");

        commandManager = new CommandManager(this);
        menuHandler = new MenuHandler();

        onPluginEnable();

        commandManager.registerAll();

        consoleOutput.info("&3~~~~~~~~~~~~ &7/////// &3~~~~~~~~~~~~");
        consoleOutput.info("Done... startup took &f" + (System.currentTimeMillis() - start) + "&7ms.");
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