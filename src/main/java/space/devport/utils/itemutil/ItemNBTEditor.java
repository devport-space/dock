package space.devport.utils.itemutil;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.SpigotHelper;
import space.devport.utils.utilities.Reflection;

import java.util.HashMap;
import java.util.Map;

/**
 * Item NBT Editor
 */
public class ItemNBTEditor {

    // TODO Add method to get all NBT keys on an item
    // TODO Hook to ConsoleOutput


    public static Map<String, String> getNBTTagMap(@NotNull ItemStack item) {
        Map<String, String> meta = new HashMap<>();
        try {
            Object nmsItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
            boolean hasTag = (boolean)nmsItemStack.getClass().getMethod("hasTag").invoke(nmsItemStack);
            if(hasTag) {
                NBTTagCompound tags = (NBTTagCompound)nmsItemStack.getClass().getMethod("getTag").invoke(nmsItemStack);
                for (String fieldName : tags.c()) {
                    meta.put(fieldName, tags.get(fieldName).toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return meta;
    }


    /**
     * Writes key and value to the item's NBT.
     *
     * @param item  Item that NBT Data's will be changed
     * @param key   Key of NBT Compound
     * @param value Value of NBT Compound
     * @return Edited item.
     */
    public static ItemStack writeNBT(@NotNull ItemStack item, @NotNull String key, @NotNull String value) {
        try {
            Object nativeItemStack = ReflectionStatics.getAsNMSItemStack(item);
            Object tag;
            if(SpigotHelper.getVersion().contains("v1.7") || SpigotHelper.getVersion().contains("v1.8")) {
                boolean hasTag = (boolean)nativeItemStack.getClass().getMethod("hasTag").invoke(nativeItemStack);
                tag = hasTag ? nativeItemStack.getClass().getMethod("getTag").invoke(nativeItemStack) : nativeItemStack.getClass().getMethod("makeTag").invoke(nativeItemStack);
            } else {
                tag = nativeItemStack.getClass().getDeclaredMethod("getOrCreateTag").invoke(nativeItemStack);
            }

            tag.getClass().getDeclaredMethod("setString", String.class, String.class).invoke(tag, key, value);
            nativeItemStack.getClass().getDeclaredMethod("setTag", tag.getClass()).invoke(nativeItemStack, tag);

            return ReflectionStatics.getAsItemStack(nativeItemStack);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    /**
     * Gets value from NBT compound that matches a key from item's NBT.
     *
     * @param item Item to use
     * @param key  Key to look for
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
     * Checks if item has NBT.
     *
     * @param item Item to check
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
     * Checks if an item has a key in NBT.
     *
     * @param item Item to check
     * @param key  Key to look for
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