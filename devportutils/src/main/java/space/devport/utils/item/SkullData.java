package space.devport.utils.item;

import com.cryptomorin.xseries.SkullUtils;
import com.google.common.base.Strings;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.text.Placeholders;

public class SkullData {

    @Getter
    private final String identifier;

    @Getter
    private final transient Placeholders placeholders;

    public SkullData(String identifier) {
        this.identifier = identifier;
        this.placeholders = new Placeholders();
    }

    public SkullData(SkullData skullData) {
        this.identifier = skullData.getIdentifier();
        this.placeholders = skullData.getPlaceholders();
    }

    public static String formatUrl(String input) {
        if (Strings.isNullOrEmpty(input))
            return input;

        return input.startsWith("https://textures.minecraft.net/texture/") || input.startsWith("http://textures.minecraft.net/texture/") ? input : "https://textures.minecraft.net/texture/" + input;
    }

    public SkullData parseWith(Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    public ItemStack apply(ItemStack item) {

        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta)) {
            return item;
        }

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        if (identifier == null)
            return item;

        SkullUtils.applySkin(skullMeta, this.placeholders.parse(identifier));
        return item;
    }

    @Nullable
    public static SkullData fromString(@Nullable String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        if (str.startsWith("token:"))
            str = formatUrl(str.replace("token:", ""));
        else if (str.startsWith("url:"))
            str = str.replace("url:", "");
        else if (str.startsWith("owner:"))
            str = str.replace("owner:", "");

        return new SkullData(str);
    }

    @Override
    public String toString() {
        return identifier == null ? "" : identifier;
    }

    @Nullable
    public static SkullData readSkullTexture(ItemStack item) {

        if (item == null || item.getItemMeta() == null || !(item.getItemMeta() instanceof SkullMeta))
            return null;

        return new SkullData(SkullUtils.getSkinValue(item.getItemMeta()));
    }
}