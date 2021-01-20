package space.devport.utils.text;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.utility.reflection.ServerVersion;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for String operations.
 *
 * @author Devport Team
 */
@UtilityClass
public class StringUtil {

    public String LIST_DELIMITER = "\n";

    public String HEX_PATTERN = "\\{#[A-Fa-f0-9]{6}}";

    private Pattern hexPattern = Pattern.compile(HEX_PATTERN);

    public final List<ChatColor> DISTINCT_CHAT_COLORS = Arrays.asList(ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GREEN, ChatColor.RED, ChatColor.YELLOW);

    public ChatColor getRandomColor() {
        int rand = new Random().nextInt(DISTINCT_CHAT_COLORS.size());
        return DISTINCT_CHAT_COLORS.get(rand);
    }

    public void compilePattern() {
        hexPattern = Pattern.compile(HEX_PATTERN);
    }

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
    public String color(String msg) {
        return color(msg, '&');
    }

    /**
     * Colors a string with Bukkit color codes using specified color character for parsing.
     *
     * @param msg       Default string
     * @param colorChar Color character to parse colors with
     * @return String with Bukkit color codes
     */
    @Contract("null,_ -> null")
    public String color(String msg, char colorChar) {
        return msg == null ? null : hexColor(msg, colorChar);
    }

    @Nullable
    public String hexColor(String string, char colorChar) {

        if (string == null) return null;

        if (hexPattern != null || ServerVersion.isCurrentAbove(ServerVersion.v1_16)) {
            Matcher matcher = hexPattern.matcher(string);
            while (matcher.find()) {
                final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = string.substring(0, matcher.start());
                final String after = string.substring(matcher.end());
                string = before + hexColor + after;
                matcher = hexPattern.matcher(string);
            }
        }
        return ChatColor.translateAlternateColorCodes(colorChar, string);
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
        return listToString(list, LIST_DELIMITER);
    }

    /**
     * Joins a list of strings in a single, multi-line parsed string.
     *
     * @param list      List of strings to join together
     * @param delimiter Line delimiter to use
     * @return String with line separators.
     */
    @Contract("null,_ -> null;_,null -> null")
    @Nullable
    public String listToString(@Nullable List<String> list, @Nullable String delimiter) {
        return list == null || delimiter == null ? null : String.join(delimiter, list);
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
        return listFromString(string, LIST_DELIMITER);
    }

    public List<String> replace(List<String> list, String key, Object value) {
        return list.stream().map(l -> l.replace(key, value.toString())).collect(Collectors.toList());
    }

    public String join(String delimiter, Object... params) {
        return Arrays.stream(params)
                .map(String::valueOf)
                .filter(string -> !Strings.isNullOrEmpty(string))
                .collect(Collectors.joining(delimiter));
    }

    public String valueOfEmpty(Object obj) {
        String str = String.valueOf(obj);
        return str.isEmpty() ? "'empty'" : str;
    }
}