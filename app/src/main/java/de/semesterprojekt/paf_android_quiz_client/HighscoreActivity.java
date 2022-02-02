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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.semesterprojekt.paf_android_quiz_client.adapter.EmptyAdapter;
import de.semesterprojekt.paf_android_quiz_client.model.Highscore;
import de.semesterprojekt.paf_android_quiz_client.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.adapter.HighscoreAdapter;
import de.semesterprojekt.paf_android_quiz_client.util.Helper;

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

        Helper.clearPicassoCache(); // because opponent also needs to see updated userimages
        recyclerView = findViewById(R.id.recyclerview_highscore);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EmptyAdapter emptyAdapter = new EmptyAdapter();
        recyclerView.setAdapter(emptyAdapter);

        highscoreArrayList = new ArrayList<>();

        getHighScores();
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
     * Gets Highscorelist from Rest-API and puts it into the recyclerview
     */
    private void getHighScores() {
        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(HighscoreActivity.this.getApplication());
        // get RestService instance and request highScores from API
        restServiceSingleton.getHighScores(userToken, new RestServiceListener() {
            @Override
            public void onGetHighScores(JSONArray highScores) {
                super.onGetHighScores(highScores);
                if(highScores != null) {

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
            }

            @Override
            public void onSessionExpired() {
                super.onSessionExpired();
                Dialog dialog = Helper.getSessionExpiredDialog(HighscoreActivity.this);
                dialog.show();
            }
        });
    }

    private void getSesssionData() {
        sessionManager = SessionManager.getSingletonInstance(getApplicationContext());
        // Get JWT userToken from session
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
    }

    // moves user to HighScore Voew
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