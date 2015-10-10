package gg.uhc.uhc.modules.timer;

public class TimeAppendedMessage implements TimerMessage {

    protected final String message;

    public TimeAppendedMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage(long secondsRemaining) {
        return message + " - " + TimeUtil.secondsToString(secondsRemaining);
    }
}
