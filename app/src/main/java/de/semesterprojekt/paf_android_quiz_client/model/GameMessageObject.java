package de.semesterprojekt.paf_android_quiz_client.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameMessageObject {

    private String opponentName;
    private int userScore;
    private int opponentScore;
    private String category;
    private String question;
    private ArrayList<String> answers;
    private String userName;

    public GameMessageObject() {

    }

    public GameMessageObject(String userName, String opponentName, int userScore, int opponentScore, String category, String question, ArrayList<String> answers) {

        this.userName = userName;
        this.opponentName = opponentName;
        this.userScore = userScore;
        this.opponentScore = opponentScore;
        this.category = category;
        this.question = question;
        this.answers = answers;
    }

    // {"category":"Essen & Trinken","question":"Aus welchem Land kommt der Gouda?","answer":["Ghana","Niederlande","Frankreich","Luxemburg"],"userScore":0,"opponentScore":0,"user":{"userId":0,"userName":"Bernd","profileImage":null,"ready":false},"opponent":{"userId":0,"userName":"Beate","profileImage":null,"ready":false}}

}
