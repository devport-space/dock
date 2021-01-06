package space.devport.utils.struct;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Context {

    private final Map<Class<?>, Object> values = new HashMap<>();

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

    /**
     * Get a context object stored here by class.
     *
     * @param clazz Object class
     * @param <T>   ype signature
     * @return Context object or null if not present
     */
    @Nullable
    public <T> T get(@NotNull Class<T> clazz) {
        for (Object o : this.getValues()) {
            if (clazz.isAssignableFrom(o.getClass()))
                return clazz.cast(o);
        }
        return null;
    }

    public Context add(Context context) {
        return add(context.getValues());
    }

    public Context set(Context context) {
        this.values.clear();
        add(context);
        return this;
    }

    public Context add(Collection<Object> objects) {
        objects.forEach(o -> values.put(o.getClass(), o));
        return this;
    }

    public Context add(Object object) {
        this.values.put(object.getClass(), object);
        return this;
    }

    public Context add(Object... objects) {
        for (Object o : objects)
            this.values.put(o.getClass(), o);
        return this;
    }

    public void clear() {
        this.values.clear();
    }

    public Set<Object> getValues() {
        return new HashSet<>(values.values());
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
            builder.append(", ").append(o.getClass().getSimpleName()).append(":").append(o.toString());
        return builder.append("]").toString();
    }
}