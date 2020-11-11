package space.devport.utils.item.nbt;

import lombok.experimental.UtilityClass;
import space.devport.utils.ConsoleOutput;
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
        put(Byte.class, new TypeApplier<Byte>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                //TODO missing ICompound#withByte
            }

            @Override
            public Byte query(ICompound compound, String key) {
                //TODO
                return 1;
            }
        });

        put(Short.class, new TypeApplier<Short>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                compound.withShort(key, (short) value);
            }

            @Override
            public Short query(ICompound compound, String key) {
                return compound.getShort(key);
            }
        });

        put(Integer.class, new TypeApplier<Integer>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                compound.withInteger(key, (int) value);
            }

            @Override
            public Integer query(ICompound compound, String key) {
                return compound.getInteger(key);
            }
        });

        put(Long.class, new TypeApplier<Long>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                compound.withLong(key, (long) value);
            }

            @Override
            public Long query(ICompound compound, String key) {
                return compound.getLong(key);
            }
        });

        put(Float.class, new TypeApplier<Float>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                compound.withFloat(key, (float) value);
            }

            @Override
            public Float query(ICompound compound, String key) {
                return compound.getFloat(key);
            }
        });

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

        put(byte[].class, new TypeApplier<Byte[]>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                //TODO missing ICompound#withByteArray
            }

            @Override
            public Byte[] query(ICompound compound, String key) {
                //TODO
                return new Byte[0];
            }
        });

        put(String.class, new TypeApplier<String>() {
            @Override
            public void apply(ICompound compound, String key, Object value) {
                compound.withString(key, (String) value);
            }

            @Override
            public String query(ICompound compound, String key) {
                return compound.getString(key);
            }
        });

        //TODO
        //TODO missing ICompound#withList
        //TODO missing ICompound#withIntArray
        //TODO missing ICompound#withLongArray
    }};

    public void setValue(ICompound compound, String key, Object value) {
        TypeApplier<?> applier = APPLIERS.get(value.getClass());
        if (applier != null)
            applier.apply(compound, key, value);
        else
            ConsoleOutput.getInstance().err("Plugin attempted to save unsupported type " + value.getClass().getSimpleName() + " to NBT.");
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
