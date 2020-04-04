package space.devport.utils.commands.struct;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Preconditions {

    @Getter
    private boolean operator = false;

    @Getter
    private List<String> permissions = new ArrayList<>();

    public Preconditions() {
    }

    public boolean check(CommandSender sender) {
        if (!sender.isOp() && operator) return false;
        return permissions.stream().allMatch(sender::hasPermission);
    }

    public Preconditions operator(boolean b) {
        this.operator = b;
        return this;
    }

    public Preconditions permissions(String... permissions) {
        this.permissions = Arrays.asList(permissions);
        return this;
    }
}
