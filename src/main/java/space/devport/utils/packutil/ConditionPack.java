package space.devport.utils.packutil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle various conditions.
 *
 * @author Wertik1206
 * */
@Builder(toBuilder = true)
public class ConditionPack {

    // Does the player have to be an operator?
    @Getter
    @Setter
    @Builder.Default
    private boolean operator = false;

    // Min and max player health
    // Values with 0 are ignored.
    @Getter
    @Setter
    @Builder.Default
    private double minHealth = 0;
    @Getter
    @Setter
    @Builder.Default
    private double maxHealth = 0;

    // Permission conditions the player has to meet
    // TODO Logic operators AND/OR
    @Getter
    @Builder.Default
    private final List<String> permissions = new ArrayList<>();
    
    // List of possible worlds the player can be in to match thiscondition
    @Getter
    @Builder.Default
    private final List<String> worlds = new ArrayList<>();

    // Check whether a player meets certain conditions
    public boolean check(Player player) {
        return true;
    }
}