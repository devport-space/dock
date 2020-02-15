package space.devport.utils.messageutil;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageBuilder extends ParseFormat {

    // Holds the original message.
    @Getter
    private List<String> message = new ArrayList<>();

    // Holds the current message being edited.
    @Getter
    private List<String> workingMessage = new ArrayList<>();

    // Character to parse colors with
    @Getter
    private char colorChar = '&';

    // Default class constructor
    public MessageBuilder() {
    }

    public MessageBuilder(String[] message) {
        set(message);
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

    public MessageBuilder set(String message) {
        workingMessage.clear();
        this.message.clear();

        this.message.add(message);
        this.workingMessage.add(message);

        return this;
    }

    public MessageBuilder set(List<String> message) {
        workingMessage = message;
        this.message = message;
        return this;
    }

    public MessageBuilder set(String[] message) {
        set(Arrays.asList(message));
        return this;
    }

    // Uses ParseFormat to parse stored placeholders
    // Can be implied to handle custom parsing when extending this class
    public MessageBuilder parsePlaceholders() {
        workingMessage = workingMessage.stream().map(this::parse).collect(Collectors.toList());
        return this;
    }

    // Set line chars to parse correctly.
    public String toString() {
        return StringUtil.toMultilineString(workingMessage);
    }

    // Parse a single placeholder in the whole message.
    public MessageBuilder parsePlaceholder(String placeholder, String value) {
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

    @Override
    public MessageBuilder addPlaceholder(String placeholder) {
        super.addPlaceholder(placeholder);
        return this;
    }

    @Override
    public MessageBuilder fill(String placeholder, String value) {
        super.fill(placeholder, value);
        return this;
    }

    @Override
    public MessageBuilder setDefaultValue(String defaultValue) {
        super.setDefaultValue(defaultValue);
        return this;
    }

    @Override
    public MessageBuilder addPlaceholder(String placeholder, String value) {
        super.addPlaceholder(placeholder, value);
        return this;
    }

    @Override
    public MessageBuilder addPlaceholders(String[] placeholders) {
        super.addPlaceholders(placeholders);
        return this;
    }

    @Override
    public MessageBuilder fill(String[] arguments) {
        super.fill(arguments);
        return this;
    }

    @Override
    public MessageBuilder setPlaceholders(String[] placeholders) {
        super.setPlaceholders(placeholders);
        return this;
    }
}