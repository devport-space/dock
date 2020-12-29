package space.devport.utils.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.item.nbt.NBTContainer;
import space.devport.utils.item.nbt.TypeUtil;
import space.devport.utils.struct.Context;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.message.CachedMessage;
import space.devport.utils.text.message.Message;
import space.devport.utils.utility.ParseUtil;
import space.devport.utils.version.api.ICompound;

import java.util.*;

public class ItemPrefab implements Cloneable {

    // Some ItemStack parameters are already saved inside other variables.
    // These NBT keys will be ignored in loading so they don't affect those.
    public static final List<String> FILTERED_NBT = Arrays.asList("Damage", "Enchantments", "display", "HideFlags");

    // Item data

    @Getter
    private XMaterial material;

    @Getter
    private Amount amount = new Amount(1);

    @Getter
    private CachedMessage name = new CachedMessage();

    @Getter
    private CachedMessage lore = new CachedMessage();

    @Getter
    private final Set<Enchant> enchants = new HashSet<>();

    @Getter
    private final Set<ItemFlag> flags = new HashSet<>();

    private final Map<String, NBTContainer> nbt = new HashMap<>();

    // Extra data

    @Getter
    private SkullData skullData;

    @Getter
    private ItemDamage damage;

    @Getter
    private boolean glow;

    @Getter
    private final Placeholders placeholders = new Placeholders();

    // Additional builders

    private final Set<PrefabBuilder> builders = new HashSet<>();

    private ItemPrefab(@NotNull XMaterial material) {
        this.material = material;
    }

    private ItemPrefab(ItemPrefab prefab) {
        this.material = prefab.getMaterial();
        this.amount = new Amount(prefab.getAmount());

        this.name = new CachedMessage(prefab.getName());
        this.lore = new CachedMessage(prefab.getLore());

        this.enchants.addAll(prefab.getEnchants());
        this.flags.addAll(prefab.getFlags());
        this.nbt.putAll(prefab.getNBT());

        this.skullData = SkullData.of(prefab.getSkullData());
        this.damage = ItemDamage.of(prefab.getDamage());

        this.glow = prefab.isGlow();

        this.placeholders.copy(prefab.getPlaceholders());
    }

    private ItemPrefab(@NotNull ItemStack item) {
        this.material = XMaterial.matchXMaterial(item.getType());
        this.amount = new Amount(item.getAmount());

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        this.name = new CachedMessage(meta.getDisplayName());
        this.lore = new CachedMessage(meta.getLore());

        this.enchants.addAll(Enchant.from(meta));
        this.flags.addAll(meta.getItemFlags());

        ICompound compound = ItemUtil.getCompound(item);
        for (String key : compound.getKeys()) {
            if (!FILTERED_NBT.contains(key))
                nbt.put(key, TypeUtil.containValue(compound, key));
        }

        this.skullData = SkullData.readSkullTexture(item);
        this.damage = ItemDamage.from(item);
    }

    @Contract("null -> null")
    public static ItemPrefab createNew(XMaterial material) {
        return material == null ? null : new ItemPrefab(material);
    }

    @Contract("null -> null")
    public static ItemPrefab createNew(Material material) {
        return material == null ? null : new ItemPrefab(XMaterial.matchXMaterial(material));
    }

    @Contract("null -> null")
    public static ItemPrefab of(ItemPrefab prefab) {
        return prefab == null ? null : new ItemPrefab(prefab);
    }

    @Contract("null -> null")
    public static ItemPrefab of(ItemStack item) {
        return item == null ? null : new ItemPrefab(item);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ItemPrefab clone() {
        return new ItemPrefab(this);
    }

    public ItemStack build(Context context) {
        this.placeholders.addContext(context);
        return build();
    }

    public ItemStack build() {

        if (material == null)
            return null;

        ItemStack item = material.parseItem();

        if (item == null)
            return null;

        item.setAmount(amount.getInt());

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return null;

        if (hasDamage())
            item = damage.apply(item);

        // Name
        String name = this.name.parseWith(placeholders).parse().color().toString();
        this.name.pull();

        meta.setDisplayName(name);

        // Lore
        List<String> lore = this.lore.parseWith(placeholders).parse().color().getMessage();
        this.lore.pull();

        meta.setLore(lore);

        // Enchants
        this.enchants.forEach(enchant -> enchant.apply(meta));

        // Flags
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));

        item.setItemMeta(meta);

        // NBT
        if (!nbt.isEmpty()) {
            ICompound compound = ItemUtil.getCompound(item);

            for (Map.Entry<String, NBTContainer> entry : nbt.entrySet()) {
                String key = placeholders.parse(entry.getKey());

                NBTContainer container = entry.getValue().clone();
                Object value = container.getValue();

                if (String.class.isAssignableFrom(value.getClass())) {
                    String str = (String) value;
                    str = placeholders.parse(str);
                    container.setValue(ParseUtil.parseNumber(str));
                }

                container.apply(compound, key);
            }

            item = compound.finish();
        }

        if (skullData != null) {
            skullData.parseWith(placeholders);
            item = skullData.apply(item);
        }

        // Run additional builders
        for (PrefabBuilder builder : builders)
            item = builder.apply(item);

        return item;
    }

    // --------------- Builder chain ---------------

    public ItemPrefab withType(XMaterial material) {
        this.material = material;
        return this;
    }

    public ItemPrefab withAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public ItemPrefab withAmount(double value) {
        this.amount = new Amount(value);
        return this;
    }

    public ItemPrefab withAmount(double low, double high) {
        this.amount = new Amount(low, high);
        return this;
    }

    public ItemPrefab withName(Message name) {
        this.name = new CachedMessage(name);
        return this;
    }

    public ItemPrefab withName(String name) {
        this.name = new CachedMessage(name);
        return this;
    }

    public ItemPrefab withLore(Message lore) {
        this.lore = new CachedMessage(lore);
        return this;
    }

    public ItemPrefab withLore(Collection<String> lore) {
        this.lore = new CachedMessage(new ArrayList<>(lore));
        return this;
    }

    public ItemPrefab withLore(String... lore) {
        this.lore = new CachedMessage(lore);
        return this;
    }

    public ItemPrefab appendLore(String... lore) {
        this.lore.append(lore);
        return this;
    }

    public ItemPrefab addEnchant(XEnchantment enchantment, int level) {
        this.enchants.add(new Enchant(enchantment, level));
        return this;
    }

    public ItemPrefab addEnchant(Enchant enchant) {
        this.enchants.add(enchant);
        return this;
    }

    /**
     * Overwrite the enchants.
     */
    public ItemPrefab withEnchants(Collection<Enchant> enchants) {
        this.enchants.clear();
        this.enchants.addAll(enchants);
        return this;
    }

    public ItemPrefab addFlags(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public ItemPrefab withFlags(ItemFlag... flags) {
        this.flags.clear();
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public ItemPrefab addNBT(String key, NBTContainer container) {
        this.nbt.put(key, container);
        return this;
    }

    public <T> ItemPrefab addNBT(String key, T value) {
        NBTContainer container = new NBTContainer(value);
        return addNBT(key, container);
    }

    public ItemPrefab addNBT(Map<String, NBTContainer> nbt) {
        this.nbt.putAll(nbt);
        return this;
    }

    public ItemPrefab withNBT(Map<String, NBTContainer> nbt) {
        this.nbt.clear();
        this.nbt.putAll(nbt);
        return this;
    }

    public ItemPrefab withSkullData(SkullData skullData) {
        this.skullData = skullData;
        return this;
    }

    public ItemPrefab withSkullData(String identifier) {
        this.skullData = SkullData.of(identifier);
        return this;
    }

    public ItemPrefab withDamage(ItemDamage damage) {
        this.damage = damage;
        return this;
    }

    public ItemPrefab withDamage(int damage) {
        this.damage = new ItemDamage(damage);
        return this;
    }

    public ItemPrefab withGlow() {
        return withGlow(true);
    }

    public ItemPrefab withGlow(boolean b) {
        this.glow = b;
        return this;
    }

    public ItemPrefab parseWith(Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    public ItemPrefab addBuilder(PrefabBuilder builder) {
        this.builders.add(builder);
        return this;
    }

    // --------------- Clears and removes ---------------

    public ItemPrefab removeEnchant(XEnchantment enchantment) {
        this.enchants.removeIf(enchant -> enchant.compare(enchantment));
        return this;
    }

    public ItemPrefab clearEnchants() {
        this.enchants.clear();
        return this;
    }

    public ItemPrefab removeFlag(ItemFlag flag) {
        this.flags.remove(flag);
        return this;
    }

    public ItemPrefab clearFlags() {
        this.flags.clear();
        return this;
    }

    public ItemPrefab removeNBT(String key) {
        this.nbt.remove(key);
        return this;
    }

    public ItemPrefab clearNBT() {
        this.nbt.clear();
        return this;
    }

    public ItemPrefab clearGlow() {
        return withGlow(false);
    }

    // --------------- Boolean terminal operations ---------------

    public boolean hasEnchantment(XEnchantment xEnchantment) {
        return this.enchants.stream().anyMatch(enchant -> enchant.compare(xEnchantment));
    }

    public boolean hasFlag(ItemFlag flag) {
        return this.flags.contains(flag);
    }

    public boolean hasNBT() {
        return !this.nbt.isEmpty();
    }

    public boolean hasNBT(String key) {
        return this.nbt.containsKey(key);
    }

    public <T> boolean hasNBTValue(String key, T value) {
        return this.nbt.get(key).getValue().equals(value);
    }

    @Nullable
    public <T> T getNBTValue(String key, Class<T> clazz) {
        NBTContainer container = getNBT().get(key);

        if (container == null || clazz == null || !clazz.isAssignableFrom(container.getValue().getClass()))
            return null;

        return clazz.cast(container.getValue());
    }

    public boolean hasDamage() {
        return damage != null && damage.hasDamage();
    }

    public Map<String, NBTContainer> getNBT() {
        return nbt;
    }
}
