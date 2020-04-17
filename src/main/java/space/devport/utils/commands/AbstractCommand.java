package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.devport.utils.DevportPlugin;
import space.devport.utils.DevportUtils;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.language.LanguageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand {

    private final LanguageManager language;

    @Getter
    private final String name;

    @Getter
    protected Preconditions preconditions = new Preconditions();

    protected String[] aliases = new String[]{};

    public AbstractCommand(String name) {
        this.name = name;
        this.language = DevportPlugin.getInstance().getLanguageManager();
    }

    // This is called from outside and sends the message automatically once it gets a response.
    public void runCommand(CommandSender sender, String... args) {
        if (checkRange()) {
            int res = getRange().check(args.length);
            if (res > 0) {
                CommandResult.TOO_MANY_ARGS.getMessage()
                        .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                        .replace("%usage%", getUsage())
                        .send(sender);
                return;
            } else if (res < 0) {
                CommandResult.NOT_ENOUGH_ARGS.getMessage()
                        .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                        .replace("%usage%", getUsage())
                        .send(sender);
                return;
            }
        }

        // Check preconditions

        if (this.preconditions.isConsoleOnly() && sender instanceof Player) {
            CommandResult.NO_PLAYER.getMessage()
                    .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                    .replace("%usage%", getUsage())
                    .send(sender);
            return;
        }

        if (this.preconditions.isPlayerOnly() && !(sender instanceof Player)) {
            CommandResult.NO_CONSOLE.getMessage()
                    .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                    .replace("%usage%", getUsage())
                    .send(sender);
            return;
        }

        if (!this.preconditions.getPermissions().isEmpty() && this.preconditions.getPermissions().stream().noneMatch(sender::hasPermission)) {
            CommandResult.NO_PERMISSION.getMessage()
                    .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                    .replace("%usage%", getUsage())
                    .send(sender);
            return;
        }

        if (this.preconditions.isOperator() && !sender.isOp()) {
            CommandResult.NOT_OPERATOR.getMessage()
                    .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                    .replace("%usage%", getUsage())
                    .send(sender);
            return;
        }

        perform(sender, args).getMessage()
                .replace("%prefix%", DevportUtils.getInstance().getConsoleOutput().getPrefix())
                .replace("%usage%", getUsage())
                .send(sender);
    }

    // This should be overridden by commands and performs the wanted action itself.
    protected abstract CommandResult perform(CommandSender sender, String... args);

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract ArgumentRange getRange();

    public boolean checkRange() {
        return true;
    }

    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList(aliases));
    }
}