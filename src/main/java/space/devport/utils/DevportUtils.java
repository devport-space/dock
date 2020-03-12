package space.devport.utils;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import space.devport.utils.menuutil.MenuHandler;

import java.util.Random;

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

    // Optional Dependencies
    @Getter
    private Economy economy;

    @Getter
    private final Random random = new Random();

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

        // Optional Dependencies
        setupEconomy();
    }

    // Check if a plugin is enabled
    public boolean checkDependency(String pluginName) {
        if (plugin == null) return false;

        if (plugin.getServer().getPluginManager().getPlugin(pluginName) == null) return false;

        return plugin.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return;

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) return;

        this.economy = rsp.getProvider();
    }
}