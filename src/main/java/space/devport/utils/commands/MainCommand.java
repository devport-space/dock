package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MainCommand extends AbstractCommand {

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand(String name) {
        super(name);
        language.addDefault("Commands.Help." + name + ".Usage", getDefaultUsage());
        language.addDefault("Commands.Help." + name + ".Description", getDefaultDescription());
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length == 0) {
            constructHelp(label).send(sender);
            return CommandResult.SUCCESS;
        }

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equalsIgnoreCase(args[0]) && !subCommand.getAliases().contains(args[0])) continue;

            args = Arrays.copyOfRange(args, 1, args.length, String[].class);

            subCommand.runCommand(sender, label, args);
            return CommandResult.SUCCESS;
        }

        constructHelp(label).send(sender);
        return CommandResult.SUCCESS;
    }

    private Message constructHelp(String label) {
        Message help = language.get("Commands.Help.Header").parseWith(DevportPlugin.getInstance().getGlobalPlaceholders());

        String lineFormat = language.get("Commands.Help.Sub-Command-Line").color().toString();

        Placeholders commandParams = new Placeholders()
                .add("%usage%", getUsage().replace("%label%", label).color().toString())
                .add("%description%", getDescription().replace("%label%", label).color().toString());

        if (!getUsage().isEmpty() || !getDescription().isEmpty())
            help.append(commandParams.parse(lineFormat));

        for (SubCommand subCommand : this.subCommands) {
            if (subCommand.getUsage().isEmpty() && subCommand.getDescription().isEmpty()) continue;

            commandParams
                    .add("%usage%", subCommand.getUsage().replace("%label%", label).color().toString())
                    .add("%description%", subCommand.getDescription().replace("%label%", label).color().toString());
            help.append(commandParams.parse(lineFormat));
        }

        return help;
    }

    public MainCommand addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        subCommand.setParent(getName());
        subCommand.addLanguage();
        return this;
    }

    @Override
    public abstract String getDefaultUsage();

    @Override
    public abstract String getDefaultDescription();

    public boolean registerTabCompleter() {
        return true;
    }

    @Override
    public ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}