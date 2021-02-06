package space.devport.dock.callbacks.exception;

import space.devport.dock.callbacks.CallbackContent;

public class CallbackException extends Exception {

    public CallbackException(CallbackContent content) {
        super(composeMessage(content), content.getThrowable());
    }

    private static String composeMessage(CallbackContent content) {
        StringBuilder str = new StringBuilder();
        if (content.getVariableRaw() != null)
            str.append(String.format("Variable: %s", content.getVariableRaw()));
        if (content.getInputRaw() != null)
            str.append(String.format("Input: %s", content.getInputRaw()));
        return str.toString();
    }
}
