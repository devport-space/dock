package util;

import org.junit.Test;
import space.devport.dock.common.Result;
import space.devport.dock.util.ParseUtil;

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

    // #get() should throw the exception (nested inside RuntimeException) that happened during the value supply.
    @Test
    public void resultShouldThrowCorrectly() {
        assertThrows(RuntimeException.class, () -> ParseUtil.parseEnum("INVALID", MyEnum.class).get());
    }

    // #get() should throw NoSuchElementException when there's no value present
    @Test
    public void resultShouldThrowCorrectlyWhenEmpty() {
        assertThrows(NoSuchElementException.class, () -> Result.empty().get());
    }

    @Test
    public void resultShouldDefaultCorrectly() {
        MyEnum value = ParseUtil.parseEnum("INVALID", MyEnum.class).orElse(MyEnum.ONE);

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
                .orElseGet(ParseUtil.parseEnum("ONE", MyEnum.class).toSupplier());

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

        resultOne.orDefault("Fck you");

        assertThrows(NoSuchElementException.class, resultTwo::get);
    }

    @Test
    public void parseUtilUsage() {
        // Basic enum parsing.
        MyEnum four = ParseUtil.parseEnum("FOUR", MyEnum.class).get();

        assertEquals(MyEnum.FOUR, four);


        // Some Numbers or w/e.
        Object parsedInteger = ParseUtil.parseNumber("10").get();

        assertTrue(parsedInteger instanceof Integer);
    }

    @Test
    public void resultShouldCombineCorrectly() {

        // Basic combination, result should be correct.
        Result<Double> resultOne = Result.of(10D);

        Result<Double> resultTwo = Result.of(20D);

        double sum = Result.combine(resultOne, resultTwo, Double::sum)
                .orElse(0D);

        assertEquals(30, sum, 0);


        // When one of the results fail with exception, the final result should hold it.
        Result<Double> resultThree = Result.of(30D);
        Result<Double> exceptionalResult = Result.ofException(new IllegalArgumentException());

        Result<Double> addition = Result.combine(resultThree, exceptionalResult, Double::sum);

        assertThrows(RuntimeException.class, addition::throwException);


        // Using chained combine.
        Result<Double> resultFour = Result.of(30D)
                .combine(Result.of(10D), Double::sum);

        assertEquals(40D, resultFour.get(), 0);


        // Chained combine with exception.
        Result<Double> resultFive = Result.of(30D)
                .combine(Result.ofException(new IllegalArgumentException()), Double::sum);

        assertThrows(RuntimeException.class, resultFive::throwException);
    }

    @Test
    public void resultShouldMapCorrectly() {
        // Basic mapping.
        Result<String> stringResult = Result.of("10");

        Result<Double> doubleResult = stringResult.map(Double::parseDouble);
        assertEquals(10D, doubleResult.get(), 0);

        // Mapping should hold any exceptions that happened in the process.
        Result<String> invalidResult = Result.of("A");

        Result<Double> failedResult = invalidResult.map(Double::parseDouble);
        assertThrows(RuntimeException.class, failedResult::throwException);
    }
}
