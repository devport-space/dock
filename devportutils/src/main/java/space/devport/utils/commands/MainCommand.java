package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.UsageFlag;
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

    @Getter
    private Message footer;

    @Getter
    private Message header;

    @Getter
    private String lineFormat;

    public MainCommand(String name) {
        super(name);

        this.header = new Message("&8&m        &r " + plugin.getColor() + "%pluginName% &7v&f%version% &8&m        &r");
        this.footer = new Message();

        this.lineFormat = plugin.getColor() + "%usage% &8- &7%description%";
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

    @Override
    public @NotNull Message getUsage() {
        if (plugin.use(UsageFlag.LANGUAGE))
            return language.get("Commands.Help." + getName() + ".Usage");
        return new Message(getDefaultUsage());
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length == 0) {
            constructHelp(sender, label).send(sender);
            return CommandResult.SUCCESS;
        }

        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getName().equalsIgnoreCase(args[0]) && !subCommand.getAliases().contains(args[0]))
                continue;

            args = Arrays.copyOfRange(args, 1, args.length, String[].class);

            subCommand.runCommand(sender, label, args);
            return CommandResult.SUCCESS;
        }

        constructHelp(sender, label).send(sender);
        return CommandResult.SUCCESS;
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
                    .filter(sc -> sc.match(args[0]))
                    .findAny().orElse(null);

            if (subCommand != null) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                return filterSuggestions(subCommand.requestTabComplete(sender, newArgs), newArgs.length > 0 ? newArgs[newArgs.length - 1] : "");
            }
        }
        return new ArrayList<>();
    }

    private Message constructHelp(CommandSender sender, String label) {

        Message help = (plugin.use(UsageFlag.LANGUAGE) ? language.get("Commands.Help.Header") : new Message(header)).parseWith(plugin.getGlobalPlaceholders());

        String lineFormat = (plugin.use(UsageFlag.LANGUAGE) ? language.get("Commands.Help.Sub-Command-Line").color().toString() : getLineFormat());

        Placeholders commandParams = new Placeholders()
                .copy(plugin.getGlobalPlaceholders())
                .add("%label%", label);

        String usage = getUsage().parseWith(commandParams).color().toString();
        String description = getDescription().parseWith(commandParams).color().toString();

        commandParams.add("%usage%", usage)
                .add("%description%", description);

        if (!getUsage().isEmpty() || !getDescription().isEmpty())
            help.append(commandParams.parse(lineFormat));

        for (SubCommand subCommand : this.subCommands) {
            if (subCommand.getUsage().isEmpty() && subCommand.getDescription().isEmpty())
                continue;

            if (!subCommand.getPreconditions().check(sender, true))
                continue;

            usage = subCommand.getUsage().parseWith(commandParams).color().toString();
            description = subCommand.getDescription().parseWith(commandParams).color().toString();

            commandParams.add("%usage%", usage)
                    .add("%description%", description);
            help.append(commandParams.parse(lineFormat));
        }

        if (!footer.isEmpty())
            help.append(footer);

        return help.parseWith(commandParams);
    }

    public MainCommand withFooter(Message footer) {
        this.footer = footer;
        return this;
    }

    public MainCommand withHeader(Message header) {
        this.header = header;
        return this;
    }

    public MainCommand withLineFormat(String lineFormat) {
        this.lineFormat = lineFormat;
        return this;
    }

    public void addLanguage() {
        if (plugin.use(UsageFlag.LANGUAGE)) {

            if (getDefaultUsage() != null)
                language.addDefault("Commands.Help." + getName() + ".Usage", getDefaultUsage());
            if (getDefaultDescription() != null)
                language.addDefault("Commands.Help." + getName() + ".Description", getDefaultDescription());

            language.addDefault("Commands.Help.Header", header.toString());
            language.addDefault("Commands.Help.Footer", footer.toString());
            language.addDefault("Commands.Help.Line-Format", lineFormat);
        }

        this.subCommands.forEach(SubCommand::addLanguage);
    }

    public MainCommand addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        subCommand.withParent(this);
        return this;
    }
}