package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.semesterprojekt.paf_android_quiz_client.model.PlayedGames;
import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;

public class ProfileActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String userToken;
    String won, lost, draw, average, gameCount;

    TextView tv_historyAverage, tv_historyGamesCounter, tv_historyWon, tv_historyLost,
    tv_historyDraw;

    RecyclerView recyclerView;
    ArrayList<PlayedGames> playedGamesArrayList;
    PlayedGamesAdapter playedGamesAdapter;
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
        setContentView(R.layout.activity_profile);
        getSesssionData();
        initViews();

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        playedGamesArrayList = new ArrayList<>();

        getHistory();
    }

    private void getHistory() {

        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(ProfileActivity.this.getApplication());
        // get RestService instance and request playedGames from API
        restServiceSingleton.getHistory(userToken, new RestServiceListener() {
            @Override
            public void onGetHistory(JSONObject history) {
                super.onGetHistory(history);
                JSONArray playedGames = null;
                try {
                    playedGames = history.getJSONArray("playedGames");
                    won = "WON: " + history.getInt("wonGames");
                    lost = "LOST: " + history.getInt("lostGames");
                    draw = "DRAW: " + history.getInt("drawGames");
                    average = "Average Score: " + history.getInt("averageScore");
                    gameCount = "Games played: " + history.getInt("gameCount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Type playedGamesListType = new TypeToken<List<PlayedGames>>() {
                }.getType();
                playedGamesArrayList = gson.fromJson(String.valueOf(playedGames), playedGamesListType);

                tv_historyGamesCounter.setText(gameCount);
                tv_historyAverage.setText(average);
                tv_historyDraw.setText(draw);
                tv_historyLost.setText(lost);
                tv_historyWon.setText(won);

                playedGamesAdapter = new PlayedGamesAdapter(ProfileActivity.this, playedGamesArrayList);

                recyclerView.setAdapter(playedGamesAdapter);
                playedGamesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initViews() {
        tv_historyAverage = findViewById(R.id.tv_profile_average);
        tv_historyGamesCounter = findViewById(R.id.tv_profile_played_games_counter);
        tv_historyDraw = findViewById(R.id.tv_profile_draw);
        tv_historyLost = findViewById(R.id.tv_profile_lost);
        tv_historyWon = findViewById(R.id.tv_profile_won);

        recyclerView = findViewById(R.id.recyclerview_profile);
    }

    private void getSesssionData() {
        sessionManager = new SessionManager(getApplicationContext());
        // Get JWT userToken from session
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
    }
}