package space.devport.dock.text.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.struct.Context;
import space.devport.dock.text.Placeholders;
import space.devport.dock.text.StringUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


//TODO: Create a common interface instead of extending this class directly in subclasses.
@NoArgsConstructor
public class Message implements Cloneable {

    protected List<String> content = new LinkedList<>();

    @Getter
    protected transient Placeholders placeholders = new Placeholders();

    /**
     * Copy constructor.
     *
     * @param message Message to copy.
     */
    private Message(@Nullable Message message) {
        copy(message);

        if (message != null)
            this.placeholders = message.getPlaceholders().clone();
    }

    /**
     * Array constructor.
     *
     * @param content Array to construct with
     */
    public Message(@Nullable String... content) {
        set(content);
    }

    /**
     * List constructor.
     *
     * @param content List to construct with
     */
    public Message(@Nullable List<String> content) {
        set(content);
    }

    /**
     * Single line constructor.
     *
     * @param line Line in string
     */
    public Message(@Nullable String line) {
        set(line);
    }

    @NotNull
    public static Message of(Message message) {
        return new Message(message);
    }

    /**
     * Set this {@link Message} content.
     * <p>
     * If the input is null, use an empty list.
     *
     * @param message {@code Collection<String>} to use.
     * @return This {@link Message} instance.
     */
    @NotNull
    public Message set(@Nullable Collection<String> message) {
        this.content = message == null ? new ArrayList<>() : new ArrayList<>(message);
        return this;
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
        return set(Arrays.asList(message));
    }

    @NotNull
    public Message copy(@Nullable Message message) {
        if (message == null)
            return set(new LinkedList<>());
        return set(message.getContent());
    }

    /**
     * Check if the message is empty.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    /**
     * Parse a single placeholder and value.
     *
     * @param placeholder Key to look for
     * @param value       Value to replace with
     * @return MessageBuilder object
     */
    public Message replace(@Nullable String placeholder, @Nullable Object value) {
        if (placeholder == null || value == null || isEmpty())
            return this;

        content = content.stream()
                .map(line -> !line.isEmpty() ? line.replaceAll("(?i)" + placeholder, String.valueOf(value)) : "")
                .collect(Collectors.toList());
        return this;
    }

    public Message setPlaceholders(Placeholders placeholders) {
        this.placeholders = placeholders;
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

    // ---- Add a line / lines ----

    // Prefix the first line
    public Message prefix(String str) {
        if (isEmpty())
            return this;

        this.content.set(0, str + this.content.get(0));
        return this;
    }

    public Message insert(List<String> toAdd) {
        toAdd = new ArrayList<>(toAdd);
        toAdd.addAll(content);
        return set(toAdd);
    }

    // Add something to the front
    public Message insert(String... toAdd) {
        return insert(Arrays.asList(toAdd));
    }

    public Message insert(Message toAdd) {
        return insert(toAdd.getContent());
    }

    public Message append(List<String> toAdd) {
        content.addAll(new ArrayList<>(toAdd));
        return this;
    }

    public Message append(String... toAdd) {
        return append(Arrays.asList(toAdd));
    }

    public Message append(Message message) {
        return append(message.getContent());
    }

    public Message parse(Object... objects) {
        return context(objects).parse();
    }

    // Parse placeholders
    public Message parse() {
        this.content = this.content.stream().map(l -> placeholders.parse(l)).collect(Collectors.toList());
        return this;
    }

    /**
     * Color the message.
     *
     * @return MessageBuilder object
     */
    public Message color() {
        content = StringUtil.color(content);
        return this;
    }

    public Message color(char colorChar) {
        content = StringUtil.color(content, colorChar);
        return this;
    }

    // Parse, color and convert to String.
    public String build() {
        return parse().color().toString();
    }

    /**
     * Parse message to string.
     * Multiple lines are separated by given delimiter, or default.
     *
     * @param delimiter Delimiter to divide lines by.
     * @return Parsed String or null if delimiter is null.
     */
    @Contract("null -> null")
    @Nullable
    public String toString(@Nullable String delimiter) {
        return StringUtil.join(content, delimiter);
    }

    /**
     * Parse message to string.
     * <p>
     * Equivalent to {@link Message#toString(String)} passed ("\n").
     *
     * @return Parsed String.
     * @see Message#toString(String)
     */
    @Override
    public String toString() {
        return StringUtil.join(content, "\n");
    }

    /*
     * Send without setting context.
     */
    public void sendTo(Player player) {
        send((CommandSender) player);
    }

    /**
     * Add {@link Player} to {@link Placeholders} {@link Context} and send him this {@link Message}.
     * <p>
     * Parses attached {@link Placeholders} and colors using {@link #color()} before sending.
     *
     * @param player {@link Player} to send this message to.
     * @see Placeholders
     * @see Context
     * @see Player
     */
    public void send(@NotNull Player player) {
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
     * Send this message to {@link CommandSender}.
     * <p>
     * Parses attached {@link Placeholders} and colors using {@link #color()} before sending.
     * <p>
     * Doesn't proceed to send the message if {@link Message#isEmpty()} returns true or sender is {@code null}.
     *
     * @param sender {@link CommandSender} to send this message to.
     */
    public void send(@Nullable CommandSender sender) {

        if (sender == null || isEmpty())
            return;

        sender.sendMessage(parse().color().toString());
    }

    /**
     * Send a message starting with %prefix%.
     * Note: Requires Message to be parsed using global placeholders.
     *
     * @param sender CommandSender to send to
     * @see space.devport.dock.text.language.LanguageManager#getPrefixed(String)
     * @see space.devport.dock.text.language.LanguageManager#send(CommandSender, String)
     * @deprecated Will be removed upon 4.x release as it is essentially useless.
     * Use {@link space.devport.dock.text.language.LanguageManager#getPrefixed(String)} to send a prefixed message properly.
     */
    public void sendPrefixed(CommandSender sender) {

        if (sender == null)
            return;

        if (!isEmpty()) {
            String message = StringUtil.color(placeholders.getPlaceholderCache().containsKey("prefix") ?
                    placeholders.parse("%prefix%") + toString() : toString());
            sender.sendMessage(message);
        }
    }

    public Message map(Function<String, String> action) {
        this.content = this.content.stream().map(action).collect(Collectors.toList());
        return this;
    }

    public Message forEach(Consumer<String> action) {
        this.content.forEach(action);
        return this;
    }

    public Message filter(Predicate<String> action) {
        this.content.removeIf(action);
        return this;
    }

    /*
     * Returns a copy of the contained message.
     */
    public List<String> getContent() {
        return new ArrayList<>(content);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Message clone() {
        return new Message(this);
    }
}