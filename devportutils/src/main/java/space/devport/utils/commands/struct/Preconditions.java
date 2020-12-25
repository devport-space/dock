package space.devport.utils.commands.struct;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.text.message.Message;

import java.util.*;

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

    private final Set<CheckWrapper<? extends CommandSender>> checks = new HashSet<>();

    @Data
    public static class CheckWrapper<T extends CommandSender> {

        private final PreconditionCheck<T> check;
        private final Message errorMessage;
        private final Class<T> clazz;

        public boolean isAssignable(@NotNull Class<?> other) {
            return clazz.isAssignableFrom(other);
        }

        @Nullable
        public <X> T cast(X object) {
            return object != null && isAssignable(object.getClass()) ? clazz.cast(object) : null;
        }

        public boolean verify(CommandSender sender) {
            T object = cast(sender);
            if (object == null)
                return false;
            return check.verify(object);
        }
    }

    public interface PreconditionCheck<T extends CommandSender> {
        boolean verify(T sender);
    }

    /**
     * Add a CommandSender check.
     * If the precondition doesn't pass, send {@param errorMessage} to sender.
     */
    @NotNull
    public <T extends CommandSender> Preconditions withCheck(@NotNull PreconditionCheck<T> check, @NotNull Class<T> clazz, @NotNull Message errorMessage) {
        this.checks.add(new CheckWrapper<>(check, errorMessage, clazz));
        return this;
    }

    private final DevportPlugin plugin;

    /**
     * Permissions are checked with an OR scheme ( has to have at least one of them )
     */
    public Preconditions permissions(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
        return this;
    }

    public Preconditions(DevportPlugin plugin) {
        this.plugin = plugin;
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
     * Add a CommandSender additional check.
     * If the check returned false, send a message from LanguageManager by key {@param errorMessageKey} to sender.
     */
    @NotNull
    public <T extends CommandSender> Preconditions withCheck(@NotNull PreconditionCheck<T> check, @NotNull Class<T> clazz, @NotNull String errorMessageKey) {
        Message errorMessage = plugin.use(UsageFlag.LANGUAGE) ? plugin.getManager(LanguageManager.class).get(errorMessageKey) : new Message("&cAdditional checks didn't pass.");
        this.checks.add(new CheckWrapper<>(check, errorMessage, clazz));
        return this;
    }

    /**
     * Run through added checks and send a message if set and silent is false.
     */
    public boolean checkAdditional(CommandSender sender, boolean... silent) {
        for (CheckWrapper<?> check : checks) {
            if (!check.verify(sender)) {
                if (silent.length > 0 && !silent[0])
                    check.getErrorMessage()
                            .parseWith(plugin.getGlobalPlaceholders())
                            .send(sender);
                return false;
            }
        }
        return true;
    }

}