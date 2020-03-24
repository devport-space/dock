import space.devport.utils.DevportUtils;
import space.devport.utils.text.Message;
import space.devport.utils.text.Placeholders;

public class TestItAll {

    public static void main(String[] args) {

        new DevportUtils(null);

        // -------- MessageBuilding, Formatting & Parsing --------

        // Create a MessageBuilder with placeholders
        Message msg = new Message("Hello %playerName%.", "Welcome to %worldName%!");

        Placeholders placeholders = new Placeholders()
                .add("%playerName%", "Wertik1206")
                .add("%worldName%", "world");

        // Print it/Use it
        System.out.println(msg.parseWith(placeholders).toString());
    }
}