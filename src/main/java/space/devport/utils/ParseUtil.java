package space.devport.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ParseUtil {

    public double parseDouble(String str, double defaultValue, boolean... silent) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            if (silent.length < 1 || !silent[0])
                ConsoleOutput.getInstance().warn("Could not parse double from " + str + ", using " + defaultValue + " as default.");
            return defaultValue;
        }
    }

    public double parseDouble(String str, boolean... silent) {
        return parseDouble(str, 0, silent);
    }

    public int parseInteger(String str, int defaultValue, boolean... silent) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            if (silent.length < 1 || !silent[0])
                ConsoleOutput.getInstance().warn("Could not parse int from " + str + ", using " + defaultValue + " as default.");
            return defaultValue;
        }
    }

    public int parseInteger(String str, boolean... silent) {
        return parseInteger(str, 0, silent);
    }
}