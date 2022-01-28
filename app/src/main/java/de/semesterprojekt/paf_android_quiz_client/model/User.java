package de.semesterprojekt.paf_android_quiz_client.model;

/**
 * represents the logged in User and his token and his opponent
 */
public class User {

    private String userName = "test";
    private String profileImage;


    //Contructors

    public User(String userName, String profileImage) {
        this.userName = userName;
        this.profileImage = profileImage;
    }

    /**
     * Takes an JSONObject and returns an User Object
     *
     */
//    public static User getUser(JSONObject jsonObject) throws JSONException {
//        String userName = jsonObject.getString("userName");
//        String token = jsonObject.getString("token");
//
//        return new User(userName, token);
//
//    }

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
                "profileImage: " + this.profileImage + "\n";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImageName(String profileImage) {
        this.profileImage = profileImage;
    }
}
