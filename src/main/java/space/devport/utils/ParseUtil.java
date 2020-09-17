package space.devport.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ParseUtil {

    public double parseDouble(String str, double defaultValue, boolean... silent) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            if (silent.length < 1 || !silent[0])
                ConsoleOutput.getInstance().warn("Could not parse double from " + str + ", using 0 as default.");
            return defaultValue;
        }
    }

    public double parseDouble(String str, boolean... silent) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            if (silent.length < 1 || !silent[0])
                ConsoleOutput.getInstance().warn("Could not parse double from " + str + ", using 0 as default.");
            return 0;
        }
    }
}
