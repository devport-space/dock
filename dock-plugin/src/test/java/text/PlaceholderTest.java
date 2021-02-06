package text;

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import space.devport.dock.text.Placeholders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlaceholderTest {

    @Test
    public void placeholdersShouldParseCorrectly() {
        String text = "Hello %world%!";

        Placeholders placeholders = new Placeholders()
                .add("%world%", "awesome world");

        assertEquals("Hello awesome world!", placeholders.parse(text));

        text = "Hello %world%! This %world% is nice.";

        assertEquals("Hello awesome world! This awesome world is nice.", placeholders.parse(text));

        List<String> lines = Arrays.asList("Hello %world%!", "Am I still in %world%?");

        assertEquals(Arrays.asList("Hello awesome world!", "Am I still in awesome world?"), placeholders.parse(lines));
    }

    @Test
    public void placeholdersShouldReassignCorrectly() {
        String text = "This world is %subjective%%punctuation%";

        // Leaving out % should work just fine too.
        Placeholders placeholders = new Placeholders()
                .add("subjective", "great")
                .add("punctuation", "!");

        assertEquals("This world is great!", placeholders.parse(text));

        placeholders.add("subjective", "a nightmare")
                .add("punctuation", ".");

        assertEquals("This world is a nightmare.", placeholders.parse(text));
    }

    private static class Car {

        @Getter
        private final String name;
        @Getter
        @Setter
        private double range;

        public Car(String name, double range) {
            this.name = name;
            this.range = range;
        }
    }

    @Test
    public void placeholdersShouldHandleDynamicParsersCorrectly() {
        String text = "Car %name% with range of %range% miles.";

        Car lotus = new Car("Lotus", 600);

        Placeholders placeholders = new Placeholders()
                .addDynamicPlaceholder("%name%", Car::getName, Car.class)
                .addDynamicPlaceholder("%range%", Car::getRange, Car.class);

        assertEquals("Car Lotus with range of 600.0 miles.", placeholders
                .addContext(lotus)
                .parse(text));

        Car porsche = new Car("Porsche", 700);

        assertEquals("Car Porsche with range of 700.0 miles.", placeholders
                .addContext(porsche)
                .parse(text));
    }

    private static class SuperSport extends Car {

        @Getter
        private final double price;

        public SuperSport(String name, double range, double price) {
            super(name, range);
            this.price = price;
        }
    }

    @Test
    public void placeholdersShouldHandleSubClassesCorrectly() {
        String text = "Car %name% with range of %range% miles that costs %price%";

        Placeholders placeholders = new Placeholders()
                .addDynamicPlaceholder("%name%", Car::getName, Car.class)
                .addDynamicPlaceholder("%range%", Car::getRange, Car.class)
                .addDynamicPlaceholder("%price%", SuperSport::getPrice, SuperSport.class);

        Car nissan = new SuperSport("Nissan", 650, 670000);

        assertEquals("Car Nissan with range of 650.0 miles that costs 670000.0", placeholders
                .addContext(nissan)
                .parse(text));
    }

    @Test
    public void placeholdersShouldHandleAdditionalParsersCorrectly() {
        String text = "Car %name% with range of %range% miles.";

        Placeholders placeholders = new Placeholders()
                .addParser((input, object) -> {
                    input = input.replaceAll("(?i)%name%", object.getName());
                    input = input.replaceAll("(?i)%range%", String.valueOf(object.getRange()));
                    return input;
                }, Car.class);

        Car felicia = new Car("Felicia", 400);

        assertEquals("Car Felicia with range of 400.0 miles.", placeholders
                .addContext(felicia)
                .parse(text));
    }
}
