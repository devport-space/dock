package space.devport.dock.configuration;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.item.ItemPrefab;
import space.devport.dock.item.impl.PrefabFactory;
import space.devport.dock.item.data.Amount;
import space.devport.dock.item.data.ItemDamage;
import space.devport.dock.item.data.SkullData;
import space.devport.dock.menu.MenuBuilder;
import space.devport.dock.menu.item.MenuItem;
import space.devport.dock.region.Region;
import space.devport.dock.struct.Conditions;
import space.devport.dock.struct.Rewards;
import space.devport.dock.utility.StringUtil;
import space.devport.dock.text.message.Message;
import space.devport.dock.utility.LocationUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class Configuration {

    @Getter
    private final String path;

    @Getter
    private final IDockedPlugin plugin;

    @Getter
    private File file;

    @Getter
    private FileConfiguration fileConfiguration;

    @Getter
    @Setter
    private boolean autoSave = false;

    /**
     * Creates a File from path and initializes a new Configuration with it.
     * <p>
     * Note: Does not load the Configuration upon initialization.
     *
     * @param plugin DevportPlugin instance.
     * @param path   String path to File.
     * @see Configuration#load()
     * @see Configuration#load(boolean)
     */
    public Configuration(@NotNull IDockedPlugin plugin, @NotNull String path) {
        this(plugin, createFile(path));
    }

    /**
     * Initialize a new Configuration with given file.
     * <p>
     * Note: Does not load the Configuration upon initialization.
     *
     * @param plugin DevportPlugin instance.
     * @param file   File to create Configuration from.
     * @see Configuration#load()
     * @see Configuration#load(boolean)
     */
    public Configuration(IDockedPlugin plugin, @NotNull File file) {
        this.plugin = plugin;
        this.file = file;
        this.path = file.getPath();
    }

    private static File createFile(String path) {
        return new File(path.contains(".yml") ? path : String.format("%s.yml", path));
    }

    private boolean create(boolean silent) {

        if (file.exists())
            return true;

        // Ensure folder structure
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            if (!silent)
                log.error("Could not create " + path);

        try {
            plugin.getPlugin().saveResource(path, false);
            log.debug("Created new " + path);
        } catch (Exception e) {
            try {
                if (!file.createNewFile()) {
                    if (!silent)
                        log.error("Could not create file at " + file.getAbsolutePath());
                    return false;
                }
            } catch (IOException e1) {
                if (!silent)
                    log.error("Could not create file at " + file.getAbsolutePath());
                e1.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Loads {@link YamlConfiguration} from {@link File}.
     * <p>
     * Ensures the file and it's folder structure exist, attempts to save from resources if not.
     * <p>
     * Equivalent to {@link #load(boolean)}
     *
     * @return True if the load was successful, false otherwise.
     */
    public boolean load() {
        return load(false);
    }

    /**
     * Loads {@link YamlConfiguration} from {@link File}.
     * <p>
     * Ensures the file and it's folder structure exist, attempts to save from resources if not.
     *
     * @param silent Doesn't print visible log messages if true.
     * @return True if the load was successful, false otherwise.
     */
    public boolean load(boolean silent) {
        file = new File(plugin.getDataFolder(), path);

        if (!create(silent))
            return false;

        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
        if (!silent)
            log.info(String.format("Loaded %s...", path));
        return true;
    }

    /**
     * Save this configuration to file.
     *
     * @return True if the save was successful, false if not.
     */
    public boolean save() {
        try {
            fileConfiguration.save(file);
            return true;
        } catch (IOException | NullPointerException e) {
            log.error(String.format("Could not save %s...", path));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves this Configuration to a different {@link File}.
     *
     * @param file File to save to.
     * @param set  If true sets file as the default.
     * @return True if the save was successful, false if not.
     */
    public boolean saveToFile(@NotNull File file, boolean set) {
        if (set) {
            this.file = file;
            return save();
        }

        try {
            this.fileConfiguration.save(file);
            return true;
        } catch (IOException | NullPointerException e) {
            log.error(String.format("Could not save %s...", path));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves this Configuration to a different {@link File}.
     * <p>
     * Equivalent to {@link Configuration#saveToFile(File, boolean)} passed (file, false).
     *
     * @param file File to save to.
     * @return True if the save was successful, false if not.
     * @throws NullPointerException If file is null.
     * @see Configuration#saveToFile(File, boolean)
     */
    public boolean saveToFile(@NotNull File file) {
        Objects.requireNonNull(file);
        return saveToFile(file, false);
    }

    /**
     * Saves current FileConfiguration to a different file by path.
     *
     * @param path Path to save to
     * @param set  Whether to set the file as default or not
     * @return True if the save was successful, false otherwise.
     * @throws NullPointerException If path is null.
     */
    public boolean saveToFile(@NotNull String path, boolean set) {
        Objects.requireNonNull(path);
        return saveToFile(createFile(path), set);
    }

    /**
     * Saves current FileConfiguration to a different file by path.
     * <p>
     * Equivalent to {@link Configuration#saveToFile(String, boolean)} passed (path, false).
     *
     * @param path Path to save to
     * @return True if the save was successful, false otherwise.
     * @throws NullPointerException If path is null.
     * @see Configuration#saveToFile(String, boolean)
     */
    public boolean saveToFile(String path) {
        return saveToFile(path, false);
    }

    /**
     * Deletes the file.
     * <p>
     * Note: Only deletes the file on system. Does not remove loaded Configuration.
     *
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete() {
        if (file.delete()) {
            return true;
        } else {
            log.error(String.format("Could not delete file %s", path));
            return false;
        }
    }

    /**
     * Deletes file and loads it again.
     * <p>
     * Equivalent to {@link Configuration#delete()} and {@link Configuration#load()}.
     *
     * @return True if both {@link Configuration#delete()} and {@link Configuration#load()} were successful, false otherwise.
     */
    public boolean clear() {
        return delete() && load();
    }

    @NotNull
    public ConfigurationSection section(String path) {
        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);
        return section != null ? section : fileConfiguration.createSection(path);
    }

    /**
     * Returns colored string retrieved from config.
     * Returns null if string is null or empty.
     *
     * @param path Path to string in config file
     * @return String with Bukkit color codes
     */
    @Nullable
    public String getColoredString(@Nullable String path) {
        if (Strings.isNullOrEmpty(path)) return null;
        return StringUtil.color(Strings.isNullOrEmpty(fileConfiguration.getString(path)) ? null : fileConfiguration.getString(path));
    }

    /**
     * Returns colored string retrieved from config.
     * If there's nothing on the path and default is not set, returns null.
     *
     * @param path         Path to string in config file
     * @param defaultValue Optional String, default value to return
     * @return String with Bukkit color codes
     */
    public String getColoredString(@Nullable String path, @NotNull String defaultValue) {
        return StringUtil.color(getString(path, defaultValue));
    }

    @Nullable
    public String getString(@Nullable String path) {
        return path != null ? fileConfiguration.getString(path) : null;
    }

    /*
     * A replacement for FileConfiguration.getString(String, String),
     * as it doesn't have correct annotations.
     */
    @Contract("null,_ -> param2;_,!null -> !null")
    public String getString(@Nullable String path, String defaultValue) {
        if (path == null)
            return defaultValue;

        String str = fileConfiguration.getString(path);
        return str == null ? defaultValue : str;
    }

    @Nullable
    public List<String> getStringList(@Nullable String path) {
        return path == null ? null : fileConfiguration.getStringList(path);
    }

    /**
     * Returns a list of strings.
     *
     * @param path        Path to list of strings in config file.
     * @param defaultList Default value.
     * @return List of strings.
     */
    @NotNull
    public List<String> getStringList(@Nullable String path, @NotNull List<String> defaultList) {
        if (Strings.isNullOrEmpty(path)) return defaultList;
        return fileConfiguration.getStringList(path).isEmpty() ? defaultList : fileConfiguration.getStringList(path);
    }

    /**
     * Returns colored string list retrieved from config.
     *
     * @param path Path to list of strings in config file
     * @return List of strings with Bukkit color codes
     */
    @Nullable
    public final List<String> getColoredList(@Nullable String path) {
        Message msg = getMessage(path);
        return msg == null ? null : msg.color().getContent();
    }

    /**
     * Returns colored string list retrieved from config.
     *
     * @param path        Path to list of strings in config file.
     * @param defaultList Default list to return when there's nothing on path.
     * @return List of strings with Bukkit color codes.
     */
    @NotNull
    public final List<String> getColoredList(@Nullable String path, @NotNull List<String> defaultList) {
        return getMessage(path, new Message(defaultList)).color().getContent();
    }

    /**
     * Returns a colored message from either a String of a List.
     *
     * @param path Path to list of strings in config file.
     * @return Multi-line colored string.
     */
    @NotNull
    public String getColoredMessage(@Nullable String path) {
        return getMessage(path, new Message()).color().toString();
    }

    /**
     * Returns an array from yaml parsed form a String List.
     *
     * @param path Path to list of strings
     * @return Array of strings
     */
    @NotNull
    public String[] getArrayList(@Nullable String path) {
        return getStringList(path, new ArrayList<>()).toArray(new String[0]);
    }

    /**
     * Returns an array from yaml parsed from String using given delimiter.
     *
     * @param path      Path to look at.
     * @param delimiter Delimiter to use.
     * @return String array.
     */
    @NotNull
    public String[] getArray(@Nullable String path, @NotNull String delimiter) {
        String str = getString(path);
        return str == null ? new String[0] : str.split(delimiter);
    }

    /**
     * Returns a character from the yaml.
     * Returns given default when null.
     *
     * @param path         Path to look at.
     * @param defaultValue Default to use.
     * @return char.
     */
    public char getChar(@Nullable String path, char defaultValue) {
        String str = getString(path);
        return Strings.isNullOrEmpty(str) ? defaultValue : str.toCharArray()[0];
    }

    /**
     * Returns an array of integers parsed from string.
     *
     * @param path String path to an Array.toString output.
     * @return Array of integers.
     */
    public int[] getInts(@Nullable String path) {
        String str = getString(path);

        if (str == null)
            return new int[]{};

        str = str.replace("[", "").replace("]", "")
                .replace(" ", "");

        if (Strings.isNullOrEmpty(str)) return new int[]{};

        return Arrays.stream(str.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    /**
     * Returns an array of integers parsed from string.
     *
     * @param path         String path to an Array.toString output.
     * @param defaultValue Default value to return if there's nothing on path.
     * @return Array of integers.
     */
    @Contract("null,_ -> param2")
    public int[] getInts(@Nullable String path, int[] defaultValue) {
        String str = getString(path);

        if (Strings.isNullOrEmpty(str))
            return defaultValue;

        str = str.replace("[", "").replace("]", "")
                .replace(" ", "");

        if (Strings.isNullOrEmpty(str))
            return new int[]{};

        return Arrays.stream(str.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    /**
     * Get a Message from the Configuration.
     *
     * @param path Path to the Message.
     * @return null if the message is completely absent. Return a blank one otherwise.
     */
    @Nullable
    public Message getMessage(@Nullable String path) {
        return getMessage(path, null);
    }

    /**
     * Loads a message builder either from String or from a list of strings.
     * Returns a default from Default.java when missing.
     *
     * @param path         Path to the Message.
     * @param defaultValue Default Message to return.
     * @return Message on the path.
     */
    @Contract("null,null -> null;null,_ -> param2")
    public Message getMessage(@Nullable String path, @Nullable Message defaultValue) {

        if (Strings.isNullOrEmpty(path))
            return defaultValue;

        if (fileConfiguration.isString(path)) {
            String msg = fileConfiguration.getString(path);

            if (Strings.isNullOrEmpty(msg))
                return new Message();

            return new Message(msg);
        } else if (fileConfiguration.isList(path)) {
            List<String> msg = fileConfiguration.getStringList(path);
            return new Message(msg);
        }

        return defaultValue;
    }

    /**
     * Loads a region from given path.
     *
     * @param path Path to the Region.
     * @return Region object.
     */
    @Nullable
    public Region getRegion(@Nullable String path) {
        Location min = LocationUtil.parseLocation(fileConfiguration.getString(path + "." + SubPath.REGION_MIN));
        Location max = LocationUtil.parseLocation(fileConfiguration.getString(path + "." + SubPath.REGION_MAX));

        if (min == null) {
            log.error("Could not get a Region from " + composePath(path) + ", minimum location didn't load.");
            return null;
        }

        if (max == null) {
            log.error("Could not get a Region from " + composePath(path) + ", maximum location didn't load.");
            return null;
        }

        return new Region(min, max);
    }

    /**
     * Saves a region to given path.
     *
     * @param path   String path to save to.
     * @param region Region to save.
     */
    public void setRegion(@Nullable String path, @Nullable Region region) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not set Region to " + composePath(path) + ", path is invalid.");
            return;
        }

        // Check region
        if (region == null) {
            log.error("Could not set Region to " + composePath(path) + ", region is null.");
            return;
        }

        ConfigurationSection section = fileConfiguration.createSection(path);

        section.set(SubPath.REGION_MIN.toString(), LocationUtil.composeString(region.getMin()));
        section.set(SubPath.REGION_MAX.toString(), LocationUtil.composeString(region.getMax()));

        if (autoSave)
            save();
    }

    /**
     * Loads a MenuBuilder from given path.
     *
     * @param path String path to load from.
     * @return MenuBuilder object.
     */
    @Nullable
    public MenuBuilder getMenuBuilder(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not get MenuBuilder from " + composePath(path) + ", path is invalid.");
            return null;
        }

        MenuBuilder menuBuilder = new MenuBuilder(plugin);

        ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

        if (section == null) {
            log.error("Could not get MenuBuilder from " + composePath(path) + ", path is invalid.");
            return null;
        }

        menuBuilder.title(section.getString(SubPath.MENU_TITLE.toString(), "My nice menu"));

        menuBuilder.slots(section.getInt(SubPath.MENU_SLOTS.toString(), 9));

        // Load inventory matrix
        if (section.contains(SubPath.MENU_MATRIX.toString()))
            menuBuilder.buildMatrix(getArrayList(path + "." + SubPath.MENU_MATRIX));

        ConfigurationSection itemsSection = section.getConfigurationSection(SubPath.MENU_ITEMS.toString());

        // Load items
        if (itemsSection != null) {
            for (String itemName : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = section.getConfigurationSection(SubPath.MENU_ITEMS + "." + itemName);

                if (itemSection == null) continue;

                MenuItem item = getMenuItem(path + "." + SubPath.MENU_ITEMS + "." + itemName);

                if (item == null) continue;

                // If it contains matrix-char
                if (itemSection.contains(SubPath.MENU_MATRIX_CHAR.toString()))
                    menuBuilder.addMatrixItem(getChar(itemSection.getCurrentPath() + "." + SubPath.MENU_MATRIX_CHAR, ' '), item);
                else
                    menuBuilder.setItem(item);
            }
        }

        return menuBuilder;
    }

    /**
     * Loads a MenuItem from given path.
     *
     * @param path String path to the item.
     * @return MenuItem object.
     */
    @Nullable
    public MenuItem getMenuItem(@Nullable String path) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not get MenuItem from " + composePath(path) + ", path is invalid.");
            return null;
        }

        // Load ItemBuilder
        ItemPrefab itemPrefab = getItem(path);

        String itemName = path.contains(".") ? path.split("\\.")[path.split("\\.").length - 1] : path;

        int slot = fileConfiguration.getInt(path + "." + SubPath.MENU_ITEM_SLOT, -1);

        if (itemPrefab == null) return null;

        MenuItem item = new MenuItem(plugin, itemPrefab, itemName, slot);

        item.setCancelClick(fileConfiguration.getBoolean(path + "." + SubPath.MENU_ITEM_CANCEL_CLICK, true));

        // Load rewards
        if (section(path).contains("rewards"))
            item.setRewards(getRewards(path.concat(".rewards")));

        return item;
    }

    /**
     * Loads an ItemPrefab from given path.
     *
     * @param path         String path to ItemBuilder.
     * @param defaultValue Default value.
     * @return ItemBuilder object.
     */
    @Contract("null,_ -> param2")
    public ItemPrefab getItem(@Nullable String path, @Nullable ItemPrefab defaultValue) {

        /// Check path
        if (Strings.isNullOrEmpty(path))
            return defaultValue;

        // Try to load
        try {
            ConfigurationSection section = fileConfiguration.getConfigurationSection(path);

            if (section == null) {
                log.debug("Invalid section at " + composePath(path) + ", returning null.");
                return defaultValue;
            }

            String type = section.getString(SubPath.ITEM_TYPE.toString());

            if (Strings.isNullOrEmpty(type)) {
                log.warn("Could not parse material from " + composePath(path) + ", returning null.");
                return defaultValue;
            }

            XMaterial xMaterial = XMaterial.matchXMaterial(type.toUpperCase()).orElse(null);

            if (xMaterial == null) {
                log.warn("Could not parse material from " + type + " at " + composePath(path) + ", returning null.");
                return defaultValue;
            }

            ItemPrefab prefab = PrefabFactory.createNew(xMaterial);

            // Damage
            if (section.contains(SubPath.ITEM_DAMAGE.toString()))
                prefab.withDamage(ItemDamage.fromString(section.getString(SubPath.ITEM_DAMAGE.toString())));

            // Display name
            if (section.contains(SubPath.ITEM_NAME.toString()))
                prefab.withName(section.getString(SubPath.ITEM_NAME.toString()));

            // Amount
            if (section.contains(SubPath.ITEM_AMOUNT.toString()))
                prefab.withAmount(getAmount(section.getCurrentPath() + "." + SubPath.ITEM_AMOUNT.toString(), new Amount(1)));

            if (section.contains(SubPath.ITEM_GLOW.toString()))
                prefab.withGlow(section.getBoolean(SubPath.ITEM_GLOW.toString()));

            // Lore
            if (section.contains(SubPath.ITEM_LORE.toString()))
                prefab.withLore(section.getStringList(SubPath.ITEM_LORE.toString()));

            // Enchants
            if (section.contains(SubPath.ITEM_ENCHANTS.toString())) {
                List<String> dataList = section.getStringList(SubPath.ITEM_ENCHANTS.toString());

                for (String dataString : dataList) {
                    int level = 1;

                    if (dataString.contains(SubPath.ITEM_ENCHANT_DELIMITER.toString())) {
                        level = Integer.parseInt(dataString.split(SubPath.ITEM_ENCHANT_DELIMITER.toString())[1]);
                        dataString = dataString.split(SubPath.ITEM_ENCHANT_DELIMITER.toString())[0];
                    }

                    XEnchantment xEnchantment = XEnchantment.matchXEnchantment(dataString).orElse(null);

                    if (xEnchantment == null) {
                        log.warn("Could not parse enchantment " + dataString + " at " + composePath(path));
                        continue;
                    }

                    prefab.addEnchant(xEnchantment, level);
                }
            }

            // Item Flags
            if (section.contains(SubPath.ITEM_FLAGS.toString()))
                for (String flagName : section.getStringList(SubPath.ITEM_FLAGS.toString())) {
                    ItemFlag flag = ItemFlag.valueOf(flagName);

                    prefab.withFlags(flag);
                }

            // Skull data
            if (section.contains(SubPath.ITEM_SKULL_DATA.toString()))
                prefab.withSkullData(SkullData.of(section.getString(SubPath.ITEM_SKULL_DATA.toString())));

            // NBT
            if (section.contains(SubPath.ITEM_NBT.toString()))
                for (String nbtString : section.getStringList(SubPath.ITEM_NBT.toString()))
                    if (nbtString.contains(SubPath.ITEM_NBT_DELIMITER.toString())) {
                        String[] arr = nbtString.split(SubPath.ITEM_NBT_DELIMITER.toString());
                        prefab.addNBT(arr[0], arr.length > 1 ? arr[1] : "");
                    }

            return prefab;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    /**
     * Loads an ItemBuilder from given path.
     *
     * @param path String path to ItemBuilder.
     * @return ItemBuilder object.
     */
    @Nullable
    public ItemPrefab getItem(@Nullable String path) {
        return getItem(path, null);
    }

    /**
     * Set an ItemPrefab object to a yaml file under a given path.
     *
     * @param path   Path to save it under.
     * @param prefab ItemPrefab to save.
     */
    public void setItem(@Nullable String path, @NotNull ItemPrefab prefab) {

        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not set ItemPrefab to " + composePath(path) + ", path null.");
            return;
        }

        ConfigurationSection section = section(path);

        section.set(SubPath.ITEM_TYPE.toString(), prefab.getMaterial().toString());

        if (prefab.hasDamage())
            section.set(SubPath.ITEM_DAMAGE.toString(), prefab.getDamage().toString());

        if (!(prefab.getAmount().isFixed() && prefab.getAmount().getFixedValue() == 1))
            section.set(SubPath.ITEM_AMOUNT.toString(), prefab.getAmount().isFixed() ? (int) prefab.getAmount().getFixedValue() : prefab.getAmount().toString());

        if (!prefab.getName().isEmpty())
            section.set(SubPath.ITEM_NAME.toString(), prefab.getName().toString());

        if (!prefab.getLore().isEmpty())
            section.set(SubPath.ITEM_LORE.toString(), prefab.getLore().getContent());

        if (!prefab.getEnchants().isEmpty()) {
            List<String> enchants = new ArrayList<>();
            prefab.getEnchants().forEach(e -> enchants.add(e.getEnchantment().name() + SubPath.ITEM_ENCHANT_DELIMITER + e.getLevel().toString()));
            section.set(SubPath.ITEM_ENCHANTS.toString(), enchants);
        }

        if (!prefab.getFlags().isEmpty())
            section.set(SubPath.ITEM_FLAGS.toString(), prefab.getFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));

        if (!prefab.getNBT().isEmpty()) {
            List<String> nbt = new ArrayList<>();
            prefab.getNBT().forEach((k, v) -> nbt.add(k + SubPath.ITEM_NBT_DELIMITER + v.toString()));
            section.set(SubPath.ITEM_NBT.toString(), nbt);
        }

        if (prefab.isGlow())
            section.set(SubPath.ITEM_GLOW.toString(), prefab.isGlow());

        if (prefab.getSkullData() != null)
            section.set(SubPath.ITEM_SKULL_DATA.toString(), prefab.getSkullData().toString());

        if (autoSave)
            save();
    }

    public void setMessage(@Nullable String path, @NotNull Message message) {
        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not set Message to " + composePath(path) + ", path is invalid.");
            return;
        }

        if (message.isEmpty())
            fileConfiguration.set(path, "");
        else {
            if (message.getContent().size() > 1) {
                fileConfiguration.set(path, message.getContent());
            } else
                fileConfiguration.set(path, message.getContent().get(0));
        }

        if (autoSave)
            save();
    }

    /**
     * Load an Amount from given path.
     *
     * @param path         String path to Amount.
     * @param defaultValue Optional Amount, default to return.
     * @return Amount object.
     */
    @NotNull
    public Amount getAmount(@Nullable String path, @NotNull Amount defaultValue) {

        // Check path
        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not load Amount from " + composePath(path) + ", path is invalid.");
            return defaultValue;
        }

        String dataStr = fileConfiguration.getString(path);

        if (Strings.isNullOrEmpty(dataStr))
            return defaultValue;

        try {
            if (dataStr.contains("-")) {
                String[] arr = dataStr.split("-");

                double low = Double.parseDouble(arr[0]);
                double high = Double.parseDouble(arr[1]);

                return new Amount(low, high);
            } else {
                int n = Integer.parseInt(dataStr);

                return new Amount(n);
            }
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /**
     * Load an Amount from given path.
     *
     * @param path String path to Amount.
     * @return Amount object.
     */
    @Nullable
    public Amount getAmount(@Nullable String path) {

        if (Strings.isNullOrEmpty(path)) {
            log.error("Could not load Amount from " + composePath(path) + ", path is invalid.");
            return null;
        }

        return Amount.fromString(fileConfiguration.getString(path));
    }

    @NotNull
    public Conditions getConditions(@Nullable String path) {
        Conditions conditions = new Conditions();

        if (Strings.isNullOrEmpty(path)) return conditions;

        conditions.operator(fileConfiguration.getBoolean(path + ".operator", false));
        conditions.permissions(getStringList(path + ".permissions", new ArrayList<>()));

        conditions.worlds(getStringList(path + ".worlds", new ArrayList<>()));

        conditions.health(getAmount(path + ".health", new Amount(0)));

        return conditions;
    }

    @NotNull
    public Rewards getRewards(@Nullable String path) {
        Rewards rewards = new Rewards(plugin);

        if (Strings.isNullOrEmpty(path)) return rewards;

        rewards.broadcast(getMessage(path + ".broadcast", new Message()));
        rewards.inform(getMessage(path + ".inform", new Message()));

        rewards.commands(getStringList(path + ".commands", new ArrayList<>()));

        rewards.money(getAmount(path + ".money", new Amount(0)));
        rewards.tokens(getAmount(path + ".tokens", new Amount(0)));

        ConfigurationSection itemsSection = fileConfiguration.getConfigurationSection(path + ".items");

        if (itemsSection == null) return rewards;

        for (String name : itemsSection.getKeys(false)) {
            ItemPrefab prefab = getItem(path + ".items." + name);

            if (prefab != null)
                rewards.addItem(prefab);
        }

        return rewards;
    }

    public void setRewards(String path, Rewards rewards) {

        if (Strings.isNullOrEmpty(path))
            return;

        ConfigurationSection section = section(path);

        if (!rewards.getTokens().isEmpty())
            section.set("tokens", rewards.getTokens().toString());
        if (!rewards.getMoney().isEmpty())
            section.set("money", rewards.getMoney().toString());
        if (!rewards.getInform().isEmpty())
            section.set("inform", rewards.getInform().getContent());
        if (!rewards.getBroadcast().isEmpty())
            section.set("broadcast", rewards.getBroadcast().getContent());
        if (!rewards.getCommands().isEmpty())
            section.set("commands", rewards.getCommands());

        for (int i = 0; i < rewards.getItems().size(); i++) {
            setItem(path + ".items." + i, rewards.getItems().get(i));
        }

        if (autoSave)
            save();
    }

    public String composePath(String path) {
        return Strings.isNullOrEmpty(path) ? String.format("file:%s", file.getName()) : String.format("file:%s@%s", file.getName(), path);
    }
}