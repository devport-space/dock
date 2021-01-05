package space.devport.utils.commands.build;

import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.CommandExecutor;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.text.message.Message;

public class BuildableMainCommand extends MainCommand {

    private String defaultUsage = "%label%";
    private String defaultDescription;

    public BuildableMainCommand(DevportPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return defaultUsage;
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return defaultDescription;
    }

    public BuildableMainCommand withDefaultUsage(String defaultUsage) {
        this.defaultUsage = defaultUsage;
        return this;
    }

    public BuildableMainCommand withDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
        return this;
    }

    @Override
    public BuildableMainCommand withFooter(Message footer) {
        super.withFooter(footer);
        return this;
    }

    @Override
    public BuildableMainCommand withHeader(Message header) {
        super.withHeader(header);
        return this;
    }

    @Override
    public BuildableMainCommand withExtraEntry(String key, Message entry) {
        super.withExtraEntry(key, entry);
        return this;
    }

    @Override
    public BuildableMainCommand withLineFormat(String lineFormat) {
        super.withLineFormat(lineFormat);
        return this;
    }

    @Override
    public BuildableMainCommand withSubCommand(SubCommand subCommand) {
        super.withSubCommand(subCommand);
        return this;
    }

    @Override
    public BuildableMainCommand withExecutor(CommandExecutor executor) {
        super.withExecutor(executor);
        return this;
    }
}
