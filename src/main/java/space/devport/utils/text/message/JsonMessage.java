package space.devport.utils.text.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import space.devport.utils.utility.reflection.Reflection;
import space.devport.utils.utility.reflection.ServerVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class JsonMessage extends Message {

    private static final Gson gson = new GsonBuilder().create();

    private static boolean enabled = ServerVersion.isAboveCurrent(ServerVersion.v1_8);

    private static Method mc_IChatBaseComponent_ChatSerializer_a;
    private static Constructor mc_PacketPlayOutChat_new;
    private static Method cb_craftPlayer_getHandle;
    private static Field mc_entityPlayer_playerConnection;
    private static Method mc_playerConnection_sendPacket;

    static {
        init();
    }

    static void init() {
        if (!enabled) return;

        try {
            Class cb_craftPlayerClazz;
            Class mc_entityPlayerClazz;
            Class mc_playerConnectionClazz;
            Class mc_PacketInterface;
            Class mc_IChatBaseComponent;
            Class mc_IChatBaseComponent_ChatSerializer;
            Class mc_PacketPlayOutChat;

            cb_craftPlayerClazz = Reflection.getCBClass("entity.CraftPlayer");
            cb_craftPlayer_getHandle = cb_craftPlayerClazz.getDeclaredMethod("getHandle");

            mc_entityPlayerClazz = Reflection.getNMSClass("EntityPlayer");
            mc_entityPlayer_playerConnection = mc_entityPlayerClazz.getDeclaredField("playerConnection");

            mc_playerConnectionClazz = Reflection.getNMSClass("PlayerConnection");
            mc_PacketInterface = Reflection.getNMSClass("Packet");
            mc_playerConnection_sendPacket = mc_playerConnectionClazz.getDeclaredMethod("sendPacket", mc_PacketInterface);

            mc_IChatBaseComponent = Reflection.getNMSClass("IChatBaseComponent");
            mc_IChatBaseComponent_ChatSerializer = Reflection.getNMSClass("IChatBaseComponent$ChatSerializer");
            mc_IChatBaseComponent_ChatSerializer_a = mc_IChatBaseComponent_ChatSerializer.getMethod("a", String.class);

            mc_PacketPlayOutChat = Reflection.getNMSClass("PacketPlayerOutChat");
            mc_PacketPlayOutChat_new = mc_PacketPlayOutChat.getConstructor(mc_IChatBaseComponent);
        } catch (Throwable ex) {
            Bukkit.getLogger().log(Level.WARNING, "Problem preparing raw chat packets (disabling further packets)", ex);
            enabled = false;
        }
    }

    private final List<JsonObject> textList = new ArrayList<>();

    public void clear() {
        textList.clear();
    }

    public JsonMessage addMessage(String s) {
        JsonObject txt = new JsonObject();
        txt.addProperty("text", s);
        textList.add(txt);
        return this;
    }

    public JsonMessage addRunCommand(String text, String hoverText, String cmd) {
        JsonObject txt = new JsonObject();
        txt.addProperty("text", text);

        JsonObject hover = new JsonObject();
        hover.addProperty("action", "show_text");
        hover.addProperty("value", hoverText);
        txt.add("hoverEvent", hover);

        JsonObject click = new JsonObject();
        click.addProperty("action", "run_command");
        click.addProperty("value", cmd);
        txt.add("clickEvent", click);

        textList.add(txt);
        return this;
    }

    public JsonMessage addPromptCommand(String text, String hoverText, String cmd) {
        JsonObject txt = new JsonObject();
        txt.addProperty("text", text);

        JsonObject hover = new JsonObject();
        hover.addProperty("action", "show_text");
        hover.addProperty("value", hoverText);
        txt.add("hoverEvent", hover);

        JsonObject click = new JsonObject();
        click.addProperty("action", "suggest_command");
        click.addProperty("value", cmd);
        txt.add("clickEvent", click);

        textList.add(txt);
        return this;
    }

    public JsonMessage addURL(String text, String hoverText, String url) {
        JsonObject txt = new JsonObject();
        txt.addProperty("text", text);

        JsonObject hover = new JsonObject();
        hover.addProperty("action", "show_text");
        hover.addProperty("value", hoverText);
        txt.add("hoverEvent", hover);

        JsonObject click = new JsonObject();
        click.addProperty("action", "open_url");
        click.addProperty("value", url);
        txt.add("clickEvent", hover);

        textList.add(txt);
        return this;
    }

    @Override
    public String toString() {
        return gson.toJson(textList);
    }

    public void sendTo(Player player) {
        if (!enabled) return;

        try {
            Object packet = mc_PacketPlayOutChat_new.newInstance(mc_IChatBaseComponent_ChatSerializer_a.invoke(null, this.toString()));
            Object cbPlayer = cb_craftPlayer_getHandle.invoke(player);
            Object mcConnection = mc_entityPlayer_playerConnection.get(cbPlayer);
            mc_playerConnection_sendPacket.invoke(mcConnection, packet);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Problem preparing raw chat packets (disabling further packets)", ex);
            enabled = false;
        }
    }
}