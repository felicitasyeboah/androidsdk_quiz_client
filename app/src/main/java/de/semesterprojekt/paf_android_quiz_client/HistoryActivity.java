package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import de.semesterprojekt.paf_android_quiz_client.model.PlayedGames;
import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;

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
        initViews();

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        } else if (itemId == R.id.quit) {
            //TODO: APP schließen!! Wenn nicht zu loesen, dann exit aus menue loeschen
            finish();
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHistory() {

        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(HistoryActivity.this.getApplication());
        // get RestService instance and request playedGames from API
        restServiceSingleton.getPlayedGames(userToken, new RestServiceListener() {
            @Override
            public void onGetPlayedGames(JSONObject history) {
                super.onGetPlayedGames(history);
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

                playedGamesAdapter = new PlayedGamesAdapter(HistoryActivity.this, playedGamesArrayList);
                playedGamesAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(playedGamesAdapter);
            }
        });
    }

    private void initViews() {
        tv_historyAverage = findViewById(R.id.tv_history_average);
        tv_historyGamesCounter = findViewById(R.id.tv_history_played_games_counter);
        tv_historyDraw = findViewById(R.id.tv_history_draw);
        tv_historyLost = findViewById(R.id.tv_history_lost);
        tv_historyWon = findViewById(R.id.tv_history_won);

        recyclerView = findViewById(R.id.recyclerview_history);
    }

    private void getSesssionData() {
        sessionManager = new SessionManager(getApplicationContext());
        // Get JWT userToken from session
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
    }
    /**
     * Called when the user taps the Highscore button
     */
    private void goToHighScores() {
        // move to highscoreview
        Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
        startActivity(intent);
    }

    private void goToProfile() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    private void goToHistory() {
        startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
    }
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