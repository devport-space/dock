package space.devport.utils.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtil {

    public int takeElement(long millis, TimeElement element, boolean startingElement) {

        long seconds = millis / 1000;

        int val = 0;
        for (TimeElement loopElement : TimeElement.values()) {

            if (startingElement && loopElement.getSeconds() > element.getSeconds()) continue;

            val = (int) (seconds / loopElement.getSeconds());
            seconds = seconds % loopElement.getSeconds();
            if (loopElement == element) return val;
        }
        return val;
    }
}