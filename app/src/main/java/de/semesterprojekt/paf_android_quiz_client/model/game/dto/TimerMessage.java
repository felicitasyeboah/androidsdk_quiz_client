package de.semesterprojekt.paf_android_quiz_client.model.game.dto;

public class TimerMessage {
    private int timeLeft;

    public TimerMessage(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
