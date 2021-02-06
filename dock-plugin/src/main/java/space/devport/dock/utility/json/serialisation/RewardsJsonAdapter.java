package space.devport.dock.utility.json.serialisation;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import space.devport.dock.DockedPlugin;
import space.devport.dock.item.ItemPrefab;
import space.devport.dock.item.data.Amount;
import space.devport.dock.struct.Rewards;
import space.devport.dock.text.message.CachedMessage;

import java.lang.reflect.Type;
import java.util.List;

public class RewardsJsonAdapter implements JsonSerializer<Rewards>, JsonDeserializer<Rewards> {

    private final DockedPlugin plugin;

    public RewardsJsonAdapter(DockedPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Rewards deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject())
            return null;

        JsonObject object = json.getAsJsonObject();

        Amount tokens = context.deserialize(object.get("tokens"), new TypeToken<Amount>() {
        }.getType());

        Amount money = context.deserialize(object.get("money"), new TypeToken<Amount>() {
        }.getType());

        List<ItemPrefab> items = context.deserialize(object.get("items"), new TypeToken<List<ItemPrefab>>() {
        }.getType());

        CachedMessage inform = context.deserialize(object.get("inform"), new TypeToken<CachedMessage>() {
        }.getType());

        CachedMessage broadcast = context.deserialize(object.get("broadcast"), new TypeToken<CachedMessage>() {
        }.getType());

        List<String> commands = context.deserialize(object.get("commands"), new TypeToken<List<String>>() {
        }.getType());

        return new Rewards(plugin, tokens, money, items, inform, broadcast, commands);
    }

    @Override
    public JsonElement serialize(Rewards rewards, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("tokens", context.serialize(rewards.getTokens()));
        object.add("money", context.serialize(rewards.getMoney()));
        object.add("items", context.serialize(rewards.getItems()));
        object.add("inform", context.serialize(rewards.getInform()));
        object.add("broadcast", context.serialize(rewards.getBroadcast()));
        object.add("commands", context.serialize(rewards.getCommands()));
        return object;
    }
}
