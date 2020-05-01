package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand {

    protected final LanguageManager language;

    @Getter
    private final String name;

    @Getter
    protected Preconditions preconditions = new Preconditions();

    protected String[] aliases = new String[]{};

    public AbstractCommand(String name) {
        this.name = name;
        this.language = DevportPlugin.getInstance().getLanguageManager();
    }

    // This should be overridden by commands and performs the wanted action itself.
    protected abstract CommandResult perform(CommandSender sender, String label, String[] args);

    @NotNull
    public abstract ArgumentRange getRange();

    /**
     * Usage and description here are only taken as defaults, they're added to language by their name.
     */

    @NotNull
    public abstract String getDefaultUsage();

    @NotNull
    public abstract String getDefaultDescription();

    @NotNull
    public Message getUsage() {
        if (this instanceof SubCommand) {
            return language.get("Commands.Help." + ((SubCommand) this).getParent() + "." + getName() + ".Usage");
        } else return language.get("Commands.Help." + getName() + ".Usage");
    }

    @NotNull
    public Message getDescription() {
        if (this instanceof SubCommand) {
            return language.get("Commands.Help." + ((SubCommand) this).getParent() + "." + getName() + ".Description");
        } else return language.get("Commands.Help." + getName() + ".Description");
    }

    // This is called from outside and sends the message automatically once it gets a response.
    public void runCommand(CommandSender sender, String label, String[] args) {
        if (checkRange()) {
            int res = getRange().check(args.length);
            if (res > 0) {
                CommandResult.TOO_MANY_ARGS.getMessage()
                        .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                        .replace("%label%", label)
                        .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                        .send(sender);
                return;
            } else if (res < 0) {
                CommandResult.NOT_ENOUGH_ARGS.getMessage()
                        .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                        .replace("%label%", label)
                        .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                        .send(sender);
                return;
            }
        }

        // Check preconditions

        if (this.preconditions.isConsoleOnly() && sender instanceof Player) {
            CommandResult.NO_PLAYER.getMessage()
                    .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        if (this.preconditions.isPlayerOnly() && !(sender instanceof Player)) {
            CommandResult.NO_CONSOLE.getMessage()
                    .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        if (!this.preconditions.getPermissions().isEmpty() && this.preconditions.getPermissions().stream().noneMatch(sender::hasPermission)) {
            CommandResult.NO_PERMISSION.getMessage()
                    .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        if (this.preconditions.isOperator() && !sender.isOp()) {
            CommandResult.NOT_OPERATOR.getMessage()
                    .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                    .replace("%label%", label)
                    .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                    .send(sender);
            return;
        }

        perform(sender, label, args).getMessage()
                .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders())
                .replace("%label%", label)
                .replace("%usage%", getUsage().color().toString().replace("%label%", label))
                .send(sender);
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
}