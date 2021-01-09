package space.devport.utils.item.data;

import com.cryptomorin.xseries.SkullUtils;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.text.Placeholders;

import java.lang.reflect.Field;
import java.util.Objects;

public class SkullData {

    private static Field blockProfileField;

    @Getter
    @Setter
    private String identifier;

    @Getter
    private final transient Placeholders placeholders;

    private SkullData(@Nullable String identifier) {
        this.identifier = identifier;
        this.placeholders = new Placeholders();
    }

    private SkullData(@NotNull SkullData skullData) {
        Objects.requireNonNull(skullData);

        this.identifier = skullData.getIdentifier();
        this.placeholders = skullData.getPlaceholders();
    }

    @NotNull
    public static SkullData of(@Nullable String identifier) {
        return new SkullData(identifier);
    }

    @Contract("null -> null")
    public static SkullData of(@Nullable SkullData skullData) {
        return skullData == null ? null : new SkullData(skullData);
    }

    @Nullable
    public static SkullData readSkullTexture(@Nullable ItemStack item) {

        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta))
            return null;

        return SkullData.of(SkullUtils.getSkinValue(item.getItemMeta()));
    }

    @Contract("null -> null")
    public static SkullData readSkullTexture(@Nullable Block block) {
        return block == null ? null : of(base64fromBlock(block));
    }

    @Nullable
    public static String base64fromBlock(@NotNull Block block) {
        BlockState state = block.getState();

        if (!(state instanceof Skull))
            return null;

        Skull skull = (Skull) state;
        return base64FromBlockState(skull);
    }

    @Nullable
    private static String base64FromBlockState(@NotNull Skull skull) {
        Objects.requireNonNull(skull);

        try {
            if (blockProfileField == null) {
                blockProfileField = skull.getClass().getDeclaredField("profile");
                blockProfileField.setAccessible(true);
            }

            Object obj = blockProfileField.get(skull);

            if (!(obj instanceof GameProfile))
                return null;

            GameProfile profile = (GameProfile) obj;

            return profile.getProperties().get("textures").stream()
                    .filter(p -> p.getName().equals("textures"))
                    .findAny().map(Property::getValue).orElse(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Contract("null -> null;!null -> !null")
    public ItemStack apply(@Nullable ItemStack item) {

        if (item == null || Strings.isNullOrEmpty(identifier) ||
                item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta)) {
            return item;
        }

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        skullMeta = SkullUtils.applySkin(skullMeta, this.placeholders.parse(identifier));
        item.setItemMeta(skullMeta);
        return item;
    }

    @NotNull
    public SkullData parseWith(@NotNull Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(identifier);
    }
}