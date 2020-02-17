package space.devport.utils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.simplegui.SimpleGUI;
import space.devport.utils.simplegui.SimpleGUIHandler;

public class DevportUtils {

    // To enable some features that require a ConsoleOutput instance or a JavaPlugin reference.

    @Getter
    private JavaPlugin plugin;

    // ConsoleOutput used internally and the plugin should obtain here
    @Getter
    private ConsoleOutput consoleOutput;

    public static DevportUtils inst;

    @Getter
    private SimpleGUIHandler guiHandler;

    public DevportUtils(JavaPlugin plugin) {
        inst = this;

        this.plugin = plugin;

        consoleOutput = new ConsoleOutput();
        consoleOutput.setPrefix(plugin.getDescription().getName() + " >> ");

        guiHandler = new SimpleGUIHandler();

        registerListener();
    }

    public DevportUtils(JavaPlugin plugin, boolean debug) {
        inst = this;

        this.plugin = plugin;

        consoleOutput = new ConsoleOutput();
        consoleOutput.setPrefix(plugin.getDescription().getName() + " >> ");
        consoleOutput.setDebug(debug);

        guiHandler = new SimpleGUIHandler();

        registerListener();
    }

    // Register GUI listener
    private void registerListener() {
        plugin.getServer().getPluginManager().registerEvents(guiHandler, plugin);
    }
}
