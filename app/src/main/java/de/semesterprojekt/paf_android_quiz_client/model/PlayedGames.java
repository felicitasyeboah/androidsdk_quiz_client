package de.semesterprojekt.paf_android_quiz_client.model;

/**
 * Representation of the PlayedGames Request from the RestServerAPI
 */
public class PlayedGames {
    private String timeStamp;
    private int userScore;
    private int opponentScore;
    private User opponent;
    private int wonGames;
    private int lostGames;
    private int drawGames;
    private int averageScore;
    private int gameCount;

    public PlayedGames(String timeStamp, int userScore, int opponentScore, User opponent, int wonGames, int lostGames, int drawGames, int averageScore, int gameCount) {
        this.timeStamp = timeStamp;
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.opponent = opponent;
        this.wonGames = wonGames;
        this.lostGames = lostGames;
        this.drawGames = drawGames;
        this.averageScore = averageScore;
        this.gameCount = gameCount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getUserScore() {
        return userScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public User getOpponent() {
        return opponent;
    }

    public int getWonGames() {
        return wonGames;
    }

    public int getLostGames() {
        return lostGames;
    }

    public int getDrawGames() {
        return drawGames;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public int getGameCount() {
        return gameCount;
    }
}
