package space.devport.utils.struct;

import com.google.common.base.Strings;
import lombok.Getter;
import me.realized.tokenmanager.TokenManagerPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.economy.EconomyManager;
import space.devport.utils.item.data.Amount;
import space.devport.utils.item.ItemPrefab;
import space.devport.utils.item.impl.PrefabFactory;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.message.CachedMessage;
import space.devport.utils.text.message.Message;
import space.devport.utils.utility.DependencyUtil;
import space.devport.utils.utility.ParseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class to handle various player and server rewards.
 *
 * @author Wertik1206
 */
public class Rewards implements Cloneable {

    @Getter
    private Amount tokens = new Amount(0);

    @Getter
    private Amount money = new Amount(0);

    @Getter
    private List<ItemPrefab> items = new ArrayList<>();

    @Getter
    private CachedMessage inform = new CachedMessage();

    @Getter
    private CachedMessage broadcast = new CachedMessage();

    @Getter
    private List<String> commands = new ArrayList<>();

    @Getter
    private transient Placeholders placeholders = new Placeholders();

    private final transient Random random = new Random();

    @Getter
    private transient DevportPlugin plugin;

    public Rewards(DevportPlugin plugin) {
        this.plugin = plugin;
    }

    public Rewards(Rewards rewards) {
        if (rewards == null) return;

        this.tokens = rewards.getTokens();
        this.money = rewards.getMoney();
        this.items = new ArrayList<>(rewards.getItems());
        this.inform = new CachedMessage(rewards.getInform());
        this.broadcast = new CachedMessage(rewards.getBroadcast());
        this.commands = new ArrayList<>(rewards.getCommands());
        this.placeholders = Placeholders.of(rewards.getPlaceholders());
        this.plugin = rewards.getPlugin();
    }

    public void giveAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            give(player, false);
        }
        sendBroadcast();
    }

    // Reward a player
    public void give(@Nullable Player player, boolean... broadcast) {

        placeholders.copy(plugin.obtainPlaceholders());

        if (player != null) {
            placeholders.addContext(new Context().fromPlayer(player));

            // Tokens - TokenManager
            placeholders.add("%rewards_tokens%", giveTokens(player));

            // Money - Vault
            placeholders.add("%rewards_money%", giveMoney(player));

            // Items
            giveItems(player);

            // Inform - to player
            inform.setPlaceholders(placeholders).sendTo(player);
            inform.pull();
        }

        if (broadcast.length < 1 || broadcast[0])
            sendBroadcast();

        parseCommands(player);
    }

    public void sendBroadcast() {
        // Broadcast - to all players
        broadcast.setPlaceholders(placeholders);
        Bukkit.getOnlinePlayers().forEach(broadcast::sendTo);
        broadcast.pull();
    }

    public double giveTokens(@Nullable Player player) {
        if (player == null)
            return 0;

        int tokens = this.tokens.getInt();
        if (tokens != 0 && DependencyUtil.isEnabled("TokenManager")) {
            if (TokenManagerPlugin.getInstance().addTokens(player, tokens))
                return tokens;
        }
        return 0;
    }

    public double giveMoney(@Nullable Player player) {
        if (player == null)
            return 0;

        double money = this.money.getDouble();
        if (money != 0 && DependencyUtil.isEnabled("Vault") && plugin.isRegistered(EconomyManager.class)) {
            EconomyResponse response = plugin.getManager(EconomyManager.class).getEconomy().depositPlayer(player, money);
            if (response.transactionSuccess()) {
                return money;
            }
        }
        return 0;
    }

    public void giveItems(@Nullable Player player) {
        if (player == null) return;

        for (ItemPrefab item : items) {
            player.getInventory().addItem(item
                    .parseWith(placeholders)
                    .build());
        }
    }

    public void parseCommands(@Nullable Player player) {

        // Commands - with prefixes
        if (!commands.isEmpty()) {
            List<String> randomCommands = new ArrayList<>();

            for (String cmd : commands) {
                cmd = cmd.trim();
                if (cmd.startsWith("rand!"))
                    randomCommands.add(cmd.replace("rand!", ""));
                else
                    parseCommand(player, cmd);
            }

            // Pick one command
            if (!randomCommands.isEmpty()) {
                int random = this.random.nextInt(randomCommands.size());
                parseCommand(player, randomCommands.get(random));
            }
        }
    }

    // Parses a single command
    private void parseCommand(@Nullable Player player, @Nullable String cmd) {

        if (Strings.isNullOrEmpty(cmd) || cmd.contains("%player%") && player == null) return;

        // Parse placeholders
        cmd = placeholders.parse(cmd);

        if (cmd.startsWith("op!")) {
            // Execute as OP
            if (player != null) executeOp(cmd.replace("op!", ""), player);
        } else if (cmd.startsWith("p!")) {
            // Execute as player
            if (player != null) executePlayer(cmd.replace("p!", ""), player);
        } else
            // Execute as console
            executeConsole(cmd);
    }

    // Execute command as console
    private void executeConsole(String cmd) {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.trim()));
    }

    // Execute command as player
    private void executePlayer(String cmd, Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> player.performCommand(cmd.trim()));
    }

    // Execute as player with op
    private void executeOp(String cmd, Player player) {

        // If player is already op, we don't have to set it again
        if (player.isOp()) {
            executePlayer(cmd.trim(), player);
            return;
        }

        // Give op and take after command is executed
        player.setOp(true);
        executePlayer(cmd, player);
        player.setOp(false);
    }

    @Override
    public String toString() {
        return tokens.toString() + " - " + money.toString() + " - " + items.toString() + " - " + inform.toString() + " - " + broadcast.toString() + " - " + commands.toString();
    }

    public Rewards tokens(Amount amount) {
        this.tokens = amount;
        return this;
    }

    public Rewards tokens(int amount) {
        return tokens(new Amount(amount));
    }

    public Rewards tokens(int low, int high) {
        return tokens(new Amount(low, high));
    }

    public Rewards money(Amount amount) {
        this.money = amount;
        return this;
    }

    public Rewards money(double amount) {
        return money(new Amount(amount));
    }

    public Rewards money(double low, double high) {
        return money(new Amount(low, high));
    }

    public Rewards inform(Message message) {
        this.inform = new CachedMessage(message);
        return this;
    }

    public Rewards broadcast(Message message) {
        this.broadcast = new CachedMessage(message);
        return this;
    }

    public Rewards commands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    public Rewards addItem(ItemPrefab prefab) {
        this.items.add(PrefabFactory.of(prefab));
        return this;
    }

    public Rewards items(List<ItemPrefab> items) {
        this.items = items;
        return this;
    }

    public Rewards parseWith(Placeholders placeholders) {
        this.placeholders = Placeholders.of(placeholders);
        return this;
    }
}