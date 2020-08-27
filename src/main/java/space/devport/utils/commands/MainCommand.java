package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MainCommand extends AbstractCommand {

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand(String name) {
        super(name);

        if (DevportPlugin.getInstance().use(UsageFlag.LANGUAGE)) {
            if (getDefaultUsage() != null)
                language.addDefault("Commands.Help." + name + ".Usage", getDefaultUsage());
            if (getDefaultDescription() != null)
                language.addDefault("Commands.Help." + name + ".Description", getDefaultDescription());
        }
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length == 0) {
            constructHelp(sender, label).send(sender);
            return CommandResult.SUCCESS;
        }

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equalsIgnoreCase(args[0]) && !subCommand.getAliases().contains(args[0])) continue;

            args = Arrays.copyOfRange(args, 1, args.length, String[].class);

            subCommand.runCommand(sender, label, args);
            return CommandResult.SUCCESS;
        }

        constructHelp(sender, label).send(sender);
        return CommandResult.SUCCESS;
    }

    private Message constructHelp(CommandSender sender, String label) {

        DevportPlugin plugin = DevportPlugin.getInstance();

        Message help = (plugin.use(UsageFlag.LANGUAGE) ? language.get("Commands.Help.Header") : new Message("&8&m        &r &3%pluginName% &7v&f%version% &8&m        "))
                .parseWith(DevportPlugin.getInstance().getGlobalPlaceholders());

        String lineFormat = (plugin.use(UsageFlag.LANGUAGE) ? language.get("Commands.Help.Sub-Command-Line") : new Message("&3%usage% &8- &7%description%")).color().toString();

        Placeholders commandParams = new Placeholders()
                .add("%usage%", getUsage().replace("%label%", label).color().toString())
                .add("%description%", getDescription().replace("%label%", label).color().toString());

        if (!getUsage().isEmpty() || !getDescription().isEmpty())
            help.append(commandParams.parse(lineFormat));

        for (SubCommand subCommand : this.subCommands) {
            if (subCommand.getUsage().isEmpty() && subCommand.getDescription().isEmpty()) continue;

            if (!subCommand.getPreconditions().check(sender)) continue;

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
    public List<String> requestTabComplete(CommandSender sender, String[] args) {

        if (args.length == 1) {
            List<String> subCommands = getSubCommands().stream()
                    .map(SubCommand::getName)
                    .collect(Collectors.toList());
            return filterSuggestions(subCommands, args[0]);
        } else {
            SubCommand subCommand = getSubCommands().stream()
                    .filter(sc -> sc.getName().equalsIgnoreCase(args[0]) ||
                            sc.getAliases().stream()
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toList())
                                    .contains(args[0].toLowerCase()))
                    .findAny()
                    .orElse(null);

            if (subCommand != null) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return filterSuggestions(subCommand.requestTabComplete(sender, newArgs), newArgs.length > 0 ? newArgs[newArgs.length - 1] : "");
            }
        }
        return new ArrayList<>();
    }

    @Override
    public abstract @Nullable String getDefaultUsage();

    @Override
    public abstract @Nullable String getDefaultDescription();

    public boolean registerTabCompleter() {
        return true;
    }

    @Override
    public boolean checkRange() {
        return false;
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}