package space.devport.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
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
    @Getter
    @Setter
    private String token;

    @Getter
    private final transient Placeholders placeholders = new Placeholders();

    public SkullData(String owningPlayer, String token) {
        this.owningPlayer = owningPlayer;
        this.token = token;
    }

    public SkullData(SkullData skullData) {
        this.owningPlayer = skullData.getOwningPlayer();
        this.token = skullData.getToken();
    }

    public SkullData parseWith(Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    public ItemStack apply(ItemStack item) {

        if (item == null || item.getItemMeta() == null)
            return item;

        if (!(item.getItemMeta() instanceof SkullMeta))
            return item;

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        if (owningPlayer != null) {
            String owningPlayer = placeholders.parse(this.owningPlayer);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owningPlayer);

            if (ServerVersion.isBelowCurrent(ServerVersion.v1_13))
                skullMeta.setOwner(owningPlayer);
            else
                skullMeta.setOwningPlayer(offlinePlayer);
            item.setItemMeta(skullMeta);
        } else if (token != null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);

            Property property = token.contains("http://textures.minecraft.net/texture") ? getPropertyURL(token) : getProperty(token);
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

        return item;
    }

    private static Property getProperty(String texture) {
        return new Property("textures", texture);
    }

    private static Property getPropertyURL(String url) {
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        return new Property("textures", new String(encodedData));
    }

    public static SkullData fromString(String str) {
        if (str.toLowerCase().startsWith("owner:")) {
            str = str.replace("owner:", "");
            return new SkullData(str, null);
        } else if (str.toLowerCase().startsWith("token:")) {
            str = str.replace("owner:", "");
            return new SkullData(null, str);
        } else
            return new SkullData(str, null);
    }

    public static SkullData readSkullTexture(ItemStack item) {

        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta))
            return new SkullData(null, null);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Field profileField;

        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);

            GameProfile profile = (GameProfile) profileField.get(meta);

            if (profile == null || profile.getProperties() == null)
                return new SkullData(null, null);

            Collection<Property> properties = profile.getProperties().get("textures");
            if (properties != null) {

                Iterator<Property> iterator = properties.iterator();
                if (iterator.hasNext()) {
                    Property property = iterator.next();
                    return new SkullData(null, property.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SkullData(null, null);
    }
}