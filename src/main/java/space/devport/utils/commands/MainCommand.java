package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MainCommand extends AbstractCommand {

    private final String name;

    private String usage;

    private String description;

    public MainCommand(String name) {
        this.name = name;
    }

    public MainCommand usage(String usage) {
        this.usage = usage;
        return this;
    }

    public MainCommand description(String description) {
        this.description = description;
        return this;
    }

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    @Override
    protected CommandResult perform(CommandSender sender, String... args) {

        // Only the main command alone
        if (args.length == 0) {

            return CommandResult.SUCCESS;
        }

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equalsIgnoreCase(args[0])) continue;

            subCommand.perform(sender, args);
            break;
        }

        return CommandResult.SUCCESS;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getUsage() {
        return usage;
    }
}