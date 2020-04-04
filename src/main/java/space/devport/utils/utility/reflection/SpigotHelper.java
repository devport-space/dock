package space.devport.utils.utility.reflection;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class SpigotHelper {

    public String extractNMSVersion() {
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        return matcher.find() ? matcher.group() : null;
    }

    public String getVersion() {
        return Bukkit.getVersion();
    }
}