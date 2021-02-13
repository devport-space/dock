package space.devport.dock.economy;

import lombok.Getter;
import lombok.extern.java.Log;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import space.devport.dock.DockedManager;
import space.devport.dock.DockedPlugin;
import space.devport.dock.util.DependencyUtil;

@Log
public class EconomyManager extends DockedManager {

    @Getter
    private Economy economy;

    public EconomyManager(DockedPlugin plugin) {
        super(plugin);
    }

    @Override
    public void preEnable() {
        setupEconomy();
    }

    @Override
    public void afterDependencyLoad() {
        setupEconomy();
    }

    public boolean isHooked() {
        return economy != null;
    }

    private void setupEconomy() {

        if (DependencyUtil.isInstalled("Vault") && economy != null) {
            return;
        }

        if (!DependencyUtil.isInstalled("Vault")) {
            if (economy != null) {
                this.economy = null;
                log.warning(() ->"Vault has been uninstalled.");
            }
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            log.info("Found &eVault&7, but no economy manager.");
            return;
        }

        this.economy = rsp.getProvider();
        log.info("Found &eVault&7, using it's economy.");
    }
}
