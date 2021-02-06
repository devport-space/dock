package space.devport.dock.commands.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.commands.MainCommand;

/**
 * BuildableMainCommand provides setters and getters for command elements that are normally solved through abstraction.
 *
 * @author qwz
 */
public class BuildableMainCommand extends MainCommand {

    private String defaultUsage = "/%label%";
    private String defaultDescription = "Displays this.";

    public BuildableMainCommand(DockedPlugin plugin, String name) {
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

    @NotNull
    public BuildableMainCommand withDefaultUsage(String defaultUsage) {
        this.defaultUsage = defaultUsage;
        return this;
    }

    @NotNull
    public BuildableMainCommand withDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
        return this;
    }
}
