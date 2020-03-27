package space.devport.utils.itemutil;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.SpigotHelper;
import space.devport.utils.utilities.Reflection;

import java.util.*;

/**
 * Item NBT Editor
 */
public class ItemNBTEditor {

    public static Map<String, String> getNBTTagMap(@NotNull ItemStack item) {
        Map<String, String> meta = new HashMap<>();
        try {
            Object nmsItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
            boolean hasTag = (boolean) nmsItemStack.getClass().getMethod("hasTag").invoke(nmsItemStack);

            if (hasTag) {
                Object tag = getTag(nmsItemStack);

                List<String> keys;

                if (SpigotHelper.getVersion().contains("1.15"))
                    keys = new ArrayList<>((Set<String>) tag.getClass().getDeclaredMethod("getKeys").invoke(tag));
                else
                    keys = new ArrayList<>((Set<String>) tag.getClass().getDeclaredMethod("c").invoke(tag));

                for (String fieldName : keys) {
                    meta.put(fieldName, (String) tag.getClass().getDeclaredMethod("getString", String.class).invoke(tag, fieldName));
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
            Object nativeItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
            Object tag = getTag(nativeItemStack);

            tag.getClass().getDeclaredMethod("setString", String.class, String.class).invoke(tag, key, value);
            nativeItemStack.getClass().getDeclaredMethod("setTag", tag.getClass()).invoke(nativeItemStack, tag);

            return (ItemStack) Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asBukkitCopy", Reflection.getNMSClass(".ItemStack")).invoke(null, nativeItemStack);
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
            Object nativeItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
            Object tag = getTag(nativeItemStack);
            return (String) tag.getClass().getDeclaredMethod("getString", String.class).invoke(tag, key);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    /**
     * Removes key from item NBT.
     *
     * @param item Item that NBT Data's will be changed.
     * @param key  Key of NBT Compound.
     * @return Edited item. As new.
     */
    public static ItemStack removeNBT(@NotNull ItemStack item, @NotNull String key) {
        try {
            Object nativeItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
            Object tag = getTag(nativeItemStack);
            // remove tag
            tag.getClass().getDeclaredMethod("remove", String.class).invoke(tag, key);
            nativeItemStack.getClass().getDeclaredMethod("setTag", tag.getClass()).invoke(nativeItemStack, tag);
            return (ItemStack) Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asBukkitCopy", Reflection.getNMSClass(".ItemStack")).invoke(null, nativeItemStack);
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
            Object nativeItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
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
            Object nativeItemStack = Reflection.getDeclaredMethod(Reflection.getCBClass(".inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, item);
            Object tag = getTag(nativeItemStack);
            return (boolean) tag.getClass().getDeclaredMethod("hasKey", String.class).invoke(tag, key);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a NBTTag from an item, if there's none, creates a new one.
     *
     * @param nativeItemStack ItemStack to retrieve tag from.
     * @return NBTTag
     */
    private static Object getTag(Object nativeItemStack) {
        try {
            if (SpigotHelper.getVersion().contains("1.8") || SpigotHelper.getVersion().contains("1.7")) {
                if (!(boolean) nativeItemStack.getClass().getMethod("hasTag").invoke(nativeItemStack)) {
                    Object compound = Reflection.getNMSClass(".NBTTagCompound").getConstructor().newInstance();
                    nativeItemStack.getClass().getMethod("setTag", compound.getClass()).invoke(nativeItemStack, compound);
                }

                return nativeItemStack.getClass().getMethod("getTag").invoke(nativeItemStack);
            } else {
                return nativeItemStack.getClass().getDeclaredMethod("getOrCreateTag").invoke(nativeItemStack);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}