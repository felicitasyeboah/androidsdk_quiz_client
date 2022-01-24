package de.semesterprojekt.paf_android_quiz_client.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import de.semesterprojekt.paf_android_quiz_client.MainActivity;
import de.semesterprojekt.paf_android_quiz_client.R;


public class SessionManager {
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;
    private final static String LOGGED_IN = "IsLoggedIn";


    public SessionManager(Context context) {
        this.context = context;
        this.userSession = this.context.getSharedPreferences(this.context.getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        this.editor = userSession.edit();
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
        //TODO: anfangen, dass wenn man eingeloggt ist und per pfeiltaste auf dem handy zurueckgeht
        // zu login, man direkt wiede rin der lobby landet
    }

    public void logout() {
        editor.clear();
        editor.commit();

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