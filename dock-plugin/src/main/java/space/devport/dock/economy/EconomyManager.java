package space.devport.dock.economy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import space.devport.dock.DockedModule;
import space.devport.dock.DockedPlugin;
import space.devport.dock.utility.DependencyUtil;

@Slf4j
public class EconomyManager extends DockedModule {

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
                log.warn("Vault has been uninstalled.");
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