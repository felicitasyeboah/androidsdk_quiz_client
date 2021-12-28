package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;


/**
 * Controls StartmenuView / Layout
 */
public class StartmenueActivity extends AppCompatActivity {

    TextView tv_username;
    ImageView iv_user_icon;
    Button btn_startGame, btn_getHighscore, btn_profil, btn_logout;
    SharedPreferences sharedPreferences;
    String userToken;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startmenue);

        // Open SharedPref file
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // Get JWT userToken from storage
        userToken = sharedPreferences.getString(getString(R.string.user_token), "");
        userName = sharedPreferences.getString(getString(R.string.username), "");

        initViews();
        setViews();

        btn_startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(view);
            }
        });
        btn_getHighscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHighscore(view);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(view);
            }
        });

    }
    //TODO: make Activity and LayoutView for Profil

    /**
     * assigning values from loyoutViews (Button, textVIew, ImageView)
     */
    protected void initViews() {
        tv_username = findViewById(R.id.tv_startmenuUsername);

        iv_user_icon = (ImageView) findViewById(R.id.iv_profil);
        iv_user_icon.setImageResource(R.drawable.ic_user_default);

        btn_startGame = findViewById(R.id.btn_start_game);
        btn_getHighscore = findViewById(R.id.btn_getHighscore);
        btn_logout = findViewById(R.id.btn_logout);
    }

    /**
     * set values to views
     */
    protected void setViews() {
        tv_username.setText(userName);
        iv_user_icon.setImageResource(R.drawable.ic_user_default);
    }

    /**
     * Called when the user taps the Highscore button
     *
     * @param view
     */
    public void getHighscore(View view) {
        Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user taps the Startmenu button
     */
    public void startGame(View view) {
        Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
        startActivity(intent);
    }

    /**
     * Logs user out from server
     */
    public void logout(View view) {
        //TODO: delete user from sharedPref, not from RestSeviceSinglton Instance
/*        RestServiceSingleton.getInstance(getApplicationContext()).getUser().setToken("");
        Log.i("Token", RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken());
        RestServiceSingleton.getInstance(getApplicationContext()).setUser(null);
        if (RestServiceSingleton.getInstance(getApplicationContext()).getUser() != null) {
            Log.i("nicht null", "User");
        }*/
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}