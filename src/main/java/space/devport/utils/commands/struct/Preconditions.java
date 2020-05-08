package space.devport.utils.commands.struct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class Preconditions {

    @Getter
    private boolean operator = false;

    @Getter
    private List<String> permissions = new ArrayList<>();

    @Getter
    private boolean playerOnly = false;

    @Getter
    private boolean consoleOnly = false;

    public Preconditions operator(boolean... b) {
        this.operator = b.length <= 0 || b[0];
        return this;
    }

    public Preconditions playerOnly(boolean... b) {
        this.playerOnly = b.length <= 0 || b[0];
        return this;
    }

    public Preconditions consoleOnly(boolean... b) {
        this.consoleOnly = b.length <= 0 || b[0];
        return this;
    }

    /**
     * Permissions are checked with an OR scheme ( has to have at least one of them )
     */
    public Preconditions permissions(String... permissions) {
        this.permissions = new ArrayList<>(Arrays.asList(permissions));
        return this;
    }

    public boolean check(CommandSender sender) {
        if (!permissions.isEmpty() && permissions.stream().noneMatch(sender::hasPermission) ||
                operator && !sender.isOp() ||
                playerOnly && !(sender instanceof Player))
            return false;

        return !consoleOnly || (!(sender instanceof Player));
    }
}