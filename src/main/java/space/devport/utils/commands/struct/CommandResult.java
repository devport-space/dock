package space.devport.utils.commands.struct;

import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.text.Message;

public enum CommandResult {

    NOT_ENOUGH_ARGS("%prefix%&cNot enough arguments!", "&cUsage: &7%usage%"),
    TOO_MANY_ARGS("%prefix%&cToo many arguments!", "&cUsage: &7%usage%"),
    NO_CONSOLE("%prefix%&cOnly for players!"),
    NO_PLAYER("%prefix%&cOnly for console!"),
    FAILURE(new Message()),
    SUCCESS(new Message());

    @Setter
    private Message message;

    CommandResult(Message message) {
        this.message = message;
    }

    CommandResult(String... message) {
        this.message = new Message(message);
    }

    public void sendMessage(CommandSender sender) {
        message.send(sender);
    }

    public Message getMessage() {
        return new Message(message);
    }
}
