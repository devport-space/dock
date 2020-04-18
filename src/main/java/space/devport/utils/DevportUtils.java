package space.devport.utils;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DevportUtils {

    @Getter
    private static DevportUtils instance;

    @Getter
    private final JavaPlugin plugin;

    @Getter
    private Economy economy;

    @Getter
    private final ConsoleOutput consoleOutput;

    /**
     * Construct with a plugin reference.
     *
     * @param plugin JavaPlugin reference
     */
    public DevportUtils(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;

        this.consoleOutput = new ConsoleOutput(plugin);

        // Optional Dependencies
        setupEconomy();
    }

    // Check if a plugin is enabled
    public boolean checkDependency(String pluginName) {
        if (plugin == null) return false;

        return plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    private void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) return;

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) return;

        this.economy = rsp.getProvider();
    }
}