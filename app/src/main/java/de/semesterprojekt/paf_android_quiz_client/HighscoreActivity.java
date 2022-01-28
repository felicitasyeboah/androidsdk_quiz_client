package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.semesterprojekt.paf_android_quiz_client.model.Highscore;
import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;

/**
 * Controlls Highscore View / Layout
 */
public class HighscoreActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String userToken;

    RecyclerView recyclerView;
    ArrayList<Highscore> highscoreArrayList;
    HighscoreAdapter highscoreAdapter;
    Gson gson = new Gson();

    @Override
    protected void onStart() {
        super.onStart();
        // checks if user is logged in -> moves him back to mainActivity if not!
        sessionManager.checkLogin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        getSesssionData();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        highscoreArrayList = new ArrayList<>();

        getHighScores();
    }

    /**
     * Gets Highscorelist from Rest-API and puts it into the recyclerview
     */
    private void getHighScores() {
        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(HighscoreActivity.this.getApplication());
        // get RestService instance and request highScores from API
        restServiceSingleton.getHighScores(userToken, new RestServiceListener() {
            @Override
            public void onGetHighScores(JSONArray highScores) {
                super.onGetHighScores(highScores);

                Type highScoreListType = new TypeToken<List<Highscore>>() {
                }.getType();
                // parse JsonArray from Restservice-Response to Highscore-Objects and add them to highscoreArrayList
                highscoreArrayList = gson.fromJson(String.valueOf(highScores), highScoreListType);
                // pass HighscoreArrayList to highscoreAdapter
                highscoreAdapter = new HighscoreAdapter(HighscoreActivity.this, highscoreArrayList);
                // set highscoreAdapter to recyclerView
                recyclerView.setAdapter(highscoreAdapter);
                highscoreAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getSesssionData() {
        sessionManager = new SessionManager(getApplicationContext());
        // Get JWT userToken from session
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
    }


}