package space.devport.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> color(List<String> list) {
        List<String> out = new ArrayList<>();
        for (String line : list)
            out.add(color(line));
        return out;
    }
}
