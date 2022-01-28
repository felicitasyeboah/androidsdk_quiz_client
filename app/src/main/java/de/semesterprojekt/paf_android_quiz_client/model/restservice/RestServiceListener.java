package de.semesterprojekt.paf_android_quiz_client.model.restservice;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RestServiceListener {


    public void onLogin(String userToken) {

    }

    public void onRegister(String username) {

    }

    public void onGetHighScores(JSONArray highScores) {
    }

    public void onGetHistory(JSONObject history) {

    }
    public void onGetImageFiles(JSONArray getImageFiles) {}

}
