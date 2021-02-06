package util;

import org.junit.Test;
import space.devport.dock.text.StringUtil;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

    private final static class Car {
        private final String name;
        private final double range;

        public Car(String name, double range) {
            this.name = name;
            this.range = range;
        }

        @Override
        public String toString() {
            return String.format("%s with range %.1f", name, range);
        }
    }

    @Test
    public void stringUtilShouldJoinCorrectly() {
        String joined = StringUtil.join(", ", new Car("Porsche", 500), new Car("Nissan", 600));

        assertEquals("Porsche with range 500.0, Nissan with range 600.0", joined);
    }
}
