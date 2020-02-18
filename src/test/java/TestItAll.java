import space.devport.utils.Configuration;
import space.devport.utils.messageutil.MessageBuilder;

public class TestItAll {

    public static void main(String[] args) {
        // Create a MessageBuilder with placeholders
        MessageBuilder msg = new MessageBuilder("Hello %playerName%. Welcome to %worldName%!").addPlaceholders(new String[]{"%playerName%", "%worldName%"});

        // Fill the placeholders
        msg.fill(new String[]{"Wertik1206", "world"});

        // Print it/Use it
        System.out.println(msg.parsePlaceholders().toString());

        // Set new message content
        msg.set("Players: %p1%, %p2%, %p3%, %p4%");

        // More placeholders
        msg.setPlaceholders(new String[]{"%p1%", "%p2%", "%p3%", "%p4%"});

        // Fill them with values
        msg.fill(new String[]{"Player One", "Player Two", "Player Three", "Player Four"});

        // Print
        System.out.println(msg.parsePlaceholders().toString());
    }
}