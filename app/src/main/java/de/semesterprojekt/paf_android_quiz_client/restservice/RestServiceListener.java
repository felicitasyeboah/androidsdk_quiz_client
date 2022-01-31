package de.semesterprojekt.paf_android_quiz_client.restservice;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RestServiceListener {


    public void onLogin(String userToken) {

    }

    public void onRegister(String username) {

    }
    public void onSessionExpired() {
    }

    public void onGetHighScores(JSONArray highScores) {
    }

    public void onGetPlayedGames(JSONObject history) {

    }

}
