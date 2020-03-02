package space.devport.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class SpigotHelper {

    @Getter private static final boolean isPaper = Bukkit.getName().equalsIgnoreCase("paper");
    @Getter private static final boolean isISpigot = Bukkit.getName().equalsIgnoreCase("ispigot");
    //TODO: Add more spigot server checks

    public static String extractNMSVersion() {
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName()); //Matches string v{INT}_{INT}_R{INT}
        return matcher.find() ? matcher.group() : null;
    }

    // TODO Doesn't work, returns v1_8_R3
    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";
        return version;
    }
}