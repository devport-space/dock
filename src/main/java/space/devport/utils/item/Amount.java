package space.devport.utils.item;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class Amount {

    private final Random random = new Random();

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

    public int getInt() {
        return fixed ? (int) fixedValue : random.nextInt((int) highValue) + (int) lowValue;
    }

    public double getDouble() {
        return fixed ? fixedValue : (random.nextDouble() * highValue) + lowValue;
    }

    public String toString() {
        return fixed ? String.valueOf(fixedValue) : lowValue + "-" + highValue;
    }
}