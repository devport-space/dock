package space.devport.dock.util.time;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.util.ParseUtil;

public enum TimeElement {

    WEEK(604800),
    DAY(86400),
    HOUR(3600),
    MINUTE(60),
    SECOND(1);

    @Getter
    private final int seconds;

    TimeElement(int seconds) {
        this.seconds = seconds;
    }

    @Nullable
    public static TimeElement fromString(@Nullable String str) {
        return ParseUtil.parseEnum(str, TimeElement.class);
    }
}
