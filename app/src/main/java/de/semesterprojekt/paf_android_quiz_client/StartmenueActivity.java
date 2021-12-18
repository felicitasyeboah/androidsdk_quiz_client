package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.semesterprojekt.paf_android_quiz_client.model.RestServiceSingleton;

public class StartmenueActivity extends AppCompatActivity {

    TextView tv_username;
    ImageView iv_user_icon;
    Button btn_startGame, btn_getHighscore, btn_profil, btn_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startmenue);
        tv_username = findViewById(R.id.tv_startmenuUsername);
        tv_username.setText(RestServiceSingleton.getInstance(getApplicationContext()).getUser().getUsername());
        iv_user_icon = (ImageView) findViewById(R.id.iv_profil);
        iv_user_icon.setImageResource(R.drawable.ic_user_default);

        btn_startGame = findViewById(R.id.btn_start_game);

        btn_startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(view);
            }
        });
    }

    /**
     * Called when the user taps the Startmenu button
     */
    public void startGame(View view) {
        Intent intent = new Intent(getApplicationContext(), InGameActivity.class);
        startActivity(intent);

    }
}