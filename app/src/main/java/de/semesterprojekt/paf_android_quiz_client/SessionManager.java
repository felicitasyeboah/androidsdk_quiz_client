package de.semesterprojekt.paf_android_quiz_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import de.semesterprojekt.paf_android_quiz_client.util.Helper;

public class SessionManager {
    private static SessionManager instance;
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;
    private final static String LOGGED_IN = "IsLoggedIn";


    public SessionManager(Context context) {
        this.context = context;
        this.userSession = this.context.getSharedPreferences(this.context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        this.editor = userSession.edit();
    }

    public static synchronized SessionManager getSingletonInstance(Context context) {
        if(instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createLoginSession(String userName, String userToken) {
        editor.putBoolean(LOGGED_IN, true);
        editor.putString(context.getString(R.string.username), userName);
        editor.putString(context.getString(R.string.user_token), userToken);

        editor.commit(); // for sync; oder editor.apply for async

    }

    public boolean isLoggedIn() {
        return userSession.getBoolean(LOGGED_IN, false);
    }

    public void checkLogin() {
        if (!isLoggedIn()) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void logout() {
        editor.clear();
        editor.commit();

        Helper.shutdownPicasso();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public HashMap<String, String> getUserDatafromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(context.getString(R.string.username), userSession.getString(context.getString(R.string.username), null));
        userData.put(context.getString(R.string.user_token), userSession.getString(context.getString(R.string.user_token), null));
        return userData;
    }
}