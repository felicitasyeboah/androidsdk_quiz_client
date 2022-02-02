package de.semesterprojekt.paf_android_quiz_client.model.messages;

import de.semesterprojekt.paf_android_quiz_client.model.User;
/**
 * Creates a ScoreMessageobject from JSONString-Data received from the server via Websocket
 */
public class ScoreMessage {
    private User user;
    private User opponent;
    private int userScore;
    private int opponentScore;

    public ScoreMessage(User user, User opponent, int userScore, int opponentScore) {
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.user = user;
        this.opponent = opponent;
    }

    public int getUserScore() {
        return userScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }
}
