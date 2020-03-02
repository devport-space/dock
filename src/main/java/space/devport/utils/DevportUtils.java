package space.devport.utils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.menuutil.MenuHandler;

public class DevportUtils {

    // To enable some features that require a ConsoleOutput instance or a JavaPlugin reference.

    @Getter
    private final JavaPlugin plugin;

    // ConsoleOutput used internally and the plugin should obtain here
    @Getter
    private final ConsoleOutput consoleOutput;

    public static DevportUtils inst;

    @Getter
    private final MenuHandler menuHandler;

    public DevportUtils(JavaPlugin plugin) {
        inst = this;

        this.plugin = plugin;

        consoleOutput = new ConsoleOutput();
        consoleOutput.setPrefix(plugin.getDescription().getName() + " >> ");

        consoleOutput.info("Running on version " + SpigotHelper.getVersion());

        menuHandler = new MenuHandler();

        registerListener();
    }

    public DevportUtils(JavaPlugin plugin, boolean debug) {
        inst = this;

        this.plugin = plugin;

        consoleOutput = new ConsoleOutput();
        consoleOutput.setPrefix(plugin.getDescription().getName() + " >> ");
        consoleOutput.setDebug(debug);

        consoleOutput.info("Running on version " + SpigotHelper.getVersion());

        menuHandler = new MenuHandler();

        registerListener();
    }

    // Register Menu listener
    private void registerListener() {
        plugin.getServer().getPluginManager().registerEvents(menuHandler, plugin);
    }
}