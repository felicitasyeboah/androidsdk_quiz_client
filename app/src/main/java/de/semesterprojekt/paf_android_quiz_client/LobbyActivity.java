package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;


/**
 * Controls LobbyView / Layout
 */
public class LobbyActivity extends AppCompatActivity {

    TextView tv_username;
    LinearLayout ll_profile;
    ImageView iv_user_icon;
    Button btn_startGame;
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
        Log.d("Quiz", "Username: "+ userName +  " Usertoken: " + userToken);
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
                getProfile();
            }
        });

    }
    //TODO: make Activity and LayoutView for Profil

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
            getProfile();
        } else if (itemId == R.id.highscore) {
            getHighscore();
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
        btn_startGame = findViewById(R.id.btn_start_game);
    }

    /**
     * set values to views
     */
    protected void setViews() {
        tv_username.setText(userName);
        iv_user_icon.setImageResource(R.drawable.ic_profile_w);
    }

    /**
     * Called when the user taps the Highscore button
     */
    public void getHighscore() {
        Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
        startActivity(intent);
    }

    public void getProfile() {
        Toast.makeText(LobbyActivity.this, "Clicked on Profile", Toast.LENGTH_LONG).show();
        //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    /**
     * Called when the user taps the Start Game button
     */
    public void startGame(View view) {
        Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
        startActivity(intent);
    }

    /**
     * deletes jwt and user from storage, so tha the user has to log in again to play
     */
    public void logout() {
        sessionManager.logout();
        Toast.makeText(LobbyActivity.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
    }
}