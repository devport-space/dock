package space.devport.utils.packutil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import space.devport.utils.itemutil.Amount;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle various player & server rewards.
 *
 * @author Wertik1206
 */
@Builder(builderMethodName = "Builder", toBuilder = true, builderClassName = "RewardPackBuilder")
public class RewardPack {

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
    private MessageBuilder inform = new MessageBuilder();

    @Getter
    @Setter
    @Builder.Default
    private MessageBuilder broadcast = new MessageBuilder();

    @Getter
    @Builder.Default
    private final List<String> commands = new ArrayList<>();

    @Getter
    @Setter
    @Builder.Default
    private ParseFormat format = new ParseFormat();

    // Reward a player
    public void reward(Player player) {
        // Do shit
    }

    public void addItem(ItemBuilder item) {
        items.add(item);
    }

    public void addCommand(String cmd) {
        commands.add(cmd);
    }
}