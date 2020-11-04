package space.devport.utils.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class DependencyUtil {

    public boolean isInstalled(String name) {
        return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
    }

    public boolean isEnabled(String name) {
        return Bukkit.getServer().getPluginManager().isPluginEnabled(name);
    }
}