package space.devport.utils.text.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.struct.Context;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.StringUtil;
import space.devport.utils.utility.reflection.ServerVersion;
import space.devport.utils.version.VersionManager;

import java.util.LinkedList;

@Log
public class JsonMessage extends Message {

    //TODO 1.16+ uses "contents" instead of values
    //TODO Add proper color tag parsing

    private static final Gson gson = new GsonBuilder().create();

    private boolean newLine = true;

    private final LinkedList<JsonArray> lines = new LinkedList<>();

    private final DevportPlugin plugin;

    public JsonMessage(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    public void clear() {
        lines.clear();
    }

    /**
     * Serialize ItemStack to SNBT.
     */
    // TODO Serialize rest of the ItemStack
    public static String serializeItem(ItemStack item) {
        String str = "{%s}";
        StringBuilder content = new StringBuilder();
        content.append("id:'").append(item.getType().getKey().toString()).append("'")
                .append(",Count:").append(item.getAmount());
        return String.format(str, content);
    }

    public JsonMessage newLine() {
        this.newLine = true;
        return this;
    }

    public enum JsonHoverAction {

        SHOW_TEXT((json, placeholders, extra) -> {
            if (extra.length < 1)
                return json;

            json.addProperty("value", StringUtil.color(placeholders.parse(extra[0].toString())));
            return json;
        }),

        SHOW_ITEM((json, placeholders, extra) -> {
            if (extra.length < 1)
                return json;

            if (!ItemStack.class.isAssignableFrom(extra[0].getClass()))
                return json;

            ItemStack item = (ItemStack) extra[0];

            json.addProperty("value", serializeItem(item));
            return json;
        }),

        SHOW_ENTITY((json, placeholders, extra) -> {
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
        JsonObject parse(JsonObject json, Placeholders placeholders, Object... extra);
    }

    private JsonArray createLine() {
        JsonArray jsonArray = new JsonArray();
        this.lines.add(jsonArray);
        newLine = false;
        return jsonArray;
    }

    private JsonObject createText(String text) {
        JsonObject component = new JsonObject();
        String content = StringUtil.color(this.placeholders.parse(text));
        component.addProperty("text", content);
        return component;
    }

    private JsonObject attachHover(JsonObject json, JsonHoverAction action, Object... extra) {
        JsonObject hover = new JsonObject();
        hover.addProperty("action", action.toString());
        hover = action.getParser().parse(hover, this.placeholders, extra);
        json.add("hoverEvent", hover);
        return json;
    }

    private JsonObject attachClick(JsonObject json, JsonClickAction action, String value) {
        JsonObject click = new JsonObject();
        click.addProperty("action", action.toString());
        click.addProperty("value", StringUtil.color(this.placeholders.parse(value)));
        json.add("clickEvent", click);
        return json;
    }

    /**
     * Add hover event to previously attached component.
     */
    public JsonMessage addHover(JsonHoverAction action, Object... extra) {
        JsonObject component = getLastComponent();
        attachHover(component, action, extra);
        return this;
    }

    /**
     * Add click event to previously attached component.
     */
    public JsonMessage addClick(JsonClickAction action, String value) {
        JsonObject component = getLastComponent();
        attachClick(component, action, value);
        return this;
    }

    private JsonObject getLastComponent() {
        return getCurrentLine().get(getCurrentLine().size() - 1).getAsJsonObject();
    }

    /**
     * Append a text component to this message.
     *
     * @param text Text to append
     */
    public JsonMessage append(String text) {
        getCurrentLine().add(createText(text));
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
    public JsonMessage append(String text, @NotNull JsonHoverAction action, Object... extra) {
        JsonObject component = createText(text);

        attachHover(component, action, extra);
        getCurrentLine().add(component);
        return this;
    }

    private JsonArray getCurrentLine() {
        return lines.isEmpty() || newLine ? createLine() : lines.peekLast();
    }

    /**
     * Append a text component with a clickEvent to this message.
     *
     * @param text   Text to append
     * @param action Action to assign the clickEvent
     * @param value  String value to use for the clickEvent - url, clipboard content to copy,
     *               command or file path
     */
    public JsonMessage append(String text, @NotNull JsonClickAction action, String value) {
        JsonObject component = createText(text);

        attachClick(component, action, value);

        getCurrentLine().add(component);
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
        JsonObject component = createText(text);

        attachHover(component, hoverAction, hoverExtra);
        attachClick(component, clickAction, clickValue);

        getCurrentLine().add(component);
        return this;
    }

    @Override
    public String toString() {
        return gson.toJson(lines);
    }

    @Override
    public void send(@NotNull Player player) {
        sendJson(player, toString());
    }

    private void sendJson(@NotNull Player player, @NotNull String content) {

        if (ServerVersion.isCurrentBelow(ServerVersion.v1_8)) {
            log.warning("Json messages are not supported on versions below 1.8");
            return;
        }

        plugin.getManager(VersionManager.class).getVersionUtility().sendJsonMessage(player, content);
    }

    @Override
    public JsonMessage parseWith(Placeholders placeholders) {
        super.parseWith(placeholders);
        return this;
    }

    @Override
    public JsonMessage context(Object... objects) {
        super.context(objects);
        return this;
    }

    @Override
    public JsonMessage context(Context context) {
        super.context(context);
        return this;
    }

    @Override
    public JsonMessage replace(@Nullable String placeholder, @Nullable Object value) {
        super.replace(placeholder, value);
        return this;
    }
}