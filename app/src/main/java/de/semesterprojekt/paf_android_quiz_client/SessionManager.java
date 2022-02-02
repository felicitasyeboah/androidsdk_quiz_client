package de.semesterprojekt.paf_android_quiz_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import de.semesterprojekt.paf_android_quiz_client.util.Helper;

/**
 * Singleton class to set up an single instance for the SessionManager.
 * It stores the userToken to validate the user for RestService and Websocket communication
 */
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

    /**
     * Returns the  SessionmanagerSingleton instance, if one already exists.
     * Otherwise it creates a new SessionManagerSinglton instance.
     * @param context ApplicationContext
     * @return SessionMaanger instance
     */
    public static synchronized SessionManager getSingletonInstance(Context context) {
        if(instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }
    // stores username and user jw-token into sharedPreferences
    public void createLoginSession(String userName, String userToken) {
        editor.putBoolean(LOGGED_IN, true);
        editor.putString(context.getString(R.string.username), userName);
        editor.putString(context.getString(R.string.user_token), userToken);

        editor.commit(); // for sync; oder editor.apply for async

    }
    // is user logged in?
    public boolean isLoggedIn() {
        return userSession.getBoolean(LOGGED_IN, false);
    }

    // checks if user is logged in, if not -> puts user back to mainactivity to log in
    public void checkLogin() {
        if (!isLoggedIn()) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    // logs the user out and clears username and token from the sharedPrefrences
    public void logout() {
        editor.clear();
        editor.commit();

        Helper.shutdownPicasso();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    // returns data from sharedPrerences ( username and usertoken)
    public HashMap<String, String> getUserDatafromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(context.getString(R.string.username), userSession.getString(context.getString(R.string.username), null));
        userData.put(context.getString(R.string.user_token), userSession.getString(context.getString(R.string.user_token), null));
        return userData;
    }
}