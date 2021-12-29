package de.semesterprojekt.paf_android_quiz_client.model.game;

/**
 * defines the types of the messages a game sends
 */
public enum MessageType {

    //Message types
    GAME_MESSAGE("GAME_MESSAGE"),
    SCORE_MESSAGE("SCORE_MESSAGE"),
    RESULT_MESSAGE("RESULT_MESSAGE"),

    //Timer Types
    START_TIMER_MESSAGE("START_TIMER_MESSAGE"),
    QUESTION_TIMER_MESSAGE("QUESTION_TIMER_MESSAGE"),
    SCORE_TIMER_MESSAGE("SCORE_TIMER_MESSAGE");

    private final String value;

    private MessageType(String value) {

        this.value = value;
    }

    //Override toString method to get the value of the enum
    @Override
    public String toString() {

        return this.value;
    }
}