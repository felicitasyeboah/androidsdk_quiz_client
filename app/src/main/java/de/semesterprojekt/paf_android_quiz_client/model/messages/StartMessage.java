package de.semesterprojekt.paf_android_quiz_client.model.messages;

import de.semesterprojekt.paf_android_quiz_client.model.User;
/**
 * Creates a StartMessageobject from JSONString-Data received from the server via Websocket
 */
public class StartMessage {
    private User opponent;

    public StartMessage(User opponent, String profileImage) {
        this. opponent = opponent;
    }

    public User getOpponent() {
        return opponent;
    }
}
