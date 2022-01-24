package de.semesterprojekt.paf_android_quiz_client.model.game.dto;

import de.semesterprojekt.paf_android_quiz_client.model.User;

public class ScoreMessage {
    private User user;
    private User opponent;
    private int userScore;
    private int opponentScore;

    public ScoreMessage(User user, User opponent, int userScore, int opponentScore) {
        this.userScore = this.userScore;
        this.opponentScore = this.opponentScore;
        this.user = user;
        this.opponent = opponent;
    }
// {"user":{"userName":"ali","profileImage":"default7.png"},
// "opponent":{"userName":"feli",
// "profileImage":"default2.png"},"userScore":0,"opponentScore":985,"type":"SCORE_MESSAGE"}


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
