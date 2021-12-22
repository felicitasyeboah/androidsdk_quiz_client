package de.semesterprojekt.paf_android_quiz_client.model;

import android.media.Image;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * represents the logged in User and his token
 */
public class User {

    private String userName = "test";
    private String token;
    private int userId;
    private boolean ready = true;


    //"user":{"userId":0,"userName":"Bernd","profileImage":null,"ready":false
    //Contructor


    public User(int userId, String userName, boolean ready) {
        this.userName = userName;
        this.userId = userId;
        this.ready = ready;
    }

    public User(String userName, String token) {
        this.userName = userName;
        this.token = token;
    }

    /**
     * Takes an JSONObject and returns an User Object
     */
    public static User getUser(JSONObject jsonObject) throws JSONException {
        String userName = jsonObject.getString("userName");
        String token = jsonObject.getString("token");

        return new User(userName, token);

    }

    public void updateUser(User user) {
        this.userName = user.userName;
        this.userId = user.userId;
        this.ready = user.ready;
    }


    //TODO: prüfen, warum habe ich das nochmal drin?
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj instanceof User) {
            User that = (User) obj;
            if (this.userName.equalsIgnoreCase(that.userName)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.userName + "(" + this.token + ")";
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean getIsReady() {
        return ready;
    }

    public void setIsReady(boolean ready) {
        this.ready = ready;
    }
}
