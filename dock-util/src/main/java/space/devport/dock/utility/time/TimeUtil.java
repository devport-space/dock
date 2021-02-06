package space.devport.dock.utility.time;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtil {

    /**
     * Extract a single time element.
     *
     * @param millis          Time in milliseconds
     * @param element         Which time element to extract
     * @param startingElement Is it the starting element?
     *                        Example: When input time is 61 000ms,
     *                        parsing it with startingElement true and element seconds would give you 61 as output.
     *                        With startingElement false it would output 1.
     * @return Parsed time element value
     */
    public int extractElement(long millis, TimeElement element, boolean startingElement) {

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