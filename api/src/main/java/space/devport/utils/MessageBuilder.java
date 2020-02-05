package space.devport.utils;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    // Holds the original
    @Getter
    private List<String> message = new ArrayList<>();

    // Holds string used for parsing etc.
    @Getter
    private List<String> workingMessage = new ArrayList<>();

    // Parse format to use.
    @Getter
    private ParseFormat parseFormat = new ParseFormat();

    public MessageBuilder setFormat(ParseFormat parseFormat) {
        this.parseFormat = parseFormat;
        return this;
    }

    // Character to parse colors with
    @Getter
    private char colorChar = '&';

    @Getter
    private String lineSeparator = "\n";

    public MessageBuilder setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    public MessageBuilder() {
    }

    public MessageBuilder(ParseFormat format) {
        this.parseFormat = format;
    }

    public MessageBuilder(List<String> message) {
        this.message = message;
        this.workingMessage = message;
    }

    public MessageBuilder(String str) {
        message.add(str);
        workingMessage.add(str);
    }

    // Pull the original message again
    public void pull() {
        workingMessage.clear();
        workingMessage.addAll(message);
    }

    // Set current working message as the original
    // aka push
    public void push() {
        message.clear();
        message.addAll(workingMessage);
    }

    // Can be implied to handle custom parsing.
    public MessageBuilder parsePlaceholders() {

        // Use parse format to parse placeholders.
        for (String line : workingMessage)
            // Parse it and set back to working message
            workingMessage.set(workingMessage.indexOf(line), parseFormat.parse(line));

        return this;
    }

    // Set line chars to parse correctly.
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String line : workingMessage)
            stringBuilder.append(line).append(lineSeparator);

        return stringBuilder.toString();
    }

    // Parse a single placeholder
    public MessageBuilder parse(String placeholder, String value) {

        for (String line : workingMessage) {
            // Replace placeholder
            String newLine = line.replaceAll(placeholder, value);

            // Set to message
            workingMessage.set(workingMessage.indexOf(line), newLine);
        }

        return this;
    }

    public MessageBuilder addLine(String line) {
        message.add(line);
        workingMessage.add(line);
        return this;
    }

    // Color the message using default color character
    public MessageBuilder color() {

        for (String line : workingMessage)
            workingMessage.set(workingMessage.indexOf(line), color(line));

        return this;
    }

    // Color the message using specified color character
    public MessageBuilder color(char colorChar) {
        return this;
    }

    public MessageBuilder setColorChar(char colorChar) {
        this.colorChar = colorChar;
        return this;
    }

    private String color(String msg) {
        return msg != null ? ChatColor.translateAlternateColorCodes(colorChar, msg) : null;
    }
}
