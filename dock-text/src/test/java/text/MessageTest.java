package text;

import org.junit.Test;
import space.devport.dock.text.placeholders.Placeholders;
import space.devport.dock.text.message.CachedMessage;
import space.devport.dock.text.message.Message;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    @Test
    public void cachedMessageShouldAppendCorrectly() {
        Message message = new CachedMessage();
        message.append("Hello world!");

        assertEquals("Hello world!", message.toString());
    }

    @Test
    public void cachedMessageShouldFunctionProperly() {
        CachedMessage cachedMessage = new CachedMessage("Message with a %placeholder%");
        cachedMessage.parseWith(new Placeholders().add("%placeholder%", "Placeholder!"));

        List<String> result = cachedMessage.parse().getContent();
        cachedMessage.pull();

        assertEquals("Message with a Placeholder!", result.get(0));
        assertEquals("Message with a %placeholder%", cachedMessage.toString());
    }
}
