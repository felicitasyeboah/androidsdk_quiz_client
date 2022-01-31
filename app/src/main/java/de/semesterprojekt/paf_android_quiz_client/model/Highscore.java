package de.semesterprojekt.paf_android_quiz_client.model;

public class Highscore {
    private String timeStamp;
    private int userScore;
    private User user;

    public Highscore(String timeStamp, int userScore, User user) {

        this.timeStamp = timeStamp;
        this.userScore = userScore;
        this.user = user;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
    public int getUserScore() {
        return userScore;
    }
    public User getUser() {
        return user;
    }
}