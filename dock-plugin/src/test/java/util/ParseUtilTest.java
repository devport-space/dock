package util;

import org.bukkit.util.Vector;
import org.junit.Test;
import space.devport.dock.callbacks.ExceptionCallback;
import space.devport.dock.utility.ParseUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ParseUtilTest {

    private enum TestEnum {
        ONE,
        TWO
    }

    @Test
    public void parseUtilShouldParseCorrectly() {
        // Parse ints
        int a = ParseUtil.parseInteger("5");

        assertEquals(5, a);

        // Parse double
        double b = ParseUtil.parseDouble("5.0");

        assertEquals(5D, b, 0);

        // Parse enums
        TestEnum c = ParseUtil.parseEnum("one", TestEnum.class);

        assertEquals(TestEnum.ONE, c);

        // Parse Vectors
        Vector d = ParseUtil.parseVector("1;1;1");

        assertEquals(new Vector(1, 1, 1), d);
    }

    @Test
    public void parseUtilShouldDefaultCorrectly() {
        // Fail a parse on purpose
        int a = ParseUtil.parseInteger("aaaa", 1);

        assertEquals(1, a);

        double b = ParseUtil.parseDouble("aaaaa", 2D);

        assertEquals(2D, b, 0);

        TestEnum c = ParseUtil.parseEnum("three", TestEnum.class, TestEnum.TWO);

        assertEquals(TestEnum.TWO, c);
    }

    @Test
    public void parseUtilShouldCallbackProperly() {
        AtomicBoolean callback = new AtomicBoolean();

        int a = ParseUtil.parseIntegerHandled("aaa", -1, e -> callback.set(true));

        assertTrue(callback.getAndSet(false));
        assertEquals(-1, a);

        double b = ParseUtil.parseDoubleHandled("aaaaa", e -> callback.set(true));

        assertTrue(callback.getAndSet(false));
        assertEquals(0, b, 0);

        TestEnum c = ParseUtil.parseEnumHandled("three", TestEnum.class, (ExceptionCallback) e -> callback.set(true));

        assertTrue(callback.get());
        assertNull(c);
    }
}
