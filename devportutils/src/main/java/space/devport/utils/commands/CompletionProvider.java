package space.devport.utils.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CompletionProvider {

    @Nullable
    List<String> provide(@NotNull CommandSender sender, @NotNull String[] args);
}
