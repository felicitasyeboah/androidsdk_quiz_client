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
/*
[
    {
        "timeStamp": "2021-12-28T16:04:25.944+00:00",
        "userScore": 2871,
        "user": {
            "userName": "feli",
            "profileImage": "default2.png"
        }
    },
    {
        "timeStamp": "2022-01-23T15:07:49.111+00:00",
        "userScore": 2844,
        "user": {
            "userName": "Dekubitus",
            "profileImage": "default2.png"
        }
    },
    {
        "timeStamp": "2022-01-21T16:41:10.516+00:00",
        "userScore": 2826,
        "user": {
            "userName": "CandyMountain",
            "profileImage": "38811457-7ecd-4ad9-8d00-35d0998649d4.jpg"
        }
    },
    {
        "timeStamp": "2022-01-23T15:07:49.111+00:00",
        "userScore": 2803,
        "user": {
            "userName": "Someone",
            "profileImage": "68d761e3-c04c-4fb2-a2ee-b0d7317aa328.jpg"
        }
    },
    {
        "timeStamp": "2022-01-23T16:27:10.555+00:00",
        "userScore": 2783,
        "user": {
            "userName": "Martine",
            "profileImage": "default10.png"
        }
    }
]
 */