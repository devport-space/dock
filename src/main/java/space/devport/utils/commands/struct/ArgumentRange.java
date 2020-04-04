package space.devport.utils.commands.struct;

import lombok.Getter;

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

    public int check(int num) {
        if (num > max)
            return 1;
        else if (num < min)
            return -1;
        else return 0;
    }
}