package space.devport.utils;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.item.Amount;

@UtilityClass
public class ParseUtil {

    @Nullable
    public <E extends Enum<E>> E parseEnum(String str, Class<E> clazz) {
        try {
            return E.valueOf(clazz, str);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

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

    public Vector parseVector(@Nullable String str) {

        if (Strings.isNullOrEmpty(str)) {
            return new Vector();
        }

        String[] arr = str.split(";");

        if (arr.length != 3) {
            ConsoleOutput.getInstance().warn("Could not parse vector from " + str + ", invalid number of parameters.");
            return new Vector();
        }

        Amount x = Amount.fromString(arr[0]);
        Amount y = Amount.fromString(arr[1]);
        Amount z = Amount.fromString(arr[2]);

        return new Vector(x == null ? 0 : x.getDouble(),
                y == null ? 0 : y.getDouble(),
                z == null ? 0 : z.getDouble());
    }

    public <T> T getOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}