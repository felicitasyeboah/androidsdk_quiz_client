package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ReceiverCallNotAllowedException;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import de.semesterprojekt.paf_android_quiz_client.model.Highscore;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;

public class HighscoreActivity extends AppCompatActivity {

    TextView tv_highscore;

    RecyclerView recyclerView;
    ArrayList<Highscore> highscoreArrayList;
    HighscoreAdapter highscoreAdapter;
    String[] highscoreDate;
    int[] imageResourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        highscoreArrayList = new ArrayList<Highscore>();

        highscoreAdapter = new HighscoreAdapter(this, highscoreArrayList);
        recyclerView.setAdapter(highscoreAdapter);

        highscoreDate = new String[] {
                "w459384035",
                "43564564",
                "345345"
        };

        imageResourceId = new int[] {
                R.drawable.ic_logo,
                R.drawable.ic_logout,
                R.drawable.ic_exit,


        };

        getData();

        tv_highscore = findViewById(R.id.tv_hs_test);

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

    private void getData() {
        for(int i = 0; i<highscoreDate.length; i++) {
            Highscore highscore = new Highscore(highscoreDate[i], imageResourceId[i]);
            highscoreArrayList.add(highscore);
        }

        highscoreAdapter.notifyDataSetChanged();
    }
    //TODO: Make Layout for HighscoreView
}