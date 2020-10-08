package space.devport.utils.item;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.text.Placeholders;
import space.devport.utils.utility.reflection.ServerVersion;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class SkullData {

    @Getter
    @Setter
    private String owningPlayer;

    // base64 value
    @Getter
    @Setter
    private String token;

    // Minecraft textures url - can be shortened
    @Getter
    @Setter
    private String url;

    @Getter
    private final transient Placeholders placeholders = new Placeholders();

    public SkullData(String owningPlayer, String token, String url) {
        this.owningPlayer = owningPlayer;
        this.token = token;
        this.url = checkURL(url);
    }

    private String checkURL(String input) {
        if (Strings.isNullOrEmpty(input)) return input;
        return input.startsWith("https://textures.minecraft.net/texture/") || input.startsWith("http://textures.minecraft.net/texture/") ? input : "https://textures.minecraft.net/texture/" + url;
    }

    public SkullData(SkullData skullData) {
        this.owningPlayer = skullData.getOwningPlayer();
        this.token = skullData.getToken();
        this.url = skullData.getUrl();
    }

    public SkullData parseWith(Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    public ItemStack apply(ItemStack item) {

        ConsoleOutput.getInstance().debug("Applying skull data to item stack");

        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta)) {
            ConsoleOutput.getInstance().debug("Invalid item meta");
            return item;
        }

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        if (owningPlayer != null) {
            String owningPlayer = placeholders.parse(this.owningPlayer);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owningPlayer);

            if (ServerVersion.isBelowCurrent(ServerVersion.v1_13))
                skullMeta.setOwner(owningPlayer);
            else
                skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        } else {

            Property property = null;
            if (token != null)
                property = getProperty(token);
            else if (url != null)
                property = getPropertyURL(url);

            if (property == null) return item;

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", property);
            Field profileField = null;

            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }

            profileField.setAccessible(true);

            try {
                profileField.set(skullMeta, profile);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            item.setItemMeta(skullMeta);
        }

        item.setType(Material.PLAYER_HEAD);
        return item;
    }

    private static Property getProperty(String texture) {
        return new Property("textures", texture);
    }

    private static Property getPropertyURL(String url) {
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        return new Property("textures", new String(encodedData));
    }

    @Nullable
    public static SkullData fromString(@Nullable String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        SkullData skullData;

        if (str.toLowerCase().startsWith("owner:")) {
            str = str.replace("owner:", "");
            skullData = new SkullData(str, null, null);
        } else if (str.toLowerCase().startsWith("token:")) {
            str = str.replace("token:", "");
            skullData = new SkullData(null, str, null);
        } else if (str.toLowerCase().startsWith("url:")) {
            str = str.replace("url:", "");
            skullData = new SkullData(null, null, str);
        } else
            skullData = new SkullData(str, null, null);
        ConsoleOutput.getInstance().debug("Loaded skull data " + skullData.toString());
        return skullData;
    }

    @Override
    public String toString() {
        if (owningPlayer != null)
            return "owner:" + owningPlayer;
        else if (token != null)
            return "token:" + token;
        else if (url != null)
            return "url:" + url;
        else return "";
    }

    public static SkullData readSkullTexture(ItemStack item) {

        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta))
            return new SkullData(null, null, null);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Field profileField;

        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);

            GameProfile profile = (GameProfile) profileField.get(meta);

            if (profile == null || profile.getProperties() == null)
                return new SkullData(null, null, null);

            Collection<Property> properties = profile.getProperties().get("textures");
            if (properties != null) {

                Iterator<Property> iterator = properties.iterator();
                if (iterator.hasNext()) {
                    Property property = iterator.next();
                    return new SkullData(null, property.getValue(), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SkullData(null, null, null);
    }
}