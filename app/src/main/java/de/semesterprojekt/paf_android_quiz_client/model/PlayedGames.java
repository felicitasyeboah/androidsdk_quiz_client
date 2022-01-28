package de.semesterprojekt.paf_android_quiz_client.model;

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


/*
{
    "playedGames": [
        {
            "timeStamp": "2022-01-28T17:12:44.212+00:00",
            "userScore": 0,
            "opponentScore": 865,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T17:11:33.656+00:00",
            "userScore": 0,
            "opponentScore": 940,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:54:13.912+00:00",
            "userScore": 0,
            "opponentScore": 887,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:39:31.706+00:00",
            "userScore": 1707,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:35:29.000+00:00",
            "userScore": 0,
            "opponentScore": 569,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:34:19.439+00:00",
            "userScore": 0,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:32:50.169+00:00",
            "userScore": 0,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:31:59.718+00:00",
            "userScore": 0,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:31:11.856+00:00",
            "userScore": 864,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:28:05.760+00:00",
            "userScore": 0,
            "opponentScore": 1472,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:26:42.601+00:00",
            "userScore": 0,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:24:52.557+00:00",
            "userScore": 0,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:23:57.849+00:00",
            "userScore": 547,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:22:03.596+00:00",
            "userScore": 0,
            "opponentScore": 948,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:20:56.350+00:00",
            "userScore": 915,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:18:32.521+00:00",
            "userScore": 0,
            "opponentScore": 778,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:15:31.144+00:00",
            "userScore": 0,
            "opponentScore": 950,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:15:03.809+00:00",
            "userScore": 946,
            "opponentScore": 894,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:13:21.567+00:00",
            "userScore": 648,
            "opponentScore": 0,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        },
        {
            "timeStamp": "2022-01-28T15:11:56.126+00:00",
            "userScore": 672,
            "opponentScore": 1460,
            "opponent": {
                "userName": "ali",
                "profileImage": "default7.png"
            }
        }
    ],
    "wonGames": 6,
    "lostGames": 9,
    "drawGames": 5,
    "averageScore": 314,
    "gameCount": 20
}
 */