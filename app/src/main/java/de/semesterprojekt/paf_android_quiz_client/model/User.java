package de.semesterprojekt.paf_android_quiz_client.model;

/**
 * represents a simple user with name
 */
public class User {

    private String userName;


    //Contructor
    public User(String userName) {
        this.userName = userName;
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
        return "userName: " + this.userName + "\n";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
