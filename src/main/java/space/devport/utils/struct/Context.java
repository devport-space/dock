package space.devport.utils.struct;

import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Context {

    private final Set<Object> values = new HashSet<>();

    public Context() {
    }

    public Context(Object... objects) {
        add(objects);
    }

    public Context(Object object) {
        add(object);
    }

    public Context(Context context) {
        add(context);
    }

    public Context add(Context context) {
        this.values.addAll(context.getValues());
        return this;
    }

    public Context set(Context context) {
        this.values.clear();
        add(context);
        return this;
    }

    public Context add(Object object) {
        this.values.add(object);
        return this;
    }

    public Context add(Object... objects) {
        this.values.addAll(Arrays.asList(objects));
        return this;
    }

    public void clear() {
        this.values.clear();
    }

    public Set<Object> getValues() {
        return Collections.unmodifiableSet(values);
    }

    public Context fromPlayer(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline())
            add(offlinePlayer.getPlayer());
        return add(offlinePlayer);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName() + "[");
        for (Object o : getValues())
            builder.append(", ").append(o.toString());
        return builder.append("]").toString();
    }
}