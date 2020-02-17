package space.devport.utils.itemutil;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionStatics {

    /**
     * Finds NMS Version of the Vanilla Jar file
     *
     * @return NMS Version
     * @throws URISyntaxException Thrown when the URI of Vanilla Jar File is invalid
     * @throws IOException        Thrown when the Vanilla Jar File is not accessible
     */
    public static String getNMSVersion() throws URISyntaxException, IOException {
        JarFile file = new JarFile(new File(((CraftServer) Bukkit.getServer()).getHandle().getServer().getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        String versionRaw = file.stream()
                .filter(JarEntry::isDirectory)
                .map(JarEntry::getName)
                .filter(entry -> entry.startsWith("net/minecraft/server/v"))
                .findFirst()
                .get();

        String[] urlContent = versionRaw.split("/");
        return urlContent[urlContent.length - 1];
    }

    public static Object getAsNMSItemStack(ItemStack itemStack) throws IOException, URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + ".inventory.CraftItemStack");
        return craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
    }

    public static ItemStack getAsItemStack(Object object) throws IOException, URISyntaxException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + ".inventory.CraftItemStack");
        return (ItemStack) craftItemStack.getMethod("asBukkitCopy", object.getClass()).invoke(null, object);
    }
}