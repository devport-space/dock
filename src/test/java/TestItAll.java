import space.devport.utils.DevportUtils;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.itemutil.Amount;
import space.devport.utils.packutil.RewardPack;

public class TestItAll {

    public static void main(String[] args) {

        new DevportUtils(null);

        // -------- MessageBuilding, Formatting & Parsing --------

        // Create a MessageBuilder with placeholders
        MessageBuilder msg = new MessageBuilder("Hello %playerName%.", "Welcome to %worldName%!")
                .addPlaceholders("%playerName%", "%worldName%");

        // Fill the placeholders
        msg.fill("Wertik1206", "world");

        // Print it/Use it
        System.out.println(msg.parsePlaceholders().toString());

        // Set new message content
        msg.set("Players: %p1%, %p2%, %p3%, %p4%");

        // More placeholders
        msg.setPlaceholders("%p1%", "%p2%", "%p3%", "%p4%");

        // Fill them with values
        msg.fill("Player One", "Player Two", "Player Three", "Player Four");

        // Print
        System.out.println(msg.parsePlaceholders().toString());

        // ---------- Lombok Builder & Amount Random Value --------

        RewardPack pack = RewardPack.Builder().money(new Amount(1, 10)).build();

        System.out.println("-----------");

        System.out.println(pack.getMoney().toString() + " " + pack.getMoney().getInt());
        System.out.println(pack.getTokens().toString() + " " + pack.getTokens().getInt());

        pack = pack.toBuilder().tokens(new Amount(1, 20)).build();

        System.out.println("-----------");

        System.out.println(pack.getMoney().toString() + " " + pack.getMoney().getInt());
        System.out.println(pack.getTokens().toString() + " " + pack.getTokens().getInt());
    }
}