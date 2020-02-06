package space.devport.utils.messageutil;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    // TODO Make strings and lists nullable to prevent errs., hook to ConsoleOutput

    /**
     * Colors a string with Bukkit color codes.
     *
     * @param msg Default string with &([1-9a-fA-F]) codes
     * @return String with Bukkit color codes
     */
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Colors a list of strings with Bukkit color codes.
     *
     * @param list Default list of strings with &([1-9a-fA-F]) codes
     * @return List of strings with Bukkit color codes
     */
    public static List<String> color(List<String> list) {
        return list.stream().map(StringUtil::color).collect(Collectors.toList());
    }

    /**
     * Colors a string with Bukkit color codes using specified color character for parsing.
     *
     * @param msg       Default string with &([1-9a-fA-F]) codes
     * @param colorChar Color character to parse colors with
     * @return String with Bukkit color codes
     */
    public static String color(String msg, char colorChar) {
        return ChatColor.translateAlternateColorCodes(colorChar, msg);
    }

    /**
     * Colors a list of strings with Bukkit color codes.
     *
     * @param list      Default list of strings with &([1-9a-fA-F]) codes
     * @param colorChar Color character to parse colors with
     * @return List of strings with Bukkit color codes
     */
    public static List<String> color(List<String> list, char colorChar) {
        return list.stream().map(line -> color(line, colorChar)).collect(Collectors.toList());
    }

    /**
     * Joins a list of strings in a single, mutli-line parsed string.
     *
     * @param list List of strings to join together
     * @return String with line separators.
     */
    public static String toMultilineString(List<String> list) {
        return String.join("\n", list);
    }
}