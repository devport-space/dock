package space.devport.dock.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.UsageFlag;
import space.devport.dock.commands.struct.*;
import space.devport.dock.text.message.Message;
import space.devport.dock.text.placeholders.Placeholders;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class MainCommand extends AbstractCommand {

    @Getter
    private final List<SubCommand> subCommands = new ArrayList<>();

    @Getter
    private Message header;

    @Getter
    private Message footer;

    @Getter
    private final Map<String, Message> extraEntries = new LinkedHashMap<>();

    @Getter
    private String lineFormat;

    public MainCommand(DockedPlugin plugin, String name) {
        super(plugin, name);

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
    public @NotNull Message getUsage() {
        if (plugin.use(UsageFlag.LANGUAGE))
            return language.get("Commands.Help." + getName() + ".Usage");
        return new Message(getDefaultUsage());
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

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
    public @Nullable List<String> requestTabComplete(@NotNull CommandSender sender, String[] args) {

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
                return filterSuggestions(subCommand.getCompletion(sender, newArgs), newArgs.length > 0 ? newArgs[newArgs.length - 1] : "");
            }
        }
        return null;
    }

    private Message fetchLanguage(String path, Message def) {
        return plugin.use(UsageFlag.LANGUAGE) ? language.get(path) : def;
    }

    private String fetchLanguage(String path, String def) {
        return plugin.use(UsageFlag.LANGUAGE) ? language.get(path).toString() : def;
    }

    private Message constructHelp(CommandSender sender, String label) {

        Message help = fetchLanguage("Commands.Help.Header", header).parseWith(plugin.obtainPlaceholders());

        String lineFormat = fetchLanguage("Commands.Help.Sub-Command-Line", getLineFormat());

        Placeholders commandParams = plugin.obtainPlaceholders()
                .add("%label%", label);

        String usage = getUsage().parseWith(commandParams).color().toString();
        String description = getDescription().parseWith(commandParams).color().toString();

        commandParams.add("%usage%", usage)
                .add("%description%", description);

        if (!getUsage().isEmpty() || !getDescription().isEmpty())
            help.append(commandParams.parse(lineFormat));

        // Extra entries
        extraEntries.forEach((key, entry) -> {
            Message msg = fetchLanguage(String.format("Commands.Help.Extra.%s", key), entry);
            help.append(msg);
        });

        // Sub commands
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

        // Footer
        Message footer = fetchLanguage("Commands.Help.Footer", this.footer);

        if (!footer.isEmpty())
            help.append(footer);

        return help.parseWith(commandParams);
    }

    public void addLanguage() {
        if (plugin.use(UsageFlag.LANGUAGE)) {

            if (getDefaultUsage() != null)
                language.addDefault("Commands.Help." + getName() + ".Usage", getDefaultUsage());
            if (getDefaultDescription() != null)
                language.addDefault("Commands.Help." + getName() + ".Description", getDefaultDescription());

            language.addDefault("Commands.Help.Header", header.toString());
            extraEntries.forEach((key, entry) -> language.addDefault("Commands.Help.Extra." + key, entry.toString()));
            language.addDefault("Commands.Help.Footer", footer.toString());
            language.addDefault("Commands.Help.Line-Format", lineFormat);
        }

        subCommands.forEach(SubCommand::addLanguage);
    }

    @NotNull
    public MainCommand withFooter(@Nullable Message footer) {
        this.footer = footer;
        return this;
    }

    @NotNull
    public MainCommand withHeader(@Nullable Message header) {
        this.header = header;
        return this;
    }

    @NotNull
    public MainCommand withExtraEntry(@NotNull String key, @NotNull Message entry) {
        this.extraEntries.put(key, entry);
        return this;
    }

    @NotNull
    public MainCommand withLineFormat(@Nullable String lineFormat) {
        this.lineFormat = lineFormat;
        return this;
    }

    @NotNull
    public MainCommand withSubCommand(@NotNull SubCommand subCommand) {
        Objects.requireNonNull(subCommand, "Cannot add null as sub command.");

        this.subCommands.add(subCommand);
        subCommand.withParent(this);
        return this;
    }

    @Override
    public @NotNull MainCommand withExecutor(@Nullable CommandExecutor executor) {
        super.withExecutor(executor);
        return this;
    }

    @Override
    public @NotNull MainCommand withCompletionProvider(@Nullable CompletionProvider completionProvider) {
        super.withCompletionProvider(completionProvider);
        return this;
    }

    @Override
    public @NotNull MainCommand withRange(ArgumentRange range) {
        super.withRange(range);
        return this;
    }

    @Override
    public @NotNull MainCommand withRange(int min, int max) {
        super.withRange(min, max);
        return this;
    }

    @Override
    public @NotNull MainCommand withRange(int wanted) {
        super.withRange(wanted);
        return this;
    }

    @Override
    public @NotNull MainCommand modifyPreconditions(Consumer<Preconditions> modifier) {
        super.modifyPreconditions(modifier);
        return this;
    }
}