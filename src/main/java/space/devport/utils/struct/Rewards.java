package space.devport.utils.struct;

import lombok.Getter;
import me.realized.tokenmanager.TokenManagerPlugin;
import org.bukkit.entity.Player;
import space.devport.utils.DevportUtils;
import space.devport.utils.item.Amount;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.text.message.Message;
import space.devport.utils.text.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class to handle various player & server rewards.
 *
 * @author Wertik1206
 */
public class Rewards {

    @Getter
    private Amount tokens = new Amount(0);

    @Getter
    private Amount money = new Amount(0);

    @Getter
    private List<ItemBuilder> items = new ArrayList<>();

    @Getter
    private Message inform = new Message();

    @Getter
    private Message broadcast = new Message();

    @Getter
    private List<String> commands = new ArrayList<>();

    @Getter
    private Placeholders placeholders = new Placeholders();

    private final Random random = new Random();

    // Reward a player
    public void give(Player player) {

        placeholders.add("%player%", player.getName());

        // Tokens - TokenManager
        int tokens = this.tokens.getInt();
        if (tokens != 0 && DevportUtils.getInstance().checkDependency("TokenManager"))
            TokenManagerPlugin.getInstance().addTokens(player, tokens);

        // Money - Vault
        double money = this.money.getDouble();
        if (money != 0 && DevportUtils.getInstance().checkDependency("Vault"))
            DevportUtils.getInstance().getEconomy().depositPlayer(player, money);

        // Items
        for (ItemBuilder item : items) {
            player.getInventory().addItem(item
                    .parseWith(placeholders)
                    .build());
        }

        // Inform - to player
        inform.setPlaceholders(placeholders).send(player);

        // Broadcast - to all players
        broadcast.setPlaceholders(placeholders);
        DevportUtils.getInstance().getPlugin().getServer().getOnlinePlayers().forEach(broadcast::send);

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
    private void parseCommand(Player player, String cmd) {

        // Parse placeholders
        cmd = placeholders.parse(cmd);

        if (cmd.startsWith("op!"))
            // Execute as OP
            executeOp(cmd.replace("op!", ""), player);
        else if (cmd.startsWith("p!"))
            // Execute as player
            executePlayer(cmd.replace("p!", ""), player);
        else
            // Execute as console
            executeConsole(cmd);
    }

    // Execute command as console
    private void executeConsole(String cmd) {
        DevportUtils.getInstance().getPlugin().getServer().dispatchCommand(DevportUtils.getInstance().getPlugin().getServer().getConsoleSender(), cmd.trim());
    }

    // Execute command as player
    private void executePlayer(String cmd, Player player) {
        player.performCommand(cmd.trim());
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
        player.performCommand(cmd.trim());
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
        this.inform = new Message(message);
        return this;
    }

    public Rewards broadcast(Message message) {
        this.broadcast = new Message(message);
        return this;
    }

    public Rewards commands(List<String> commands) {
        this.commands = commands;
        return this;
    }

    public Rewards addItem(ItemBuilder itemBuilder) {
        this.items.add(new ItemBuilder(itemBuilder));
        return this;
    }

    public Rewards items(List<ItemBuilder> items) {
        this.items = items;
        return this;
    }

    public Rewards placeholders(Placeholders placeholders) {
        this.placeholders = new Placeholders(placeholders);
        return this;
    }
}