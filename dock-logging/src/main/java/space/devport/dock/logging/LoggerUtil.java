package space.devport.dock.logging;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class LoggerUtil {

    @Contract("null -> null;!null -> !null")
    @Nullable
    public String stripColor(@Nullable String msg) {
        return ChatColor.stripColor(msg);
    }

    /**
     * Colors a string with Bukkit color codes.
     *
     * @param msg Default string
     * @return String with Bukkit color codes
     */
    @Contract("null -> null")
    public String colorBukkit(String msg) {
        return msg == null ? null : ChatColor.translateAlternateColorCodes('&', msg);
    }
}
