package space.devport.utils.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@UtilityClass
public class Reflection {

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + SpigotHelper.extractNMSVersion() + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getCBClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + SpigotHelper.extractNMSVersion() + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Can see only PUBLIC methods of the class, no statics
    public static Method getMethod(Class<?> className, String methodName, Class<?>... args) {
        try {
            Method m = className.getMethod(methodName, args);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Can see all the methods of the class
    public static Method getDeclaredMethod(Class<?> className, String methodName, Class<?>... args) {
        try {
            Method m = className.getDeclaredMethod(methodName, args);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getFieldValue(Object o, String name) {
        try {
            Field field = o.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T getFieldValue(Field field, Object o) {
        try {
            return (T) field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendPacket(Player p, Object packet) {
        try {
            Object player = getHandle(p);
            Object connection = player.getClass().getField("playerConnection").get(p);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}