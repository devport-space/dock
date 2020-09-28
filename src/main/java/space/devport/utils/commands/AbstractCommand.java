package space.devport.utils.commands;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractCommand {

    private final DevportPlugin plugin;

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
        if (!plugin.use(UsageFlag.LANGUAGE)) return new Message(getDefaultUsage());

        if (this instanceof SubCommand) {
            return ((SubCommand) this).getParent() != null ? language.get("Commands.Help." + ((SubCommand) this).getParent().getName() + "." + getName() + ".Usage") : new Message();
        } else return language.get("Commands.Help." + getName() + ".Usage");
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
        if (checkRange() && getRange() != null) {
            int res = getRange().compare(args.length);
            if (res > 0) {
                CommandResult.TOO_MANY_ARGS.getMessage()
                        .parseWith(plugin.getGlobalPlaceholders())
                        .replace("%label%", label)
                        .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                        .send(sender);
                return;
            } else if (res < 0) {
                CommandResult.NOT_ENOUGH_ARGS.getMessage()
                        .parseWith(plugin.getGlobalPlaceholders())
                        .replace("%label%", label)
                        .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                        .send(sender);
                return;
            }
        }

        // Check preconditions

        if (this.preconditions.isConsoleOnly() && sender instanceof Player) {
            CommandResult.NO_PLAYER.getMessage()
                    .parseWith(plugin.getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        if (this.preconditions.isPlayerOnly() && !(sender instanceof Player)) {
            CommandResult.NO_CONSOLE.getMessage()
                    .parseWith(plugin.getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        if (!this.preconditions.getPermissions().isEmpty() && this.preconditions.getPermissions().stream().noneMatch(sender::hasPermission)) {
            CommandResult.NO_PERMISSION.getMessage()
                    .parseWith(plugin.getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        if (this.preconditions.isOperator() && !sender.isOp()) {
            CommandResult.NOT_OPERATOR.getMessage()
                    .parseWith(plugin.getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        perform(sender, label, args).getMessage()
                .parseWith(plugin.getGlobalPlaceholders())
                .replace("%label%", label)
                .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                .send(sender);
    }

    public abstract List<String> requestTabComplete(CommandSender sender, String[] args);

    protected List<String> filterSuggestions(List<String> input, String arg) {
        Collections.sort(input);
        if (Strings.isNullOrEmpty(arg)) return input;
        return input.stream().filter(o -> o.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    protected <T> T parse(CommandSender sender, String arg, Function<String, T> parser, Message errorMessage) {
        T result = parse(arg, parser);
        if (result == null) {
            errorMessage.replace("%param%", arg)
                    .send(sender);
            return null;
        }
        return result;
    }

    protected <T> T parse(String arg, Function<String, T> parser) {
        return parser.apply(arg);
    }

    protected <T> T parse(CommandSender sender, String arg, Function<String, T> parser, String errorMessageKey) {
        return parse(sender, arg, parser, language.getPrefixed(errorMessageKey));
    }

    protected String findSwitchValue(String[] args, String switchName) {
        String real = findSwitch(args, switchName);

        if (real == null) return null;

        List<String> argsList = Arrays.asList(args);
        int index = argsList.indexOf(real) + 1;

        if (index > argsList.size()) return null;

        return argsList.get(index);
    }

    protected String[] filterSwitch(String[] args, String switchName) {
        String real = findSwitch(args, switchName);
        args = Arrays.stream(args).filter(arg -> !arg.equalsIgnoreCase(real)).toArray(String[]::new);
        return args;
    }

    protected String findSwitch(String[] args, String switchName) {
        for (String arg : args) {
            String real = arg.replace("-", "").replace("-", "");

            if (switchName.toLowerCase().startsWith(real.toLowerCase())) {
                return arg;
            }
        }
        return null;
    }

    protected boolean containsSwitch(String[] args, String switchName) {
        return findSwitch(args, switchName) != null;
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