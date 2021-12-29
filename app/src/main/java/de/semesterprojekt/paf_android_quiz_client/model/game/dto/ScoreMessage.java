package de.semesterprojekt.paf_android_quiz_client.model.game.dto;

import de.semesterprojekt.paf_android_quiz_client.model.User;

public class ScoreMessage {
    private User user;
    private User opponent;
    private int userPoints;
    private int opponentPoints;

    public ScoreMessage(int userPoints, int opponentPoints, User user, User opponent) {
        this.userPoints = userPoints;
        this.opponentPoints = opponentPoints;
        this.user = user;
        this.opponent = opponent;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public int getOpponentPoints() {
        return opponentPoints;
    }

    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }
}
