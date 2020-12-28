package space.devport.utils.commands.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class Preconditions {

    @Getter
    @Setter
    private boolean operator = false;

    @Getter
    private final List<String> permissions = new ArrayList<>();

    @Getter
    @Setter
    private boolean playerOnly = false;

    @Getter
    @Setter
    private boolean consoleOnly = false;

    private final Set<CheckWrapper<? extends CommandSender>> checks = new HashSet<>();

    public Preconditions operator() {
        this.operator = true;
        return this;
    }

    public Preconditions playerOnly() {
        this.playerOnly = true;
        return this;
    }

    public Preconditions consoleOnly() {
        this.consoleOnly = true;
        return this;
    }

    /**
     * Permissions are checked with an OR scheme ( has to have at least one of them )
     */
    public Preconditions permissions(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * Add a CommandSender check.
     * If the precondition doesn't pass, send {@param errorMessage} to sender.
     */
    @NotNull
    public <T extends CommandSender> Preconditions withCheck(@NotNull PreconditionCheck<T> check, @NotNull Class<T> clazz, @NotNull Consumer<CommandSender> errorCallback) {
        this.checks.add(new CheckWrapper<>(check, errorCallback, clazz));
        return this;
    }

    /**
     * Check all preconditions.
     * Set {@param silent} to false to disable additional check messages.
     */
    public boolean check(@NotNull CommandSender sender, boolean... silent) {

        if (!permissions.isEmpty() && permissions.stream().noneMatch(sender::hasPermission) ||
                operator && !sender.isOp())
            return false;

        if (!checkAdditional(sender, silent))
            return false;

        if (sender instanceof Player) {
            return !consoleOnly;
        } else return !playerOnly;
    }

    /**
     * Run through added checks and send a message if set and silent is false.
     */
    public boolean checkAdditional(CommandSender sender, boolean... silent) {
        for (CheckWrapper<?> check : checks) {
            if (!check.verify(sender)) {
                if (silent.length > 0 && !silent[0])
                    check.getErrorCallback().accept(sender);
                return false;
            }
        }
        return true;
    }
}