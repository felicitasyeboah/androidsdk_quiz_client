package de.semesterprojekt.paf_android_quiz_client.model.game.dto;

import de.semesterprojekt.paf_android_quiz_client.model.User;

public class StartMessage {
    private User user;
    private User opponent;
    private String profileImage;

    public StartMessage(User opponent, String profileImage) {
        this. opponent = opponent;
        this.profileImage = profileImage;
    }

    public User getOpponent() {
        return opponent;
    }

    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    //type: "START_MESSAGE",
    // message: {"opponent":{"userName":"ali","profileImage":"default7.png"},"type":"START_MESSAGE"}

}
