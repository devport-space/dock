package space.devport.utils.commands.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.text.Message;

public enum CommandResult {

    NOT_ENOUGH_ARGS("Commands.Not-Enough-Args"),
    TOO_MANY_ARGS("Commands.Too-Many-Args"),
    NO_CONSOLE("Commands.Only-Players"),
    NO_PLAYER("Commands.Only-Console"),
    FAILURE(),
    SUCCESS();

    @Getter
    private String path;

    @Setter
    private Message message;

    CommandResult(String... path) {
        if (path.length > 0) this.path = path[0];
    }

    public void sendMessage(CommandSender sender) {
        message.send(sender);
    }

    public Message getMessage() {
        return new Message(message);
    }
}
