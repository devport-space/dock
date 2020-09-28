package space.devport.utils.struct;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Context {

    private final Set<Object> values = new HashSet<>();

    public Context() {
    }

    public Context(Object object) {
        this.values.add(object);
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

    public void clear() {
        this.values.clear();
    }

    public Set<Object> getValues() {
        return Collections.unmodifiableSet(values);
    }
}