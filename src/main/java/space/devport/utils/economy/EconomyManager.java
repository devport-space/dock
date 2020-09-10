package space.devport.utils.economy;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;

public class EconomyManager extends DevportManager {

    @Getter
    private Economy economy;

    public EconomyManager(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void afterDependencyLoad() {
        setupEconomy();
    }

    private void setupEconomy() {

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            if (economy != null) economy = null;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            if (economy != null) economy = null;
            consoleOutput.info("Found Vault, but no economy manager.");
            return;
        }

        if (economy != null) return;

        economy = rsp.getProvider();
        consoleOutput.info("Found Vault, using economy.");
    }
}
