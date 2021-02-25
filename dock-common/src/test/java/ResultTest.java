import org.junit.Test;
import space.devport.dock.common.Result;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ResultTest {

    private enum MyEnum {
        ONE,
        TWO,
        THREE,
        FOUR
    }

    @Test
    public void resultShouldHoldValuesCorrectly() {
        MyEnum value = ParseUtil.parseEnum("ONE", MyEnum.class).get();

        assertEquals(MyEnum.ONE, value);
    }

    // #get() should throw an exception when no value is present and we attempt to get.
    @Test
    public void resultShouldThrowCorrectly() {
        assertThrows(NoSuchElementException.class, () -> ParseUtil.parseEnum("INVALID", MyEnum.class).get());
    }

    @Test
    public void resultShouldDefaultCorrectly() {
        MyEnum value = ParseUtil.parseEnum("INVALID", MyEnum.class).get(MyEnum.ONE);

        assertEquals(MyEnum.ONE, value);
    }

    @Test
    public void resultShouldRunReactionsCorrectly() {

        // ifPresent

        AtomicBoolean ifPresent = new AtomicBoolean(false);
        AtomicBoolean ifEmpty = new AtomicBoolean(false);
        AtomicBoolean ifFailed = new AtomicBoolean(false);

        ParseUtil.parseEnum("ONE", MyEnum.class)
                .ifPresent(value -> ifPresent.set(true))
                .ifEmpty(() -> ifEmpty.set(true))
                .ifFailed(exception -> ifFailed.set(true));

        assertTrue(ifPresent.get());
        assertFalse(ifEmpty.get());
        assertFalse(ifFailed.get());

        ifFailed.set(false);
        ifEmpty.set(false);
        ifPresent.set(false);

        // ifEmpty

        ParseUtil.parseEnum("INVALID", MyEnum.class)
                .ifEmpty(() -> ifEmpty.set(true))
                .ifFailed(exception -> ifFailed.set(true))
                .ifPresent(value -> ifPresent.set(true));

        assertTrue(ifEmpty.get());
        assertTrue(ifFailed.get());
        assertFalse(ifPresent.get());

        ifFailed.set(false);
        ifEmpty.set(false);
        ifPresent.set(false);

        // ifFailed

        ParseUtil.parseEnum("INVALID", MyEnum.class)
                .ifFailed(exception -> ifFailed.set(true))
                .ifPresent(value -> ifPresent.set(true))
                .ifEmpty(() -> ifEmpty.set(true));

        assertTrue(ifFailed.get());
        assertTrue(ifEmpty.get());
        assertFalse(ifPresent.get());
    }

    @Test
    public void resultShouldThrowWhenAsked() {
        assertThrows(IllegalArgumentException.class, () -> ParseUtil.parseEnum("INVALID", MyEnum.class).throwException());
    }

    @Test
    public void generalUsageTest() {
        AtomicBoolean ifFailedReactions = new AtomicBoolean();
        AtomicBoolean ifEmptyReactions = new AtomicBoolean();

        Result.ResultReactions<MyEnum> reactions = result -> result
                .ifFailed(e -> ifFailedReactions.set(true))
                .ifEmpty(() -> ifEmptyReactions.set(true));

        AtomicBoolean ifPresent = new AtomicBoolean();
        AtomicBoolean ifFailed = new AtomicBoolean();

        // Define reactions in different cases.
        MyEnum myEnum = ParseUtil.parseEnum("INVALID", MyEnum.class)
                // Execute predefined reactions in the chain.
                .executeReactions(reactions)
                // If the value is present, verbose it out.
                .ifPresent(value -> ifPresent.set(true)) // This shouldn't get executed, value is not present.
                // If it failed with an exception, send an error or something.
                .ifFailed(exception -> ifFailed.set(true))
                // Supply a default
                .get(ParseUtil.parseEnum("ONE", MyEnum.class).toSupplier());

        // Reactions should get executed.
        assertTrue(ifFailedReactions.get());
        assertTrue(ifEmptyReactions.get());

        // Actions should get executed.
        assertTrue(ifFailed.get());
        // Value wasn't present at this time tho.
        assertFalse(ifPresent.get());

        // Assert result
        assertEquals(MyEnum.ONE, myEnum);
    }

    @Test
    public void emptyResultsShouldDiffer() {
        Result<String> resultOne = Result.empty();
        Result<String> resultTwo = Result.empty();

        resultOne.orElse("Fck you");

        assertThrows(NoSuchElementException.class, resultTwo::get);
    }
}
