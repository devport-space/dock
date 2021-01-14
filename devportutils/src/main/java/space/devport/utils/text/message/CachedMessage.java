package space.devport.utils.text.message;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Holds a Message "template" to build with, aka original,
 * parses everything into working message to allow reusability in placeholder parsing and color.
 *
 * @author Devport Team
 */
public class CachedMessage extends Message {

    @Getter
    private final List<String> original = new ArrayList<>();

    public CachedMessage(@NotNull Message message) {
        if (message instanceof CachedMessage) {
            clear();
            this.original.addAll(((CachedMessage) message).getOriginal());
            this.content.addAll(message.getContent());
        } else set(message.getContent());
    }

    public CachedMessage(@Nullable String... message) {
        this(Arrays.asList(message));
    }

    public CachedMessage(@Nullable Collection<String> message) {
        set(message);
    }

    public CachedMessage(@Nullable String line) {
        set(line);
    }

    @Override
    public @NotNull CachedMessage set(@Nullable Collection<String> message) {

        if (message == null) {
            clear();
            return this;
        }

        List<String> newContent = new ArrayList<>(message);
        clear();
        this.original.addAll(newContent);
        this.content.addAll(original);
        return this;
    }

    public CachedMessage setMessage(List<String> list) {
        this.content = list;
        return this;
    }

    @Override
    public CachedMessage insert(List<String> toAdd) {

        List<String> msg = new ArrayList<>(toAdd);
        msg.addAll(this.content);

        this.content.clear();
        this.content.addAll(msg);

        List<String> org = new ArrayList<>(toAdd);
        org.addAll(this.original);

        this.original.clear();
        this.original.addAll(org);
        return this;
    }

    /**
     * Append both the working message and the original.
     */
    @Override
    public CachedMessage append(List<String> toAdd) {
        this.original.addAll(toAdd);
        this.content.addAll(toAdd);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    // Pull the original in to working message
    public CachedMessage pull() {
        set(original);
        return this;
    }

    // Push the working message into original
    public CachedMessage push() {
        set(content);
        return this;
    }

    public void clear() {
        this.original.clear();
        this.content.clear();
    }

    @Override
    public CachedMessage clone() {
        return new CachedMessage(this);
    }
}