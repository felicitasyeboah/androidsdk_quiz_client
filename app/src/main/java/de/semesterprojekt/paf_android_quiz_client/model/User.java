package de.semesterprojekt.paf_android_quiz_client.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * represents the logged in User and his token
 */
public class User {

    private String username;
    private String token;

    //Contructor
    public User(String username, String token) {
        this.username = username;
        this.token = token;

    }

    /**
     * Takes an JSONObject and returns an User Object
     */
    public static User getUser(JSONObject jsonObject) throws JSONException {
        String username = jsonObject.getString("userName");
        String token = jsonObject.getString("token");

        return new User(username, token);

    }


    //TODO: prüfen, warum habe ich das nochmal drin?
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
