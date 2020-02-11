package apitest;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.messageutil.MessageBuilder;

import java.util.Random;

public class RandomMessageBuilder extends MessageBuilder {

    // Holding out own variables etc. inheritance, you know how that works.
    private Random random = new Random();

    @Getter
    @Setter
    private int bound = 10;

    // Override parsePlaceholders method which is run when parsed.
    @Override
    public MessageBuilder parsePlaceholders() {

        // Parse random number in bound
        parse("%random%", String.valueOf(random.nextInt(bound)));

        return super.parsePlaceholders();
    }
}
