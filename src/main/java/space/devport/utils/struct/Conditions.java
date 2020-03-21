package space.devport.utils.struct;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import space.devport.utils.item.Amount;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle various conditions.
 *
 * @author Wertik1206
 */
@Builder(builderMethodName = "Builder", toBuilder = true, builderClassName = "ConditionPackBuilder")
public class Conditions {

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

        if (!checkPermissions(player))
            return false;

        // Worlds
        if (!worlds.isEmpty() && !worlds.contains(player.getWorld().getName()))
            return false;

        // Health
        if (health.getInt() != 0)
            if (player.getHealth() > health.getHighValue() || player.getHealth() < health.getLowValue())
                return false;

        return true;
    }

    private boolean checkPermissions(Player p) {
        List<String> permissions = this.permissions;

        boolean pass = p.hasPermission(permissions.get(0));

        for (int i = 1; i < permissions.size(); i++) {
            String perm = permissions.get(i);

            if (perm.startsWith("AND ")) {
                pass = pass && p.hasPermission(perm.replace("AND ", ""));
            } else {
                // OR, perms with no prefix are taken as OR as well.
                if (p.hasPermission(perm.replace("OR ", ""))) {
                    return true;
                }
            }
        }

        return pass;
    }

    @Override
    public String toString() {
        return operator + " - " + health + " - " + permissions.toString() + " - " + worlds.toString();
    }
}