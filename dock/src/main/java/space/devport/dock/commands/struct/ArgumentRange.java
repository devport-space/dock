package space.devport.dock.commands.struct;

import lombok.Getter;

/**
 * Determines either a dynamic ( min and max ), or fixed command argument length.
 * Doesn't count the subcommand argument.
 */
public class ArgumentRange {

    @Getter
    private final int min;

    @Getter
    private final int max;

    public ArgumentRange(int wanted) {
        this.min = wanted;
        this.max = wanted;
    }

    public ArgumentRange(int min, int max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    /**
     * Compare our Range to an argument length.
     *
     * @param num Length of arguments to compare
     * @return 1 if the argument length is too much, -1 if it's not enough, 0 if it's right on spot.
     */
    public int compare(int num) {
        if (num > max)
            return 1;
        else if (num < min)
            return -1;
        else return 0;
    }
}