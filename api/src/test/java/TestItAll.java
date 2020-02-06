import apitest.RandomMessageBuilder;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;

public class TestItAll {

    public void doSomething() {

        // Initialize a ParseFormat with a %test% placeholder and a default value
        ParseFormat parseFormat = new ParseFormat()
                .addPlaceholder("%test%")
                .setDefaultValue("Unknown");

        int testValue = 15;

        // Should fill the first placeholder
        parseFormat.fill(new String[]{String.valueOf(testValue)});

        // Create a message builder with our ParseFormat
        RandomMessageBuilder messageBuilder = (RandomMessageBuilder) new RandomMessageBuilder()
                .setFormat(parseFormat)
                .addLine("The test output is: %test%")
                .addLine("And random is: %random%")
                .parsePlaceholders();

        System.out.println(messageBuilder.toString());

        // Test using the parse format again.
        // Placeholders are added in reverse order, from newest to oldest.
        // But fill makes up for it! It reverses the input. ;)
        parseFormat.addPlaceholder("%test_2%")
                .fill(new String[]{String.valueOf(23), String.valueOf(78)});

        MessageBuilder builder = new MessageBuilder(parseFormat)
                .addLine("The second test output: %test%")
                .addLine("And the third one: %test_2%")
                .parsePlaceholders();

        System.out.println(builder.toString());
    }
}