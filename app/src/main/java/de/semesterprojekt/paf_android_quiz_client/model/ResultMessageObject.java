package de.semesterprojekt.paf_android_quiz_client.model;

import androidx.annotation.NonNull;

public class ResultMessageObject {

    boolean isHighScore;
    User user;
    User opponent;
    int userScore;
    int opponentScore;

    public ResultMessageObject(boolean isHighScore, User user, User opponent, int userScore, int opponentScore) {
        this.isHighScore = isHighScore;
        this.user = user;
        this.opponent = opponent;
        this.userScore = userScore;
        this.opponentScore = opponentScore;
    }

    @NonNull
    @Override
    public String toString() {
        return "isHighScore: " + Boolean.toString(isHighScore) + "\n" +
                "Player1: " + user.toString() + "\n" +
                "Player2: " + opponent.toString() + "\n" +
                "Player1 Score: " + Integer.toString(userScore) + "\n" +
                "Player2 Score: " + Integer.toString(opponentScore) + "\n";
    }

    public boolean isHighScore() {
        return isHighScore;
    }

    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }

    public int getUserScore() {
        return userScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }
}
