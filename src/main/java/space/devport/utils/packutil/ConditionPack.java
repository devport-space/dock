package space.devport.utils.packutil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import space.devport.utils.itemutil.Amount;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle various conditions.
 *
 * @author Wertik1206
 */
@Builder(builderMethodName = "Builder", toBuilder = true, builderClassName = "ConditionPackBuilder")
public class ConditionPack {

    // Does the player have to be an operator?
    @Getter
    @Setter
    @Builder.Default
    private boolean operator = false;

    // Player health
    @Getter
    @Setter
    @Builder.Default
    private Amount health = new Amount(0);

    // Permission conditions the player has to meet
    // TODO Logic operators AND/OR
    @Getter
    @Builder.Default
    private final List<String> permissions = new ArrayList<>();

    // List of possible worlds the player can be in to match this condition
    @Getter
    @Builder.Default
    private final List<String> worlds = new ArrayList<>();

    // Checks if a player meets configured conditions
    public boolean check(Player player) {

        // Check operator status
        if (!player.isOp() && operator)
            return false;

        // TODO Permissions

        // Worlds
        if (!worlds.isEmpty() && !worlds.contains(player.getWorld().getName()))
            return false;

        // Health
        if (health.getInt() != 0)
            if (player.getHealth() > health.getHighValue() || player.getHealth() < health.getLowValue())
                return false;

        return true;
    }
}