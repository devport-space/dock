package space.devport.utils.messageutil;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Object to handle Message building.
 *
 * @author Devport Team
 **/
@NoArgsConstructor
public class MessageBuilder extends ParseFormat {

    // Holds the original message.
    @Getter
    private List<String> message = new ArrayList<>();

    // Holds the current message being edited.
    @Getter
    private List<String> workingMessage = new ArrayList<>();

    // Character to parse colors with.
    @Getter
    private char colorChar = '&';

    /**
     * Copy constructor.
     */
    public MessageBuilder(@NotNull MessageBuilder builder) {
        // Call parse format copy constructor
        super(builder);

        this.message = new ArrayList<>(builder.getMessage());
        this.workingMessage = new ArrayList<>(builder.getWorkingMessage());
        this.colorChar = builder.getColorChar();
    }

    /**
     * Array constructor.
     */
    public MessageBuilder(@Nullable String[] message) {
        set(message);
    }

    /**
     * List constructor.
     */
    public MessageBuilder(@Nullable List<String> message) {
        set(message);
    }

    /**
     * Single line constructor.
     */
    public MessageBuilder(@Nullable String str) {
        set(str);
    }

    /**
     * Parse placeholders, color the message and send.
     *
     * @param sender    CommandSender to send to
     * @param sendEmpty Optional boolean, whether so send if empty or not
     */
    public void send(@NotNull CommandSender sender, boolean... sendEmpty) {
        if (!isEmpty() && !(sendEmpty.length > 0 && sendEmpty[0]))
            sender.sendMessage(parsePlaceholders().color().toString());
    }

    /**
     * Pull original message to working.
     *
     * @return MessageBuilder object
     */
    public MessageBuilder pull() {
        workingMessage.clear();
        workingMessage.addAll(message);
        return this;
    }

    /**
     * Push working message to original.
     *
     * @return MessageBuilder object
     */
    public MessageBuilder push() {
        message.clear();
        message.addAll(workingMessage);
        return this;
    }

    /**
     * Set message to a single line.
     *
     * @param message String line to set
     * @return MessageBuilder object
     */
    public MessageBuilder set(@Nullable String message) {
        // Clear the messages
        this.workingMessage.clear();
        this.message.clear();

        // Add the line to them
        this.message.add(message);
        this.workingMessage.add(message);

        return this;
    }

    /**
     * Set the message.
     * If the input is null, sets message to a blank one.
     *
     * @param message Message to set to in a List
     * @return MessageBuilder object
     */
    public MessageBuilder set(@Nullable List<String> message) {
        // If the input is null, set to blank
        if (message == null)
            message = new ArrayList<>();

        this.workingMessage = message;
        this.message = message;
        return this;
    }


    /**
     * Set the message.
     *
     * @param message Message to set, in an Array
     * @return MessageBuilder object
     */
    public MessageBuilder set(@Nullable String[] message) {
        set(Arrays.asList(message));
        return this;
    }

    /**
     * Check if the messages are empty.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return message.isEmpty() && workingMessage.isEmpty();
    }

    /**
     * Parse placeholders & their values stored in cache.
     *
     * @return MessageBuilder object
     */
    public MessageBuilder parsePlaceholders() {
        workingMessage = workingMessage.stream().map(this::parse).collect(Collectors.toList());
        return this;
    }

    /**
     * Parse message to string.
     * Multiple lines are separated by given delimiter, or default.
     *
     * @param delimiter Optional String, delimiter to use
     * @return Parsed String
     */
    @NotNull
    public String toString(@NotNull String... delimiter) {
        if (delimiter.length > 0)
            return StringUtil.toMultilineString(workingMessage, delimiter[0]);
        else
            return StringUtil.toMultilineString(workingMessage);
    }

    /**
     * Parse a single placeholder & value in the working message.
     *
     * @param placeholder Key to look for
     * @param value       Value to replace with
     * @return MessageBuilder object
     */
    public MessageBuilder parsePlaceholder(@NotNull String placeholder, @NotNull String value) {
        workingMessage = workingMessage.stream()
                .map(line -> line.replace(placeholder, value))
                .collect(Collectors.toList());
        return this;
    }

    /**
     * Add a single line to both original and working message.
     *
     * @param line String line
     * @return MessageBuilder object
     */
    public MessageBuilder addLine(@NotNull String line) {
        message.add(line);
        workingMessage.add(line);
        return this;
    }

    /**
     * Color the message.
     *
     * @return MessageBuilder object
     */
    public MessageBuilder color() {
        workingMessage = StringUtil.color(workingMessage, colorChar);
        return this;
    }

    /**
     * Set the color char.
     *
     * @param colorChar Color character to set
     * @return MessageBuilder object
     */
    public MessageBuilder setColorChar(char colorChar) {
        this.colorChar = colorChar;
        return this;
    }

    // --------------- Methods overridden from ParseFormat ---------------------

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