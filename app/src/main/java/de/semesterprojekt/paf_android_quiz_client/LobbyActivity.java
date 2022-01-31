package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.semesterprojekt.paf_android_quiz_client.config.ServerConfig;


/**
 * Controls LobbyView / Layout
 */
public class LobbyActivity extends AppCompatActivity {

    TextView tv_username;
    LinearLayout ll_profile;
    ImageView iv_user_icon;
    Button btn_startGame, btn_history, btn_profile, btn_highScore;
    SessionManager sessionManager;
    String userToken;
    String userName;

    @Override
    protected void onStart() {
        super.onStart();
        sessionManager.checkLogin();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        // Get JWT userToken and Username from session
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
        userName = sessionManager.getUserDatafromSession().get(getString(R.string.username));
        setContentView(R.layout.activity_lobby);
        initViews();
        setViews();

        btn_startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(view);
            }
        });

        ll_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHistory();
            }
        });
        btn_highScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHighScores();
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });

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
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * @param item menu item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
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

    /**
     * assigning values from loyoutViews (Button, textVIew, ImageView)
     */
    protected void initViews() {
        tv_username = findViewById(R.id.tv_lobbyUsername);

        iv_user_icon = (ImageView) findViewById(R.id.iv_profil);

        ll_profile = findViewById(R.id.ll_profile);
        btn_history = findViewById(R.id.btn_history);
        btn_startGame = findViewById(R.id.btn_start_game);
        btn_highScore = findViewById(R.id.btn_highscore);
        btn_profile = findViewById(R.id.btn_profile);
    }

    /**
     * set values to views
     */
    protected void setViews() {
        tv_username.setText(userName);
        Picasso.get().load(ServerConfig.PROFILE_IMAGE_API + userName).fit().centerInside().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_user_icon);
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
    /**
     * Called when the user taps the Start Game button
     */
    private void startGame(View view) {
        Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
        startActivity(intent);
    }

    /**
     * deletes jwt and user from storage, so that the user has to log in again to play
     */
    private void logout() {
        sessionManager.logout();
        Toast.makeText(LobbyActivity.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
    }
}