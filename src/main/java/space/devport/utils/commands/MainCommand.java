package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MainCommand extends AbstractCommand {

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand(String name) {
        super(name);
    }

    @Override
    protected CommandResult perform(CommandSender sender, String... args) {

        if (args.length == 0) {
            constructHelp().send(sender);
            return CommandResult.SUCCESS;
        }

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equalsIgnoreCase(args[0]) && !subCommand.getAliases().contains(args[0])) continue;

            args = Arrays.copyOfRange(args, 1, args.length, String[].class);

            subCommand.runCommand(sender, args);
            return CommandResult.SUCCESS;
        }

        constructHelp().send(sender);
        return CommandResult.SUCCESS;
    }

    private Message constructHelp() {
        Message help = new Message("&8&m        &r " + DevportPlugin.getInstance().getColor() + DevportPlugin.getInstance().getName() +
                " &7v&f" + DevportPlugin.getInstance().getDescription().getVersion() + "&8&m        ");

        help.append(DevportPlugin.getInstance().getColor() + getUsage() + " &8- &7" + getDescription());
        for (SubCommand subCommand : this.subCommands) {
            help.append(DevportPlugin.getInstance().getColor() + subCommand.getUsage() + " &8- &7" + subCommand.getDescription());
        }
        return help;
    }

    public MainCommand addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    // TODO: Hook to locale

    @Override
    public abstract String getUsage();

    @Override
    public abstract String getDescription();

    @Override
    public ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}