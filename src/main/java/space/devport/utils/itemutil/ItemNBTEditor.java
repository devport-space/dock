package space.devport.utils.itemutil;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Item NBT Editor
 */
public class ItemNBTEditor {

    // TODO Add method to get all NBT keys on an item
    // TODO Hook to ConsoleOutput

    /**
     * Writes {@link @key} (key) and {@link @value} (value) to the {@link @item} (item's) NBT.
     *
     * @param item  Item that NBT Data's will be changed.
     * @param key   Key of NBT Compound.
     * @param value Value of NBT Compound.
     * @return Edited item. As new.
     */
    public static ItemStack writeNBT(@NotNull ItemStack item, @NotNull String key, @NotNull String value) {
        try {
            Object nativeItemStack = ReflectionStatics.getAsNMSItemStack(item);
            Object tag = nativeItemStack.getClass().getDeclaredMethod("getOrCreateTag").invoke(nativeItemStack);

            tag.getClass().getDeclaredMethod("setString", String.class, String.class).invoke(tag, key, value);
            nativeItemStack.getClass().getDeclaredMethod("setTag", tag.getClass()).invoke(nativeItemStack, tag);

            return ReflectionStatics.getAsItemStack(nativeItemStack);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    /**
     * Gets value from NBT compound that matches {@link @key} from {@link @item} NBT.
     *
     * @param item Item
     * @param key  Key
     * @return String value
     * @throws NullPointerException when did found anything in NBTCompound of item.
     */

    public static String getNBT(@NotNull ItemStack item, @NotNull String key) {
        try {
            Object nativeItemStack = ReflectionStatics.getAsNMSItemStack(item);
            Object tag = nativeItemStack.getClass().getDeclaredMethod("getOrCreateTag").invoke(nativeItemStack);
            return (String) tag.getClass().getDeclaredMethod("getString", String.class).invoke(tag, key);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    /**
     * Removes {@link @key} from {@link @item} NBT.
     *
     * @param item Item that NBT Data's will be changed.
     * @param key  Key of NBT Compound.
     * @return Edited item. As new.
     */
    public static ItemStack removeNBT(@NotNull ItemStack item, @NotNull String key) {
        try {
            Object nativeItemStack = ReflectionStatics.getAsNMSItemStack(item);
            Object tag = nativeItemStack.getClass().getDeclaredMethod("getOrCreateTag").invoke(nativeItemStack);
            // remove tag
            tag.getClass().getDeclaredMethod("remove", String.class).invoke(tag, key);

            nativeItemStack.getClass().getDeclaredMethod("setTag", tag.getClass()).invoke(nativeItemStack, tag);

            return ReflectionStatics.getAsItemStack(nativeItemStack);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if {@param item} has NBTCompound.
     *
     * @param item Item to check.
     * @return boolean
     */

    public static boolean hasNBT(@NotNull ItemStack item) {
        try {
            Object nativeItemStack = ReflectionStatics.getAsNMSItemStack(item);
            return (boolean) nativeItemStack.getClass().getDeclaredMethod("hasTag").invoke(nativeItemStack);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if {@param item} has NBTTag in NBTCompound
     *
     * @param item Item to check.
     * @param key  Tag to be found.
     * @return boolean
     */
    public static boolean hasNBTKey(@NotNull ItemStack item, String key) {
        try {
            Object nativeItemStack = ReflectionStatics.getAsNMSItemStack(item);
            Object tag = nativeItemStack.getClass().getDeclaredMethod("getOrCreateTag").invoke(nativeItemStack);
            return (Boolean) tag.getClass().getDeclaredMethod("hasKey", String.class).invoke(tag, key);

        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }
}