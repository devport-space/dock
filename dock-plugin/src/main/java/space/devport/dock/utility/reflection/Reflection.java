package space.devport.dock.utility.reflection;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class Reflection {

    @Nullable
    public Class<?> getNMSClass(String name) {

        if (Strings.isNullOrEmpty(name))
            return null;

        try {
            return Class.forName("net.minecraft.server." + SpigotHelper.extractNMSVersion() + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public Class<?> getCraftBukkitClass(String name) {

        if (Strings.isNullOrEmpty(name))
            return null;

        try {
            return Class.forName("org.bukkit.craftbukkit." + SpigotHelper.extractNMSVersion() + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Can see only PUBLIC methods of the class, no statics
    @Nullable
    public Method getMethod(Class<?> clazz, String name, Class<?>... args) {

        if (clazz == null || Strings.isNullOrEmpty(name))
            return null;

        try {
            Method method = clazz.getMethod(name, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Can see all the methods of the class
    @Nullable
    public Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... args) {

        if (clazz == null || Strings.isNullOrEmpty(methodName))
            return null;

        try {
            Method method = clazz.getDeclaredMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public <T> T obtainInstance(Class<T> clazz, Class<?>[] args, Object[] params) {
        Constructor<T> constructor = getConstructor(clazz, args);

        if (constructor == null)
            return null;

        try {
            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    @Nullable
    public <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... args) {

        if (clazz == null)
            return null;

        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(args);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
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

    @Nullable
    public <T> T getFieldValue(Object object, Class<T> fieldClazz, String name) {

        if (object == null || Strings.isNullOrEmpty(name) || fieldClazz == null)
            return null;

        try {
            Field field = object.getClass().getDeclaredField(name);
            return getFieldValue(field, field, fieldClazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public <T> T getFieldValue(Object object, Field field, Class<T> fieldClazz) {

        if (object == null || field == null || fieldClazz == null)
            return null;

        try {
            Object obj = field.get(object);

            if (!fieldClazz.isAssignableFrom(obj.getClass()))
                return null;

            field.setAccessible(false);
            return fieldClazz.cast(obj);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public Object getHandle(Object object) {
        try {
            Method method = getMethod(object.getClass(), "getHandle");

            if (method == null)
                return null;

            return method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<?> entityPlayerClazz, playerConnectionClazz;
    private Method sendPacketMethod;

    public boolean sendPacket(Player player, Object packet) {

        if (player == null || packet == null)
            return false;

        try {
            if (entityPlayerClazz == null) {
                entityPlayerClazz = getNMSClass("EntityPlayer");
                playerConnectionClazz = getNMSClass("PlayerConnection");

                if (playerConnectionClazz == null || entityPlayerClazz == null)
                    return false;

                sendPacketMethod = playerConnectionClazz.getMethod("sendPacket", Reflection.getNMSClass("Packet"));
            }

            Object entityPlayer = entityPlayerClazz.cast(Reflection.getHandle(player));
            Object playerConnection = playerConnectionClazz.cast(entityPlayerClazz.getDeclaredField("playerConnection").get(entityPlayer));

            sendPacketMethod.invoke(playerConnection, packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}