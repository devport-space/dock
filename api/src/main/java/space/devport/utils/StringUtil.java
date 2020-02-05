package space.devport.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    /**
     * Color string with minecraft color codes
     * @param msg default string with &([1-9a-fA-F]) codes
     * @return String with minecraft color codes
     */
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Color list of strings with minecraft color codes
     * @param list Default list of strings with &([1-9a-fA-F]) codes
     * @return List of strings with minecraft color codes
     */
    public static List<String> color(List<String> list) {
        return list.stream().map(s -> color(s)).collect(Collectors.toList());
    }
}
