package de.semesterprojekt.paf_android_quiz_client.model.messages;

import androidx.annotation.NonNull;

import de.semesterprojekt.paf_android_quiz_client.model.User;

public class ResultMessage {

    boolean isHighScore;
    User winner;
    User user;
    User opponent;
    int userScore;
    int opponentScore;
/*{"isHighScore":false,
"winner":{"userName":"ali","profileImage":"default7.png"},
"user":{"userName":"ali","profileImage":"default7.png"},
"opponent":{"userName":"feli","profileImage":"default2.png"},
"userScore":1546,"opponentScore":0,"type":"RESULT_MESSAGE"} */

    public ResultMessage(boolean isHighScore, User winner, User user, User opponent, int userScore, int opponentScore) {
        this.isHighScore = isHighScore;
        this.winner = winner;
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

    public User getWinner() {
        return winner;
    }

    public User getUser() {
        return user;
    }
    public String getUserName() {
        return user.getUserName();
    }
    public String getOpponentName() {
        return opponent.getUserName();
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
