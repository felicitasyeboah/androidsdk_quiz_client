package de.semesterprojekt.paf_android_quiz_client.model;

/**
 * represents a simple user with name and profileimage-name
 */
public class User {

    private String userName;
    private String profileImage;


    //Contructor
    public User(String userName, String profileImage) {
        this.userName = userName;
        this.profileImage = profileImage;
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
                "profileImage: " + this.profileImage + "\n";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
