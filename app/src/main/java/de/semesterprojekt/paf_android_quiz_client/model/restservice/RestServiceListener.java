package de.semesterprojekt.paf_android_quiz_client.model.restservice;

import org.json.JSONArray;

public abstract class RestServiceListener {


    public void onLogin(String userToken) {

    }

    public void onRegister(String username) {

    }

    public void onGetHighScores(JSONArray highScores) {
    }

    public void onGetImageFiles(JSONArray getImageFiles) {}

}
