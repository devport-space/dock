package space.devport.utils.messageutil;

import org.bukkit.ChatColor;
import space.devport.utils.configutil.DefaultValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for String operations.
 *
 * @author Devport Team
 */
public class StringUtil {

    /**
     * Colors a string with Bukkit color codes.
     *
     * @param msg Default string
     * @return String with Bukkit color codes
     */
    public static String color(String msg) {
        return color(msg, '&');
    }

    /**
     * Colors a string with Bukkit color codes using specified color character for parsing.
     *
     * @param msg       Default string
     * @param colorChar Color character to parse colors with
     * @return String with Bukkit color codes
     */
    public static String color(String msg, char colorChar) {
        return ChatColor.translateAlternateColorCodes(colorChar, msg);
    }

    /**
     * Colors a list of strings with Bukkit color codes.
     *
     * @param list Default list of strings
     * @return List of strings with Bukkit color codes
     */
    public static List<String> color(List<String> list) {
        return color(list, '&');
    }

    /**
     * Colors a list of strings with Bukkit color codes.
     *
     * @param list      Default list of strings
     * @param colorChar Color character to parse colors with
     * @return List of strings with Bukkit color codes
     */
    public static List<String> color(List<String> list, char colorChar) {
        return list.stream().map(line -> color(line, colorChar)).collect(Collectors.toList());
    }

    /**
     * Joins a list of strings in a single, multi-line parsed string.
     * Uses default line delimiter stored in DefaultValue.java
     *
     * @param list List of strings to join together
     * @return String with line separators.
     */
    public static String listToString(List<String> list) {
        return listToString(list, DefaultValue.LIST_DELIMITER.toString());
    }

    /**
     * Joins a list of strings in a single, multi-line parsed string.
     *
     * @param list      List of strings to join together
     * @param delimiter Line delimiter to use
     * @return String with line separators.
     */
    public static String listToString(List<String> list, String delimiter) {
        return String.join(delimiter, list);
    }

    /**
     * Parses a list from string.
     *
     * @param string    String to parse from
     * @param delimiter Delimiter to use
     * @return Parsed list
     */
    public static List<String> listFromString(String string, String delimiter) {
        List<String> list = new ArrayList<>();
        if (string.contains(delimiter))
            Collections.addAll(list, string.split(delimiter));
        return list;
    }

    /**
     * Parses a list from string using default delimiter.
     *
     * @param string String to parse from
     * @return Parsed list
     */
    public static List<String> listFromString(String string) {
        return listFromString(string, DefaultValue.LIST_DELIMITER.toString());
    }
}