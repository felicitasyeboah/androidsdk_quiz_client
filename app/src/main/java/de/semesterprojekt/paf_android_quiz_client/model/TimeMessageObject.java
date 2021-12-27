package de.semesterprojekt.paf_android_quiz_client.model;

public class TimeMessageObject {
    private int timeLeft;

    public TimeMessageObject(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
