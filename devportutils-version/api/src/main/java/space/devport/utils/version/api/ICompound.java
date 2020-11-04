package space.devport.utils.version.api;

import org.bukkit.inventory.ItemStack;

public interface ICompound {

    boolean has(String key);

    ICompound withString(String key, String value);

    ICompound withDouble(String key, double value);

    ICompound withInteger(String key, int value);

    String getString(String key);

    double getDouble(String key);

    int getInteger(String key);

    ItemStack finish();
}
