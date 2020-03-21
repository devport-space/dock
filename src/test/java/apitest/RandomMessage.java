package apitest;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.text.Message;

import java.util.Random;

public class RandomMessage extends Message {

    // Holding out own variables etc. inheritance, you know how that works.
    private final Random random = new Random();

    @Getter
    @Setter
    private int bound = 10;

    // Override parsePlaceholders method which is run when parsed.
    @Override
    public Message parsePlaceholders() {

        // Parse random number in bound
        replace("%random%", String.valueOf(random.nextInt(bound)));

        return super.parsePlaceholders();
    }
}