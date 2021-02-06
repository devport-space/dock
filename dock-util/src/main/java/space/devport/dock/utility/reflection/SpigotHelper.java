package space.devport.dock.utility.reflection;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class SpigotHelper {

    public String extractNMSVersion(boolean... shortVersion) {

        Pattern pattern;
        if (shortVersion.length > 0 && shortVersion[0])
            pattern = Pattern.compile("v\\d+_\\d+");
        else
            pattern = Pattern.compile("v\\d+_\\d+_R\\d+");

        Matcher matcher = pattern.matcher(Bukkit.getServer().getClass().getPackage().getName());
        return matcher.find() ? matcher.group() : null;
    }

    public String getVersion() {
        return Bukkit.getVersion();
    }
}