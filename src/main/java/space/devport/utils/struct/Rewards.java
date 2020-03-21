package space.devport.utils.struct;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.realized.tokenmanager.TokenManagerPlugin;
import org.bukkit.entity.Player;
import space.devport.utils.DevportUtils;
import space.devport.utils.item.Amount;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.text.Message;
import space.devport.utils.text.Placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class to handle various player & server rewards.
 *
 * @author Wertik1206
 */
@Builder(builderMethodName = "Builder", toBuilder = true, builderClassName = "RewardPackBuilder")
public class Rewards {

    @Getter
    @Setter
    @Builder.Default
    private Amount tokens = new Amount(0);

    @Getter
    @Setter
    @Builder.Default
    private Amount money = new Amount(0);

    @Getter
    @Builder.Default
    private final List<ItemBuilder> items = new ArrayList<>();

    @Getter
    @Setter
    @Builder.Default
    private Message inform = new Message();

    @Getter
    @Setter
    @Builder.Default
    private Message broadcast = new Message();

    @Getter
    @Builder.Default
    private final List<String> commands = new ArrayList<>();

    @Getter
    @Setter
    @Builder.Default
    private Placeholders format = new Placeholders();

    private final Random random = new Random();

    // Reward a player
    public void give(Player player) {

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
                    .parseWith(format)
                    .build());
        }

        // Inform - to player
        inform.setPlaceholders(format).send(player);

        // Broadcast - to all players
        broadcast.setPlaceholders(format);
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
        cmd = format.parse(cmd);

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
}