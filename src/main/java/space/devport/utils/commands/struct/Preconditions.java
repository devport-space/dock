package space.devport.utils.commands.struct;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Builder
public class Preconditions {

    @Getter
    @Builder.Default
    private boolean operator = false;

    @Getter
    @Builder.Default
    private final List<String> permissions = new ArrayList<>();

    public boolean check(CommandSender sender) {
        return true;
    }
}
