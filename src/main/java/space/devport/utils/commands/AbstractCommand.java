package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportUtils;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand {

    // TODO: Hook all of the options to a commands.yml file for maximum customisation.

    @Getter
    private final String name;

    @Getter
    protected Preconditions preconditions = new Preconditions();

    protected String[] aliases = new String[]{};

    // This is called from outside and sends the message automatically once it gets a response.
    public void runCommand(CommandSender sender, String... args) {
        if (!preconditions.check(sender)) return;

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

    public AbstractCommand(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return new ArrayList<>(Arrays.asList(aliases));
    }
}