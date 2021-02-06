package space.devport.dock.commands.struct;

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
     * Set permissions to Preconditions. Permissions are checked with an OR scheme.
     *
     * @param permissions String[] permissions to set.
     * @return This Preconditions instance.
     */
    @NotNull
    public Preconditions permissions(@NotNull String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * Add an extra check to Preconditions. If the check doesn't pass run errorCallback.
     *
     * @param <T>           Type signature.
     * @param check         Check to add.
     * @param clazz         Class extending CommandSender defining {@code <T>}.
     * @param errorCallback ErrorCallback to fire on failure.
     * @return This Preconditions instance.
     */
    @NotNull
    public <T extends CommandSender> Preconditions withCheck(@NotNull PreconditionCheck<T> check, @NotNull Class<T> clazz, @NotNull Consumer<CommandSender> errorCallback) {
        this.checks.add(new CheckWrapper<>(check, errorCallback, clazz));
        return this;
    }

    /**
     * Check all preconditions.
     *
     * @param sender CommandSender to check for.
     * @param silent Doesn't fire attached error callbacks if true
     * @return True if all checks passed, false if any of them failed.
     * @see Preconditions#withCheck(PreconditionCheck, Class, Consumer)
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
     * Run through attached extra checks.
     *
     * @param sender CommandSender to check for.
     * @param silent Doesn't fire attached error callbacks if true.
     * @return True if all the checks passed, false if any of them failed.
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