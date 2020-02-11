package space.devport.utils.messageutil;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageBuilder {

    // TODO Add documentation once done.

    // Holds the original message.
    @Getter
    private List<String> message = new ArrayList<>();

    // Holds the current message being edited.
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

    // Default class constructor
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
    public void push() {
        message.clear();
        message.addAll(workingMessage);
    }

    // Uses ParseFormat to parse stored placeholders
    // Can be implied to handle custom parsing when extending this class
    public MessageBuilder parsePlaceholders() {
        workingMessage = workingMessage.stream().map(line -> parseFormat.parse(line)).collect(Collectors.toList());
        return this;
    }

    // Set line chars to parse correctly.
    public String toString() {
        return StringUtil.toMultilineString(workingMessage);
    }

    // Parse a single placeholder in the whole message.
    public MessageBuilder parse(String placeholder, String value) {
        workingMessage = workingMessage.stream().map(line -> line.replaceAll(placeholder, value)).collect(Collectors.toList());
        return this;
    }

    // Add a single line to both working & original message.
    public MessageBuilder addLine(String line) {
        message.add(line);
        workingMessage.add(line);
        return this;
    }

    // Color the message using default color character
    public MessageBuilder color() {
        workingMessage = StringUtil.color(workingMessage);
        return this;
    }

    public MessageBuilder setColorChar(char colorChar) {
        this.colorChar = colorChar;
        return this;
    }
}