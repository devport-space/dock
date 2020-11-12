import org.junit.Test;
import space.devport.utils.text.message.CachedMessage;
import space.devport.utils.text.message.Message;

import static org.junit.Assert.assertEquals;

public class MessageTests {

    @Test
    public void cachedMessageShouldAppendCorrectly() {
        Message message = new CachedMessage();
        message.append("Hello world!");

        assertEquals("Hello world!", message.toString());
    }
}
