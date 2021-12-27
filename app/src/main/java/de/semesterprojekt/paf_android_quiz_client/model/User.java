package de.semesterprojekt.paf_android_quiz_client.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * represents the logged in User and his token and his opponent
 */
public class User {

    private String userName = "test";
    private String profileImage; //TODO: bild auslesen
    private String token;


    //Contructors
/*   public User(String userName, String profileImage) {
        this.userName = userName;
        this.profileImage = profileImage;
    }*/

    public User(String userName, String token) {
        this.userName = userName;
        //TODO: Wenn bilddatei uebertragen wird, wieder wegenehmen.
        if (token.endsWith(".png")) {
            this.profileImage = token;
        } else {
            this.token = token;
        }
    }

    /**
     * Takes an JSONObject and returns an User Object
     *
     */
    public static User getUser(JSONObject jsonObject) throws JSONException {
        String userName = jsonObject.getString("userName");
        String token = jsonObject.getString("token");

        return new User(userName, token);

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
        return "userName: " + this.userName + "\n" +
                "token: " + this.token + "\n" +
                "profileImage: " + this.profileImage + "\n";
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

}
