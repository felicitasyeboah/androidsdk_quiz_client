package de.semesterprojekt.paf_android_quiz_client.model.messages;

/**
 * Creates a TimerMessageobject from JSONString-Data received from the server via Websocket
 */
public class TimerMessage {
    private int timeLeft;

    public TimerMessage(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

}
