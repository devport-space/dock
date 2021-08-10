package util;

import org.junit.Test;
import space.devport.dock.util.ParseUtil;

import static org.junit.Assert.assertEquals;

public class ParseUtilTest {

    private enum TestEnum {
        ONE,
        TWO
    }

    @Test
    public void parseUtilShouldParseCorrectly() {
        // Parse ints
        int a = ParseUtil.parseInteger("5").get();

        assertEquals(5, a);

        // Parse double
        double b = ParseUtil.parseDouble("5.0").orElse(null);

        assertEquals(5D, b, 0);

        // Parse enums
        TestEnum c = ParseUtil.parseEnum("one", TestEnum.class).orNull();

        assertEquals(TestEnum.ONE, c);

        // Parse Vectors
        // Vector d = ParseUtil.parseVector("1;1;1"); // method temporarily removed

        // assertEquals(new Vector(1, 1, 1), d);
    }

    @Test
    public void parseUtilShouldDefaultCorrectly() {
        // Fail a parse on purpose
        int a = ParseUtil.parseInteger("aaaa").orElse(1);

        assertEquals(1, a);

        double b = ParseUtil.parseDouble("aaaaa").orElse(2D);

        assertEquals(2D, b, 0);

        TestEnum c = ParseUtil.parseEnum("three", TestEnum.class).orElse(TestEnum.TWO);

        assertEquals(TestEnum.TWO, c);
    }
}
