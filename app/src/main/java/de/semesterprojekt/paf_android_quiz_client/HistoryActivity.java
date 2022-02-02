package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.semesterprojekt.paf_android_quiz_client.adapter.EmptyAdapter;
import de.semesterprojekt.paf_android_quiz_client.model.PlayedGames;
import de.semesterprojekt.paf_android_quiz_client.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.adapter.PlayedGamesAdapter;
import de.semesterprojekt.paf_android_quiz_client.util.Helper;

/**
 * Controlls History View / Layout
 */
public class HistoryActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_history);

        getSesssionData();
        Helper.clearPicassoCache();
        initViews();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EmptyAdapter emptyAdapter = new EmptyAdapter();
        recyclerView.setAdapter(emptyAdapter);

        playedGamesArrayList = new ArrayList<>();

        getHistory();
    }
    /**
     * Displays Menu in the upper right corner in App-/Toolbar
     *
     * @param menu main menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sub_menu, menu);
        return true;
    }

    /**
     * HamburgerMenu Handler
     *
     * @param item menu item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.lobby) {
            goToLobby();
        }
        if (itemId == R.id.profile) {
            goToProfile();
        } else if (itemId == R.id.history) {
            goToHistory();
        } else if (itemId == R.id.highscore) {
            goToHighScores();
        } else if (itemId == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Gets History (/playedGames) from Rest-API and puts it into the recyclerview
     */
    private void getHistory() {

        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(HistoryActivity.this.getApplication());
        // get RestService instance and request playedGames from API
        restServiceSingleton.getPlayedGames(userToken, new RestServiceListener() {
            @Override
            public void onGetPlayedGames(JSONObject history) {
                super.onGetPlayedGames(history);
                JSONArray playedGames = null;
                try {
                    // if user hasn't played yes -> has no history yet
                    if(!history.isNull("playedGames")) {
                        playedGames = history.getJSONArray("playedGames");
                    }
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

                // parse JsonArray from Restservice-Response to PlayedGames-Objects and add them to playedGamesArrayList
                playedGamesArrayList = gson.fromJson(String.valueOf(playedGames), playedGamesListType);

                setViews();

                // pass playedGamesArrayList to PlayedGamesAdapter
                playedGamesAdapter = new PlayedGamesAdapter(HistoryActivity.this, playedGamesArrayList);
                // pass playedGamesAdapter to recyclerView
                recyclerView.setAdapter(playedGamesAdapter);
                playedGamesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSessionExpired() {
                super.onSessionExpired();
                Dialog dialog = Helper.getSessionExpiredDialog(HistoryActivity.this);
                dialog.show();
            }
        });
    }
    /**
     * assigning values from textViews and RecyclerView from the layout
     */
    private void initViews() {
        tv_historyAverage = findViewById(R.id.tv_history_average);
        tv_historyGamesCounter = findViewById(R.id.tv_history_played_games_counter);
        tv_historyDraw = findViewById(R.id.tv_history_draw);
        tv_historyLost = findViewById(R.id.tv_history_lost);
        tv_historyWon = findViewById(R.id.tv_history_won);

        recyclerView = findViewById(R.id.recyclerview_history);
    }
    /**
     * set values to views
     */
    private void setViews() {
        tv_historyGamesCounter.setText(gameCount);
        tv_historyAverage.setText(average);
        tv_historyDraw.setText(draw);
        tv_historyLost.setText(lost);
        tv_historyWon.setText(won);
    }
    // Get JWT userToken from session
    private void getSesssionData() {
        sessionManager = SessionManager.getSingletonInstance(getApplicationContext());
        // Get JWT userToken from session
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
    }

    // moves user to HighScore View
    private void goToHighScores() {
        Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
        startActivity(intent);
    }
    // moves user to Profle View
    private void goToProfile() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }
    // moves user to History view
    private void goToHistory() {
        startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
    }
    // moves User to Lobby View
    private void goToLobby() {
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        startActivity(intent);
    }

    /**
     * deletes jwt and user from storage, so that the user has to log in again to play
     */
    private void logout() {
        sessionManager.logout();
        Toast.makeText(getApplicationContext(), "Successfully logged out.", Toast.LENGTH_SHORT).show();
    }
}