package space.devport.utils.item;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.ParseUtil;

import java.util.Random;

public class Amount {

    private final transient Random random = new Random();

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

    /**
     * Constructor with a low and high.
     *
     * @param low  Minimal (low) value
     * @param high Maximal (high) value
     */
    public Amount(double low, double high) {
        fixed = false;

        lowValue = Math.min(low, high);
        highValue = Math.max(low, high);
    }

    /**
     * Constructor with a fixed value.
     *
     * @param fixedValue Fixes value
     */
    public Amount(double fixedValue) {
        fixed = true;

        this.fixedValue = fixedValue;
    }

    public Amount(Amount amount) {
        this.fixed = amount.isFixed();
        this.fixedValue = amount.getFixedValue();
        this.highValue = amount.getHighValue();
        this.lowValue = amount.getLowValue();
    }

    @Nullable
    public static Amount fromString(String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        if (str.contains("-")) {
            String[] arr = str.split("-");

            if (arr.length != 2) {
                ConsoleOutput.getInstance().warn("Could not parse Amount from " + str + ", incorrect number of parameters in a dynamic syntax.");
                return null;
            }

            double low = ParseUtil.parseDouble(arr[0]);
            double high = ParseUtil.parseDouble(arr[1]);

            return new Amount(low, high);
        } else {
            return new Amount(ParseUtil.parseDouble(str));
        }
    }

    public int getInt() {
        return fixed ? (int) fixedValue : random.nextInt((int) highValue) + (int) lowValue;
    }

    public double getDouble() {
        return fixed ? fixedValue : (random.nextDouble() * highValue) + lowValue;
    }

    public String toString() {
        return fixed ? String.valueOf(fixedValue) : lowValue + "-" + highValue;
    }

    public boolean isEmpty() {
        return fixed ? fixedValue == 0 : lowValue == 0 && highValue == 0;
    }
}