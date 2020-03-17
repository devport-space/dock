package space.devport.utils.packutil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ConditionedRewardPack {

    @Getter
    @Setter
    private ConditionPack conditionPack;

    @Getter
    @Setter
    private RewardPack rewardPack;

    public boolean give(Player player) {
        if (conditionPack.check(player)) {
            rewardPack.give(player);
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return conditionPack.toString() + " ==> " + rewardPack.toString();
    }
}