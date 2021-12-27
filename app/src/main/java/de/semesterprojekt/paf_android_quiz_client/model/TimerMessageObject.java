package de.semesterprojekt.paf_android_quiz_client.model;

public class TimerMessageObject {
    private int timeLeft;

    public TimerMessageObject(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
