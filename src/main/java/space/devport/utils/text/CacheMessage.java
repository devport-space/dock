package space.devport.utils.text;

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
public class CacheMessage extends Message {

    @Getter
    private List<String> original = new ArrayList<>();

    public CacheMessage(@NotNull Message message) {
        super(message);
    }

    public CacheMessage(@Nullable String... message) {
        super(message);
    }

    public CacheMessage(@Nullable List<String> message) {
        super(message);
    }

    public CacheMessage(@Nullable String line) {
        super(line);
    }

    @Override
    public CacheMessage set(@Nullable List<String> message) {
        this.original = message != null ? message : new ArrayList<>();
        this.message = original;
        return this;
    }

    @Override
    public CacheMessage insert(List<String> toAdd) {
        List<String> msg = new ArrayList<>(toAdd);
        msg.addAll(this.message);
        this.message = msg;

        List<String> org = new ArrayList<>(toAdd);
        org.addAll(this.original);
        this.original = org;
        return this;
    }

    @Override
    public CacheMessage append(List<String> toAdd) {
        this.original.addAll(toAdd);
        this.message.addAll(toAdd);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    // Pull the original in to working message
    public CacheMessage pull() {
        set(original);
        return this;
    }

    // Push the working message into original
    public CacheMessage push() {
        set(message);
        return this;
    }
}