package space.devport.utils.commands.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.SubCommand;

public class BuildableSubCommand extends SubCommand {

    private String defaultUsage;
    private String defaultDescription;

    public BuildableSubCommand(DevportPlugin plugin, String name) {
        super(plugin, name);
        this.defaultUsage = String.format("/%%label%% %s", getName());
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return defaultUsage;
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return defaultDescription;
    }

    @NotNull
    public BuildableSubCommand withDefaultUsage(String defaultUsage) {
        this.defaultUsage = defaultUsage;
        return this;
    }

    @NotNull
    public BuildableSubCommand withDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
        return this;
    }
}
