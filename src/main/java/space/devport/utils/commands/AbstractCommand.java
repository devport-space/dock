package space.devport.utils.commands;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.ParseUtil;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand {

    protected final DevportPlugin plugin;

    protected final LanguageManager language;

    @Getter
    private final String name;

    @Getter
    @Setter
    protected Preconditions preconditions = new Preconditions();

    private String[] aliases = new String[]{};

    public AbstractCommand(String name) {
        this.name = name;
        this.plugin = DevportPlugin.getInstance();
        this.language = plugin.getManager(LanguageManager.class);
    }

    // This should be overridden by commands and performs the wanted action itself.
    protected abstract CommandResult perform(CommandSender sender, String label, String[] args);

    @Nullable
    public abstract ArgumentRange getRange();

    /**
     * Usage and description here are only taken as defaults, they're added to language by their name.
     */

    @Nullable
    public abstract String getDefaultUsage();

    @Nullable
    public abstract String getDefaultDescription();

    @NotNull
    public Message getUsage() {
        return new Message(getDefaultUsage());
    }

    @NotNull
    public Message getDescription() {
        if (!plugin.use(UsageFlag.LANGUAGE)) return new Message(getDefaultDescription());

        if (this instanceof SubCommand) {
            return ((SubCommand) this).getParent() != null ? language.get("Commands.Help." + ((SubCommand) this).getParent().getName() + "." + getName() + ".Description") : new Message();
        } else return language.get("Commands.Help." + getName() + ".Description");
    }

    // This is called from outside and sends the message automatically once it gets a response.
    public void runCommand(CommandSender sender, String label, String[] args) {

        Placeholders commandPlaceholders = new Placeholders(plugin.getGlobalPlaceholders())
                .add("%label%", label)
                .add("%usage%", getUsage().color().toString().replaceAll("(?i)\\Q%label%\\E", label));

        if (checkRange() && getRange() != null) {
            int res = getRange().compare(args.length);
            if (res > 0) {
                CommandResult.TOO_MANY_ARGS.getMessage()
                        .parseWith(commandPlaceholders)
                        .send(sender);
                return;
            } else if (res < 0) {
                CommandResult.NOT_ENOUGH_ARGS.getMessage()
                        .parseWith(commandPlaceholders)
                        .send(sender);
                return;
            }
        }

        // Check preconditions

        if (this.preconditions.isConsoleOnly() && sender instanceof Player) {
            CommandResult.NO_PLAYER.getMessage()
                    .parseWith(commandPlaceholders)
                    .send(sender);
            return;
        }

        if (this.preconditions.isPlayerOnly() && !(sender instanceof Player)) {
            CommandResult.NO_CONSOLE.getMessage()
                    .parseWith(commandPlaceholders)
                    .send(sender);
            return;
        }

        if (!this.preconditions.getPermissions().isEmpty() && this.preconditions.getPermissions().stream().noneMatch(sender::hasPermission)) {
            CommandResult.NO_PERMISSION.getMessage()
                    .parseWith(commandPlaceholders)
                    .send(sender);
            return;
        }

        if (this.preconditions.isOperator() && !sender.isOp()) {
            CommandResult.NOT_OPERATOR.getMessage()
                    .parseWith(commandPlaceholders)
                    .send(sender);
            return;
        }

        perform(sender, label, args).getMessage()
                .parseWith(commandPlaceholders)
                .send(sender);
    }

    public abstract List<String> requestTabComplete(CommandSender sender, String[] args);

    @NotNull
    protected List<String> filterSuggestions(@NotNull List<String> input, String arg) {
        Collections.sort(input);

        if (Strings.isNullOrEmpty(arg))
            return input;

        return input.stream()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Attempt to parse the argument into an enum.
     * If something went wrong, fetch a message from LanguageManager by {@param errorMessageKey} and send it to the sender.
     */
    protected <E extends Enum<E>> E parseEnum(@NotNull CommandSender sender, String arg, @NotNull Class<E> enumClazz, @NotNull String errorMessageKey) {
        return parse(sender, arg, str -> ParseUtil.parseEnum(arg, enumClazz), errorMessageKey);
    }

    /**
     * Attempt to parse the argument into an enum.
     * If something went wrong, send {@param errorMessage} to the sender.
     * Replace placeholder '%param%' with attempted argument.
     */
    protected <E extends Enum<E>> E parseEnum(@NotNull CommandSender sender, String arg, @NotNull Class<E> enumClazz, @NotNull Message errorMessage) {
        return parse(sender, arg, str -> ParseUtil.parseEnum(arg, enumClazz), errorMessage);
    }

    /**
     * Attempt to parse the argument.
     * If something went wrong, fetch a message from LanguageManager by key {@param errorMessageKey} and end it to the sender.
     */
    @Nullable
    protected <T> T parse(@NotNull CommandSender sender, String arg, @NotNull ArgumentParser<T> parser, @NotNull String errorMessageKey) {
        return parse(sender, arg, parser, language.getPrefixed(errorMessageKey));
    }

    /**
     * Attempt to parse the argument.
     * If something went wrong, send {@param errorMessage} to sender.
     * Replace placeholder '%param%' with the attempted argument.
     */
    @Nullable
    protected <T> T parse(@NotNull CommandSender sender, String arg, @NotNull ArgumentParser<T> parser, @NotNull Message errorMessage) {
        T result = parse(arg, parser);
        if (result == null) {
            errorMessage.replace("%param%", arg)
                    .send(sender);
            return null;
        }
        return result;
    }

    /**
     * Attempt to parse the argument.
     */
    @Nullable
    protected <T> T parse(String arg, @NotNull ArgumentParser<T> parser) {
        return parser.parse(arg);
    }

    public boolean checkRange() {
        return true;
    }

    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList(aliases));
    }

    protected void setAliases(String... aliases) {
        this.aliases = aliases;
    }

    public boolean match(String argument) {
        return this.getName().equalsIgnoreCase(argument) || getAliases().stream()
                .anyMatch(alias -> alias.equalsIgnoreCase(argument));
    }

    protected void setPermissions(String... permissions) {
        if (permissions.length == 0) {
            this.preconditions.permissions(craftPermission());
        } else this.preconditions.permissions(permissions);
    }

    protected String craftPermission() {
        String permission = plugin.getDescription().getName().toLowerCase();

        if (this instanceof SubCommand) {
            SubCommand subCommand = (SubCommand) this;
            if (subCommand.getParent() != null)
                permission += "." + subCommand.getParent().getName().toLowerCase();
        }
        return permission + "." + this.getName().toLowerCase();
    }
}