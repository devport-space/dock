package space.devport.utils.text.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.struct.Context;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class to handle Messages.
 *
 * @author Devport Team
 **/
@NoArgsConstructor
public class Message {

    @Getter
    protected List<String> message = new ArrayList<>();

    @Getter
    protected transient Placeholders placeholders = new Placeholders();

    /**
     * Copy constructor.
     *
     * @param message Message to copy
     */
    public Message(@Nullable Message message) {
        set(message);
        this.placeholders = message == null ? new Placeholders() : new Placeholders(message.getPlaceholders());
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

    public Message setPlaceholders(Placeholders placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public Message parse(Object... objects) {
        return context(objects).parse();
    }

    // Parse placeholders
    public Message parse() {
        this.message = this.message.stream().map(l -> placeholders.parse(l)).collect(Collectors.toList());
        return this;
    }

    public Message parseWith(Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    public Message context(Object... objects) {
        this.placeholders.addContext(objects);
        return this;
    }

    public Message context(Context context) {
        this.placeholders.addContext(context);
        return this;
    }

    /**
     * Set the message.
     * If the input is null, sets message to a blank one.
     *
     * @param message Message to set to in a List
     * @return MessageBuilder object
     */
    public Message set(@Nullable List<String> message) {
        this.message = message != null ? new ArrayList<>(message) : new ArrayList<>();
        return this;
    }

    public Message set(@Nullable Message message) {
        if (message == null) return set(new ArrayList<>());
        return set(message.getMessage());
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
     *
     * @param message Message to set, in an Array
     * @return MessageBuilder object
     */
    public Message set(@Nullable String... message) {
        return set(new ArrayList<>(Arrays.asList(message)));
    }

    /**
     * Check if the message is empty.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return message == null || message.isEmpty();
    }

    /**
     * Parse a single placeholder and value.
     *
     * @param placeholder Key to look for
     * @param value       Value to replace with
     * @return MessageBuilder object
     */
    public Message replace(@Nullable String placeholder, @Nullable Object value) {
        if (placeholder == null || value == null || message == null)
            return this;

        message = message.stream()
                .map(line -> !line.isEmpty() ? line.replaceAll("(?i)" + placeholder, value.toString()) : "")
                .collect(Collectors.toList());
        return this;
    }

    // ---- Add a line / lines ----

    // Prefix the first line
    public Message prefix(String str) {
        if (isEmpty())
            return this;

        this.message.set(0, str + this.message.get(0));
        return this;
    }

    public Message insert(List<String> toAdd) {
        toAdd = new ArrayList<>(toAdd);
        toAdd.addAll(message);
        return set(toAdd);
    }

    // Add something to the front
    public Message insert(String... toAdd) {
        return insert(Arrays.asList(toAdd));
    }

    public Message insert(Message toAdd) {
        return insert(toAdd.getMessage());
    }

    public Message append(List<String> toAdd) {
        message.addAll(new ArrayList<>(toAdd));
        return this;
    }

    public Message append(String... toAdd) {
        return append(Arrays.asList(toAdd));
    }

    public Message append(Message message) {
        return append(message.getMessage());
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
        return StringUtil.listToString(message, "\n");
    }

    public void send(Player player) {
        send(player, player);
    }

    public void send(CommandSender sender, Object... context) {
        send(sender, new Context(context));
    }

    public void send(CommandSender sender, Context context) {
        Context oldContext = new Context(this.placeholders.getContext());
        this.context(context).send(sender);
        this.placeholders.setContext(oldContext);
    }

    /**
     * Parse placeholders, color the message and send.
     *
     * @param sender CommandSender to send to
     */
    public void send(@Nullable CommandSender sender) {

        if (sender == null || isEmpty()) return;

        sender.sendMessage(parse().color().toString());
    }

    /**
     * Send a message prefixed with the DevportPlugin prefix.
     *
     * @param sender CommandSender to send to
     */
    public void sendPrefixed(CommandSender sender) {

        if (sender == null) return;

        if (!isEmpty()) {
            String prefix = DevportPlugin.getInstance().getPrefix();
            String message = StringUtil.color((prefix == null ? "" : prefix) + toString());
            sender.sendMessage(message == null ? "" : message);
        }
    }

    public Message map(Function<String, String> action) {
        this.message = this.message.stream().map(action).collect(Collectors.toList());
        return this;
    }

    public Message forEach(Consumer<String> action) {
        this.message.forEach(action);
        return this;
    }

    public Message filter(Predicate<String> action) {
        this.message.removeIf(action);
        return this;
    }
}