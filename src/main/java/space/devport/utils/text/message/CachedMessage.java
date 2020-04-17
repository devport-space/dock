package space.devport.utils.text.message;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a Message "template" to build with, aka original,
 * parses everything into working message to allow reusability in placeholder parsing and color.
 *
 * @author Devport Team
 */
public class CachedMessage extends Message {

    @Getter
    private List<String> original = new ArrayList<>();

    public CachedMessage(@NotNull Message message) {
        super(message);
    }

    public CachedMessage(@Nullable String... message) {
        super(message);
    }

    public CachedMessage(@Nullable List<String> message) {
        super(message);
    }

    public CachedMessage(@Nullable String line) {
        super(line);
    }

    @Override
    public CachedMessage set(@Nullable List<String> message) {
        this.original = message != null ? message : new ArrayList<>();
        this.message = original;
        return this;
    }

    @Override
    public CachedMessage insert(List<String> toAdd) {
        List<String> msg = new ArrayList<>(toAdd);
        msg.addAll(this.message);
        this.message = msg;

        List<String> org = new ArrayList<>(toAdd);
        org.addAll(this.original);
        this.original = org;
        return this;
    }

    @Override
    public CachedMessage append(List<String> toAdd) {
        this.original.addAll(new ArrayList<>(toAdd));
        this.message.addAll(new ArrayList<>(toAdd));
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
        set(message);
        return this;
    }
}