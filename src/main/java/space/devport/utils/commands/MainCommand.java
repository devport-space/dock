package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand extends AbstractCommand {

    public MainCommand(String name) {
        super(name);
    }

    public MainCommand(String name, String usage, String description) {
        super(name, usage, description);
    }

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    @Override
    protected CommandResult perform(CommandSender sender, String... args) {

        if (args.length == 0) {
            return CommandResult.FAILURE;
        }

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equalsIgnoreCase(args[0]) && !subCommand.getAliases().contains(args[0])) continue;

            args = Arrays.copyOfRange(args, 1, args.length - 1, String[].class);

            subCommand.perform(sender, args);
            break;
        }

        return CommandResult.SUCCESS;
    }
}