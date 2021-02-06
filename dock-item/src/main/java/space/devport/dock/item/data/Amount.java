package space.devport.dock.item.data;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.utility.ParseUtil;

import java.util.Random;

@Slf4j
public class Amount implements Cloneable {

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

    @Getter
    @Setter
    private int decimalPlaces = 2;

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
                return null;
            }

            double low = ParseUtil.parseDoubleHandled(arr[0],
                    c -> log.warn("Failed to parse double from " + c.getInput() + ", using 0 as default."));
            double high = ParseUtil.parseDoubleHandled(arr[1],
                    c -> log.warn("Failed to parse double from " + c.getInput() + ", using 0 as default."));

            return new Amount(low, high);
        } else {
            return new Amount(ParseUtil.parseDoubleHandled(str,
                    c -> log.warn("Failed to parse double from " + c.getInput() + ", using 0 as default.")));
        }
    }

    public int getInt() {
        return fixed ? (int) fixedValue : random.nextInt((int) highValue) + (int) lowValue;
    }

    public double getDouble() {
        return ParseUtil.roundDouble(fixed ? fixedValue : (random.nextDouble() * highValue) + lowValue, decimalPlaces);
    }

    public String toString() {
        return fixed ? String.valueOf(fixedValue) : lowValue + "-" + highValue;
    }

    public boolean isEmpty() {
        return fixed ? fixedValue == 0 : lowValue == 0 && highValue == 0;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Amount clone() {
        return new Amount(this);
    }
}