package de.semesterprojekt.paf_android_quiz_client.model;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RestServiceListener {


    public void onLogin(User user) {

    }


    public void onRegister(String username) {

    }

    public void onGetHighscore(JSONArray getHighscore) {

    }
}
