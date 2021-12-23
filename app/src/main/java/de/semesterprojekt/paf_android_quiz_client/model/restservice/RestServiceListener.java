package de.semesterprojekt.paf_android_quiz_client.model.restservice;

import org.json.JSONArray;

import de.semesterprojekt.paf_android_quiz_client.model.User;

public abstract class RestServiceListener {


    public void onLogin(User user) {

    }

    public void onRegister(String username) {

    }

    public void onGetHighscore(JSONArray getHighscore) {

    }

}
