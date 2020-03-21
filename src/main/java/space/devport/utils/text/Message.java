package space.devport.utils.text;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;

import java.util.ArrayDeque;
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
public class Message {

    @Getter
    private List<String> message = new ArrayList<>();

    @Getter
    private Placeholders placeholders = new Placeholders();

    public Message setPlaceholders(Placeholders placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    /**
     * Copy constructor.
     *
     * @param message Message to copy
     */
    public Message(@NotNull Message message) {
        this.message = new ArrayList<>(message.getMessage());
        this.placeholders = message.getPlaceholders();
    }

    /**
     * Array constructor.
     *
     * @param message Array to construct with
     */
    public Message(@Nullable String... message) {
        set(message);
    }

    /**
     * List constructor.
     *
     * @param message List to construct with
     */
    public Message(@Nullable List<String> message) {
        set(message);
    }

    /**
     * Single line constructor.
     *
     * @param line Line in string
     */
    public Message(@Nullable String line) {
        set(line);
    }

    /**
     * Parse placeholders, color the message and send.
     *
     * @param sender CommandSender to send to
     */
    public void send(@NotNull CommandSender sender) {
        if (!isEmpty())
            sender.sendMessage(parse().color().toString());
    }

    public void sendPrefixed(CommandSender sender) {
        if (!isEmpty())
            sender.sendMessage(DevportPlugin.getInstance().getPrefix() + color().toString());
    }

    // Parse placeholders
    public Message parse() {
        return placeholders.parse(this);
    }

    /**
     * Set message to a single line.
     *
     * @param message String line to set
     * @return MessageBuilder object
     */
    public Message set(@Nullable String message) {
        return set(new String[]{message});
    }

    /**
     * Set the message.
     * If the input is null, sets message to a blank one.
     *
     * @param message Message to set to in a List
     * @return MessageBuilder object
     */
    public Message set(@Nullable List<String> message) {
        // If the input is null, set to blank
        if (message == null)
            message = new ArrayList<>();

        this.message = message;
        return this;
    }

    /**
     * Set the message.
     *
     * @param message Message to set, in an Array
     * @return MessageBuilder object
     */
    public Message set(@Nullable String... message) {
        set(Arrays.asList(message));
        return this;
    }

    /**
     * Check if the messages are empty.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return message.isEmpty();
    }

    /**
     * Parse a single placeholder and value.
     *
     * @param placeholder Key to look for
     * @param value       Value to replace with
     * @return MessageBuilder object
     */
    public Message replace(@NotNull String placeholder, @NotNull Object value) {
        message = message.stream()
                .map(line -> line.replace(placeholder, value.toString()))
                .collect(Collectors.toList());
        return this;
    }

    // ---- Add a line / lines ----

    public Message append(String... toAdd) {
        return append(Arrays.asList(toAdd));
    }

    public Message append(List<String> toAdd) {
        message.addAll(toAdd);
        return this;
    }

    public Message append(Message message) {
        return append(message.getMessage());
    }

    // Add something to the front
    public Message insert(String... toAdd) {
        return insert(Arrays.asList(toAdd));
    }

    public Message insert(List<String> toAdd) {
        ArrayDeque<String> deque = new ArrayDeque<>(message);
        for (String str : toAdd) deque.push(str);
        message = new ArrayList<>(deque);
        return this;
    }

    // Add something to the front
    public Message insert(Message toAdd) {
        return insert(toAdd.getMessage());
    }

    /**
     * Color the message.
     *
     * @return MessageBuilder object
     */
    public Message color() {
        message = StringUtil.color(message);
        return this;
    }

    public Message color(char colorChar) {
        message = StringUtil.color(message, colorChar);
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
    public String toString(@NotNull String delimiter) {
        return StringUtil.listToString(message, delimiter);
    }

    /**
     * Parse message to string.
     *
     * @return Parsed String
     */
    @Override
    public String toString() {
        return StringUtil.listToString(message);
    }
}