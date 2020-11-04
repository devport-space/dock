package space.devport.utils.text.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.struct.Context;
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

    private static boolean enabled = ServerVersion.isCurrentAbove(ServerVersion.v1_8);

    private static Method mc_IChatBaseComponent_ChatSerializer_a;
    private static Constructor<?> mc_PacketPlayOutChat_new;
    private static Method cb_craftPlayer_getHandle;
    private static Field mc_entityPlayer_playerConnection;
    private static Method mc_playerConnection_sendPacket;

    static {
        init();
    }

    static void init() {
        if (!enabled) return;

        try {
            Class<?> cb_craftPlayerClazz;
            Class<?> mc_entityPlayerClazz;
            Class<?> mc_playerConnectionClazz;
            Class<?> mc_PacketInterface;
            Class<?> mc_IChatBaseComponent;
            Class<?> mc_IChatBaseComponent_ChatSerializer;
            Class<?> mc_PacketPlayOutChat;

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

            mc_PacketPlayOutChat = Reflection.getNMSClass("PacketPlayOutChat");
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

    /**
     * Serialize ItemStack to SNBT.
     */
    // TODO Serialize rest of the ItemStack
    public static JsonObject serializeItem(ItemStack item) {
        JsonObject json = new JsonObject();
        json.addProperty("count", item.getAmount());
        json.addProperty("id", item.getType().toString());
        return json;
    }

    public enum JsonHoverAction {

        SHOW_TEXT((json, extra) -> {
            if (extra.length < 1)
                return json;

            json.addProperty("value", extra[0].toString());
            return json;
        }),

        SHOW_ITEM((json, extra) -> {
            if (extra.length < 1)
                return json;

            if (!ItemStack.class.isAssignableFrom(extra[0].getClass()))
                return json;

            ItemStack item = (ItemStack) extra[0];

            json.addProperty("value", serializeItem(item).getAsString());
            return json;
        }),

        SHOW_ENTITY((json, extra) -> {
            if (extra.length < 1)
                return json;

            JsonObject entity = new JsonObject();

            // All of them are optional
            if (extra[0] != null) entity.addProperty("name", extra[0].toString());
            if (extra[1] != null) entity.addProperty("type", extra[1].toString());
            if (extra[2] != null) entity.addProperty("id", extra[2].toString());

            json.addProperty("value", entity.getAsString());
            return json;
        });

        @Getter
        private final ExtraParser parser;

        JsonHoverAction(ExtraParser parser) {
            this.parser = parser;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum JsonClickAction {
        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_URL,
        COPY_TO_CLIPBOARD,
        OPEN_FILE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public interface ExtraParser {
        JsonObject parse(JsonObject json, Object... extra);
    }

    private JsonObject addHover(JsonObject json, JsonHoverAction action, Object... extra) {
        JsonObject hover = new JsonObject();
        hover.addProperty("action", "show_text");
        hover = action.parser.parse(hover, extra);
        json.add("hoverEvent", hover);
        return json;
    }

    private JsonObject addClick(JsonObject json, JsonClickAction action, String value) {
        JsonObject click = new JsonObject();
        click.addProperty("action", action.toString());
        click.addProperty("value", value);
        json.add("clickEvent", click);
        return json;
    }

    /**
     * Append a text component to this message.
     *
     * @param text Text to append
     */
    public JsonMessage appendText(String text) {
        JsonObject component = new JsonObject();
        component.addProperty("text", text);
        textList.add(component);
        return this;
    }

    /**
     * Append a text component with a hoverEvent to this message.
     *
     * @param text   Text to append
     * @param action Action to assign the hoverEvent
     * @param extra  Objects to parse for hoverEvent value,
     *               provide an ItemStack for SHOW_ITEM, Entity for SHOW_ENTITY and String for SHOW_TEXT
     */
    public JsonMessage appendHover(String text, @NotNull JsonHoverAction action, Object... extra) {
        JsonObject component = new JsonObject();
        component.addProperty("text", text);

        addHover(component, action, extra);

        textList.add(component);
        return this;
    }

    /**
     * Append a text component with a clickEvent to this message.
     *
     * @param text   Text to append
     * @param action Action to assign the clickEvent
     * @param value  String value to use for the clickEvent - url, clipboard content to copy,
     *               command or file path
     */
    public JsonMessage appendClick(String text, @NotNull JsonClickAction action, String value) {
        JsonObject component = new JsonObject();
        component.addProperty("text", text);

        addClick(component, action, value);

        textList.add(component);
        return this;
    }

    /**
     * Append a text component with both a hover and click events.
     *
     * @param text        Text to append
     * @param hoverAction Action to assign the hoverEvent
     * @param clickAction Action to assign the clickEvent
     * @param clickValue  String value to use for the clickEvent - url, clipboard content to copy,
     *                    command or file path
     * @param hoverExtra  Objects to parse for hoverEvent value,
     *                    provide an ItemStack for SHOW_ITEM, Entity for SHOW_ENTITY and String for SHOW_TEXT
     */
    public JsonMessage append(String text, @NotNull JsonHoverAction hoverAction, @NotNull JsonClickAction clickAction, String clickValue, Object... hoverExtra) {
        JsonObject component = new JsonObject();
        component.addProperty("text", text);

        addHover(component, hoverAction, hoverExtra);
        addClick(component, clickAction, clickValue);

        textList.add(component);
        return this;
    }

    @Override
    public String toString() {
        return gson.toJson(textList);
    }

    @Override
    public void send(Player player) {
        if (!enabled) return;

        // Reset context after we add the player.
        Context oldContext = new Context(this.placeholders.getContext());
        super.context(player);

        String content = this.placeholders.parse(this.toString());

        this.placeholders.setContext(oldContext);

        sendTo(player, content);
    }

    public void sendTo(Player player) {
        sendTo(player, toString());
    }

    private void sendTo(Player player, String content) {
        try {
            Object packet = mc_PacketPlayOutChat_new.newInstance(mc_IChatBaseComponent_ChatSerializer_a.invoke(null, content));
            Object cbPlayer = cb_craftPlayer_getHandle.invoke(player);
            Object mcConnection = mc_entityPlayer_playerConnection.get(cbPlayer);
            mc_playerConnection_sendPacket.invoke(mcConnection, packet);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Bukkit.getLogger().log(Level.WARNING, "Problem preparing raw chat packets (disabling further packets)", ex);
            enabled = false;
        }
    }
}