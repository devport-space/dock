package space.devport.dock.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.item.data.Amount;
import space.devport.dock.item.data.Enchant;
import space.devport.dock.item.data.ItemDamage;
import space.devport.dock.item.data.SkullData;
import space.devport.dock.version.compound.CompoundUtil;
import space.devport.dock.version.compound.NBTContainer;
import space.devport.dock.struct.Context;
import space.devport.dock.text.placeholders.Placeholders;
import space.devport.dock.text.message.CachedMessage;
import space.devport.dock.text.message.Message;
import space.devport.dock.utility.ParseUtil;
import space.devport.dock.version.api.ICompound;

import java.util.*;

@Slf4j
public abstract class AbstractPrefab implements ItemPrefab {

    private static boolean COMPOUND_FACTORY_LOADED;

    // Check if all NMS compound classes are even loaded.
    static {
        try {
            CompoundUtil.has(null); // random call to cause an error
            new NBTContainer(null);
            COMPOUND_FACTORY_LOADED = true;
        } catch (NoClassDefFoundError e) {
            COMPOUND_FACTORY_LOADED = false;
        }
    }

    private static boolean checkCompoundLoaded() {
        if (!COMPOUND_FACTORY_LOADED) {
            log.warn("Attempted to add NBT to ItemPrefab when NMS classes are not loaded.");
            return false;
        }
        return true;
    }

    private final DockedPlugin plugin;

    // Item data

    private XMaterial material;

    private Amount amount = new Amount(1);

    private CachedMessage name = new CachedMessage();

    private CachedMessage lore = new CachedMessage();

    private final Set<Enchant> enchants = new HashSet<>();

    private final Set<ItemFlag> flags = new HashSet<>();

    private final Map<String, NBTContainer> nbt = new HashMap<>();

    // Extra data

    private SkullData skullData;

    private ItemDamage damage;

    private boolean glow;

    private final Placeholders placeholders = new Placeholders();

    // Additional builders

    private final Set<PrefabBuilder> builders = new HashSet<>();

    public AbstractPrefab(DockedPlugin plugin, @NotNull XMaterial material) {
        this.material = material;
        this.plugin = plugin;
    }

    public AbstractPrefab(@NotNull ItemPrefab prefab) {
        Objects.requireNonNull(prefab, "ItemPrefab cannot be null.");

        this.plugin = prefab.getPlugin();

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

    public AbstractPrefab(DockedPlugin plugin, @NotNull ItemStack item) {
        Objects.requireNonNull(item, "ItemStack cannot be null.");

        this.plugin = plugin;

        this.material = XMaterial.matchXMaterial(item.getType());
        this.amount = new Amount(item.getAmount());

        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return;

        this.name = new CachedMessage(meta.getDisplayName());
        this.lore = new CachedMessage(meta.getLore());

        this.enchants.addAll(Enchant.from(meta));
        this.flags.addAll(meta.getItemFlags());

        if (COMPOUND_FACTORY_LOADED)
            this.nbt.putAll(CompoundUtil.getMap(item));

        this.skullData = SkullData.readSkullTexture(item);
        this.damage = ItemDamage.from(item);
    }

    @Override
    public abstract @NotNull AbstractPrefab clone();

    @Override
    @Nullable
    public ItemStack build(@NotNull Context context) {
        Objects.requireNonNull(context, "Context cannot be null.");
        placeholders.addContext(context);
        return build();
    }

    @Override
    @Nullable
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
        List<String> lore = this.lore.parseWith(placeholders).parse().color().getContent();
        this.lore.pull();

        meta.setLore(lore);

        // Enchants
        enchants.forEach(enchant -> enchant.apply(meta));

        // Flags
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));

        item.setItemMeta(meta);

        // NBT
        if (COMPOUND_FACTORY_LOADED && !nbt.isEmpty()) {
            ICompound compound = CompoundUtil.getCompound(item);

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

    @Override
    public @NotNull ItemPrefab withType(@NotNull XMaterial material) {
        this.material = material;
        return this;
    }

    @Override
    public @NotNull ItemPrefab withAmount(@NotNull Amount amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public @NotNull ItemPrefab withName(Message name) {
        this.name = name == null ? new CachedMessage() : new CachedMessage(name);
        return this;
    }

    @Override
    public @NotNull ItemPrefab withLore(Message lore) {
        this.lore = lore == null ? new CachedMessage() : new CachedMessage(lore);
        return this;
    }

    @Override
    public @NotNull ItemPrefab appendLore(@NotNull Message lore) {
        this.lore.append(lore);
        return this;
    }

    @Override
    public @NotNull ItemPrefab addEnchant(@NotNull Enchant enchant) {
        this.enchants.add(enchant);
        return this;
    }

    /**
     * Overwrite the enchants.
     */
    @Override
    public @NotNull ItemPrefab withEnchants(@NotNull Collection<Enchant> enchants) {
        this.enchants.clear();
        this.enchants.addAll(enchants);
        return this;
    }

    @Override
    public @NotNull ItemPrefab addFlags(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    @Override
    public @NotNull ItemPrefab withFlags(ItemFlag... flags) {
        this.flags.clear();
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    @Override
    public @NotNull ItemPrefab addNBT(@NotNull String key, @NotNull NBTContainer container) {
        this.nbt.put(key, container);
        return this;
    }

    @Override
    public <T> @NotNull ItemPrefab addNBT(@NotNull String key, @NotNull T value) {
        if (!checkCompoundLoaded())
            return this;

        NBTContainer container = new NBTContainer(value);
        return addNBT(key, container);
    }

    @Override
    public @NotNull ItemPrefab addNBT(@NotNull Map<String, NBTContainer> nbt) {
        this.nbt.putAll(nbt);
        return this;
    }

    @Override
    public @NotNull ItemPrefab withNBT(@NotNull Map<String, NBTContainer> nbt) {
        this.nbt.clear();
        this.nbt.putAll(nbt);
        return this;
    }

    @Override
    public @NotNull ItemPrefab withSkullData(SkullData skullData) {
        this.skullData = skullData;
        return this;
    }

    @Override
    public @NotNull ItemPrefab withDamage(ItemDamage damage) {
        this.damage = damage;
        return this;
    }

    @Override
    public @NotNull ItemPrefab withGlow(boolean b) {
        this.glow = b;
        return this;
    }

    @Override
    public @NotNull ItemPrefab parseWith(@NotNull Placeholders placeholders) {
        this.placeholders.copy(placeholders);
        return this;
    }

    @Override
    public @NotNull ItemPrefab addBuilder(@NotNull PrefabBuilder builder) {
        this.builders.add(builder);
        return this;
    }

    // --------------- Clears and removes ---------------

    @Override
    public @NotNull ItemPrefab removeEnchant(@NotNull XEnchantment enchantment) {
        this.enchants.removeIf(enchant -> enchant.compare(enchantment));
        return this;
    }

    @Override
    public @NotNull ItemPrefab clearEnchants() {
        this.enchants.clear();
        return this;
    }

    @Override
    public @NotNull ItemPrefab removeFlag(@NotNull ItemFlag flag) {
        this.flags.remove(flag);
        return this;
    }

    @Override
    public @NotNull ItemPrefab clearFlags() {
        this.flags.clear();
        return this;
    }

    @Override
    public @NotNull ItemPrefab removeNBT(@NotNull String key) {
        this.nbt.remove(key);
        return this;
    }

    @Override
    public @NotNull ItemPrefab clearNBT() {
        this.nbt.clear();
        return this;
    }

    // --------------- Boolean terminal operations ---------------

    @Override
    public boolean hasEnchantment(XEnchantment enchantment) {
        return this.enchants.stream().anyMatch(enchant -> enchant.compare(enchantment));
    }

    @Override
    public boolean hasFlag(ItemFlag flag) {
        return this.flags.contains(flag);
    }

    @Override
    public boolean hasNBT() {
        return !this.nbt.isEmpty();
    }

    @Override
    public boolean hasNBT(String key) {
        return this.nbt.containsKey(key);
    }

    @Override
    public <T> boolean hasNBTValue(String key, T value) {
        return hasNBT(key) && this.nbt.get(key).getValue().equals(value);
    }

    @Override
    @Nullable
    public <T> T getNBTValue(String key, @NotNull Class<T> clazz) {
        NBTContainer container = getNBT().get(key);

        if (container == null || !clazz.isAssignableFrom(container.getValue().getClass()))
            return null;

        return clazz.cast(container.getValue());
    }

    @Override
    public boolean hasDamage() {
        return damage != null && damage.hasDamage();
    }

    @Override
    public @NotNull Map<String, NBTContainer> getNBT() {
        return nbt;
    }

    @Override
    public @NotNull XMaterial getMaterial() {
        return material;
    }

    @Override
    public @NotNull Amount getAmount() {
        return amount;
    }

    @Override
    public @NotNull CachedMessage getName() {
        return name;
    }

    @Override
    public @NotNull CachedMessage getLore() {
        return lore;
    }

    @Override
    public @NotNull Set<Enchant> getEnchants() {
        return enchants;
    }

    @Override
    public @NotNull Set<ItemFlag> getFlags() {
        return flags;
    }

    @Override
    public SkullData getSkullData() {
        return skullData;
    }

    @Override
    public ItemDamage getDamage() {
        return damage;
    }

    @Override
    public boolean isGlow() {
        return glow;
    }

    @Override
    public @NotNull DockedPlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull Placeholders getPlaceholders() {
        return placeholders;
    }
}
