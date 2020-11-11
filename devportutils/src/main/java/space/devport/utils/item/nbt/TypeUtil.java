package space.devport.utils.item.nbt;

import lombok.experimental.UtilityClass;
import space.devport.utils.version.api.ICompound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to help solve NBT tag storage.
 */
@UtilityClass
public class TypeUtil {

    public final Map<Byte, Class<?>> BASE_CLASS_MAP = new HashMap<Byte, Class<?>>() {{
        put((byte) 1, Byte.class);
        put((byte) 2, Short.class);
        put((byte) 3, Integer.class);
        put((byte) 4, Long.class);
        put((byte) 5, Float.class);
        put((byte) 6, Double.class);
        put((byte) 7, byte[].class);
        put((byte) 8, String.class);
        put((byte) 9, List.class);
        put((byte) 11, int[].class);
        put((byte) 12, long[].class);
    }};

    public interface TypeApplier<T> {
        void apply(ICompound compound, String key, Object value);

        T query(ICompound compound, String key);
    }

    public final Map<Class<?>, TypeApplier<?>> APPLIERS = new HashMap<Class<?>, TypeApplier<?>>() {{
        put(Double.class, new TypeApplier<Double>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                compound.withDouble(key, (Double) value);
            }

            @Override
            public Double query(ICompound compound, String key) {
                return compound.getDouble(key);
            }
        });
    }};

    public void setValue(ICompound compound, String key, Object value) {
        APPLIERS.get(value.getClass()).apply(compound, key, value);
    }

    public <T> T getValue(ICompound compound, String key, Class<T> clazz) {
        return clazz.cast(APPLIERS.get(clazz).query(compound, key));
    }

    /**
     * Get a value from compound and contain it inside an NBTContainer.
     */
    public NBTContainer containValue(ICompound compound, String key) {
        byte id = compound.getId(key);
        Class<?> clazz = BASE_CLASS_MAP.get(id);

        if (clazz == null)
            return null;

        Object value = getValue(compound, key, clazz);
        return new NBTContainer(value);
    }
}
