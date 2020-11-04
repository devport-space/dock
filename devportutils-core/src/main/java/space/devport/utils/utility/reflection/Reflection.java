package space.devport.utils.utility.reflection;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    public Method getMethod(Class<?> className, String methodName, Class<?>... args) {
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
    public Method getDeclaredMethod(Class<?> className, String methodName, Class<?>... args) {
        try {
            Method m = className.getDeclaredMethod(methodName, args);
            m.setAccessible(true);
            return m;
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
            Constructor<T> constructor = clazz.getConstructor(args);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
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

    private Class<?> entityPlayerClazz, playerConnectionClazz;

    public void sendPacket(Player p, Object packet) {
        try {
            if (entityPlayerClazz == null) {
                entityPlayerClazz = getNMSClass("EntityPlayer");
                playerConnectionClazz = getNMSClass("PlayerConnection");
            }

            Object entityPlayer = entityPlayerClazz.cast(Reflection.getHandle(p));
            Object playerConnection = playerConnectionClazz.cast(entityPlayerClazz.getDeclaredField("playerConnection").get(entityPlayer));

            playerConnectionClazz.getMethod("sendPacket", Reflection.getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}