package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import de.semesterprojekt.paf_android_quiz_client.model.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.RestServiceSingleton;

public class HighscoreActivity extends AppCompatActivity {

    TextView tv_highscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        tv_highscore = findViewById(R.id.tv_getHighscore);

        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(HighscoreActivity.this.getApplication());

        restServiceSingleton.getHighscore(new RestServiceListener() {
            @Override
            public void onGetHighscore(JSONArray getHighscore) {
                super.onGetHighscore(getHighscore);
                if (getHighscore != null) {
                    tv_highscore.setText(getHighscore.toString());
                }
            }
        });
    }
}