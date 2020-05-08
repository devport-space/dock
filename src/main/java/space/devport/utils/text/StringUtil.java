package space.devport.utils.text;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.utility.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for String operations.
 *
 * @author Devport Team
 */
@UtilityClass
public class StringUtil {

    // Strip colors from String
    public String stripColor(String msg) {
        return msg != null ? ChatColor.stripColor(msg) : null;
    }

    /**
     * Colors a string with Bukkit color codes.
     *
     * @param msg Default string
     * @return String with Bukkit color codes
     */
    @Nullable
    public String color(@Nullable String msg) {
        return color(msg, '&');
    }

    /**
     * Colors a string with Bukkit color codes using specified color character for parsing.
     *
     * @param msg       Default string
     * @param colorChar Color character to parse colors with
     * @return String with Bukkit color codes
     */
    @Nullable
    public String color(@Nullable String msg, char colorChar) {
        return msg == null ? null : ChatColor.translateAlternateColorCodes(colorChar, msg);
    }

    /**
     * Colors a list of strings with Bukkit color codes.
     *
     * @param list Default list of strings
     * @return List of strings with Bukkit color codes
     */
    @Nullable
    public List<String> color(@Nullable List<String> list) {
        return color(list, '&');
    }

    /**
     * Colors a list of strings with Bukkit color codes.
     *
     * @param list      Default list of strings
     * @param colorChar Color character to parse colors with
     * @return List of strings with Bukkit color codes
     */
    @Nullable
    public List<String> color(@Nullable List<String> list, char colorChar) {
        return list == null ? null : list.stream().map(line -> color(line, colorChar)).collect(Collectors.toList());
    }

    /**
     * Joins a list of strings in a single, multi-line parsed string.
     * Uses default line delimiter stored in DefaultValue.java
     *
     * @param list List of strings to join together
     * @return String with line separators.
     */
    @Nullable
    public String listToString(@Nullable List<String> list) {
        return listToString(list, Settings.LIST_DELIMITER.toString());
    }

    /**
     * Joins a list of strings in a single, multi-line parsed string.
     *
     * @param list      List of strings to join together
     * @param delimiter Line delimiter to use
     * @return String with line separators.
     */
    @Nullable
    public String listToString(@Nullable List<String> list, @NotNull String delimiter) {
        return list == null ? null : String.join(delimiter, list);
    }

    /**
     * Parses a list from string.
     *
     * @param string    String to parse from
     * @param delimiter Delimiter to use
     * @return Parsed list
     */
    @NotNull
    public List<String> listFromString(@Nullable String string, @NotNull String delimiter) {
        List<String> list = new ArrayList<>();
        if (!Strings.isNullOrEmpty(string) && string.contains(delimiter))
            Collections.addAll(list, string.split(delimiter));
        return list;
    }

    /**
     * Parses a list from string using default delimiter.
     *
     * @param string String to parse from
     * @return Parsed list
     */
    @NotNull
    public List<String> listFromString(@Nullable String string) {
        return listFromString(string, Settings.LIST_DELIMITER.toString());
    }

    public List<String> replace(List<String> list, String key, Object value) {
        return list.stream().map(l -> l.replace(key, value.toString())).collect(Collectors.toList());
    }
}