package space.devport.utils.version.api;

import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface ICompound {

    boolean has(String key);

    byte getId(String key);

    ICompound remove(String key);

    Set<String> getKeys();

    ICompound withString(String key, String value);

    ICompound withBoolean(String key, boolean value);

    ICompound withInteger(String key, int value);

    ICompound withDouble(String key, double value);

    ICompound withLong(String key, long value);

    ICompound withFloat(String key, float value);

    ICompound withShort(String key, short value);

    String getString(String key);

    boolean getBoolean(String key);

    int getInteger(String key);

    double getDouble(String key);

    long getLong(String key);

    float getFloat(String key);

    short getShort(String key);

    /**
     * Apply compound changes to ItemStack and return it.
     */
    ItemStack finish();

    ItemStack apply(ItemStack itemStack);
}
