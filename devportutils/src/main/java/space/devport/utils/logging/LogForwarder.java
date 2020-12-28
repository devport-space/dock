package space.devport.utils.logging;

public interface LogForwarder {
    void forward(ConsoleOutput consoleOutput, String message);
}
