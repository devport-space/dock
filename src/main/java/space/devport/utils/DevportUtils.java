package space.devport.utils;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.menuutil.MenuHandler;

/**
 * Main API class, needs to be instanced in order for some features to work.
 *
 * @author Devport Team
 */
public class DevportUtils {

    // API instance
    @Getter
    private static DevportUtils instance;

    // JavaPlugin reference
    @Getter
    private final JavaPlugin plugin;

    // ConsoleOutput used internally and the plugin should obtain here
    @Getter
    private final ConsoleOutput consoleOutput;

    // Menu handler with a listener
    @Getter
    private final MenuHandler menuHandler;

    /**
     * Construct without a plugin reference.
     */
    public DevportUtils() {
        instance = this;
        consoleOutput = new ConsoleOutput();

        // Cannot instance, as they use the plugin instance.
        this.plugin = null;
        menuHandler = null;
    }

    /**
     * Construct with a plugin reference.
     *
     * @param plugin JavaPlugin reference
     */
    public DevportUtils(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;

        // Instance a console output.
        consoleOutput = new ConsoleOutput();

        // Print current version.
        consoleOutput.info("Running on version " + SpigotHelper.getVersion());

        // Instance Menu handler and register it's listener.
        menuHandler = new MenuHandler();
    }
}