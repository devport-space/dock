package space.devport.utils.packutil;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import space.devport.utils.DevportUtils;

import java.util.Random;

public class Amount {

    private Random random = new Random();

    @Getter
    @Setter
    private double fixedValue;

    @Getter
    @Setter
    private double lowValue;

    @Getter
    @Setter
    private double highValue;

    // Whether or not is the value fixed
    @Getter
    @Setter
    private boolean fixed;

    // Constructor for random value
    public Amount(double low, double high) {
        fixed = false;

        if (low > high) {
            lowValue = high;
            highValue = low;
        } else {
            lowValue = low;
            highValue = high;
        }
    }

    // Constructor for fixed value
    public Amount(double fixedValue) {
        fixed = true;

        this.fixedValue = fixedValue;
    }

    // Load Amount from yaml
    // Changed string syntax to <low>-<high>
    public static Amount loadAmount(FileConfiguration yaml, String path) {
        String dataStr = yaml.getString(path);

        if (Strings.isNullOrEmpty(dataStr))
            return new Amount(1);

        try {
            if (dataStr.contains("-")) {
                String[] arr = dataStr.split("-");

                double low = Double.parseDouble(arr[0]);
                double high = Double.parseDouble(arr[1]);

                return new Amount(low, high);
            } else {
                int n = Integer.parseInt(dataStr);

                return new Amount(n);
            }
        } catch (IllegalArgumentException e) {
            return new Amount(1);
        }
    }

    public int getInt() {
        return fixed ? (int) fixedValue : random.nextInt((int) highValue) + (int) lowValue;
    }

    public double getDouble() {
        return fixed ? fixedValue : (random.nextDouble() * highValue) + lowValue;
    }

    public String toString() {
        return fixed ? String.valueOf(fixedValue) : lowValue + " - " + highValue;
    }
}