package de.semesterprojekt.paf_android_quiz_client.model;

public interface RestServiceListener {
    void onLogin(User user);

    void onRegister(String username);
}
