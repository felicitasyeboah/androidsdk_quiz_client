package de.semesterprojekt.paf_android_quiz_client.model;

import android.media.Image;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

   //private int userId;
    private String username;
    private String token;
    //private boolean status;

    //Contructor
    public User(String username, String token) {
        //this.userId = userId;
        this.username = username;
        //this.status = status;
        this.token = token;

    }

    /**
     * Takes an JSONObject and returns an User Object
     */
    public static User getUser(JSONObject jsonObject) throws JSONException {
        //int userId = Integer.parseInt(jsonObject.getString("userId"));
        String username = jsonObject.getString("userName");
        //boolean status = Boolean.getBoolean(jsonObject.getString("ready"));
        String token = jsonObject.getString("token");
        User user = new User(username, token);

        return user;

    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj instanceof User) {
            User that = (User) obj;
            if (this.username.equalsIgnoreCase(that.username)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.username + "(" + this.token + ")";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
