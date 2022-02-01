package de.semesterprojekt.paf_android_quiz_client.restservice;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RestServiceListener {


    public void onLogin(String userToken) {

    }

    public void onRegister(String username) {

    }
    public void onSessionExpired() {
    }

    public void onGetHighScores(JSONArray highScores) {
    }

    public void onGetPlayedGames(JSONObject history) {
    }
    public void onGetNoPlayedGames() {

    }
    public void onAuthFailure(Context ctx) {
        Toast toast = Toast.makeText(ctx, "Wrong username or password!" + "\n" + "Try again.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 140);
        toast.show();
    }
    public void onTimeoutError(Context ctx){
        Toast toast = Toast.makeText(ctx, "Server not responding" + "\n" + "Try again later.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 140);
        toast.show();
    }
    public void onNoConnectionError (Context ctx) {
        Toast toast = Toast.makeText(ctx, "No Internet Connection" + "\n" + "Check your Internetconnection.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 140);
        toast.show();
    }
    public void onError(Context ctx, VolleyError error) {
        Toast toast = Toast.makeText(ctx, "Error: " + error.toString(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 140);
        toast.show();
    }
}
