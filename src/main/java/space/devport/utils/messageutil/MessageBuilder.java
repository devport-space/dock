package space.devport.utils.messageutil;

import lombok.Getter;
import org.bukkit.command.CommandSender;

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

    public MessageBuilder(MessageBuilder builder) {
        super(builder);

        this.message = new ArrayList<>(builder.getMessage());
        this.workingMessage = new ArrayList<>(builder.getWorkingMessage());
        this.colorChar = builder.getColorChar();
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

    // Send a parsed, colored message to sender
    public void send(CommandSender sender) {
        if (!isEmpty())
            sender.sendMessage(parsePlaceholders().color().toString());
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

    // Remove current message and set a new one with this as the first line.
    public MessageBuilder set(String message) {
        workingMessage.clear();
        this.message.clear();

        this.message.add(message);
        this.workingMessage.add(message);

        return this;
    }

    // Set message to a new one.
    public MessageBuilder set(List<String> message) {
        workingMessage = message;
        this.message = message;
        return this;
    }

    // Set message to a new one from array.
    public MessageBuilder set(String[] message) {
        return set(Arrays.asList(message));
    }

    // Check if the messages are empty
    public boolean isEmpty() {
        return message.isEmpty() && workingMessage.isEmpty();
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
        workingMessage = workingMessage.stream().map(line -> line.replace(placeholder, value)).collect(Collectors.toList());
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

    @Override
    public MessageBuilder copyPlaceholders(ParseFormat format) {
        super.copyPlaceholders(format);
        return this;
    }
}