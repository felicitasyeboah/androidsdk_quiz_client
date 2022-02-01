package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.semesterprojekt.paf_android_quiz_client.config.ServerConfig;
import de.semesterprojekt.paf_android_quiz_client.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.util.Helper;

public class ProfileActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String userToken;
    String won, lost, draw, average, gameCount;

    TextView tv_average, tv_gamesCounter, tv_won, tv_lost, tv_draw;

    ImageView iv_cover, iv_profile_image;
    FloatingActionButton fab_changeProfileImage;
    Button btn_history;

    Bitmap bitmap;

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
        String url = ServerConfig.PROFILE_IMAGE_API + sessionManager.getUserDatafromSession().get(getString(R.string.username));
        Helper.getPicassoInstance(getApplicationContext()).load(url).fit().centerInside().into(iv_profile_image);

        fab_changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileActivity.this)
                        .maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(10);
            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHistory();
            }
        });
        getStats();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Uri picUri = data.getData();
        if (requestCode == 10 && resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);

                iv_profile_image.setImageBitmap(bitmap);

                uploadImage(bitmap);
                // Clear Picasso Image Cache after uploading a new ProfileImage
                Helper.clearPicassoCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(Bitmap bitmap) {

        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(ProfileActivity.this.getApplication());
        // get RestService instance and request playedGames from API
        restServiceSingleton.uploadImage(userToken, bitmap, new RestServiceListener() {
            @Override
            public void onSessionExpired() {
                super.onSessionExpired();
                Dialog dialog = Helper.getSessionExpiredDialog(ProfileActivity.this);
                dialog.show();
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
        } else if (itemId == R.id.profile) {
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

    private void getStats() {
        final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(ProfileActivity.this.getApplication());
        // get RestService instance and request playedGames from API
        restServiceSingleton.getPlayedGames(userToken, new RestServiceListener() {
            @Override
            public void onGetPlayedGames(JSONObject history) {
                super.onGetPlayedGames(history);
                try {
                    won = "WON: " + history.getInt("wonGames");
                    lost = "LOST: " + history.getInt("lostGames");
                    draw = "DRAW: " + history.getInt("drawGames");
                    average = "Average Score: " + history.getInt("averageScore");
                    gameCount = "Games played: " + history.getInt("gameCount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tv_gamesCounter.setText(gameCount);
                tv_average.setText(average);
                tv_draw.setText(draw);
                tv_lost.setText(lost);
                tv_won.setText(won);

            }
        });
    }

    private void initViews() {
        iv_cover = findViewById(R.id.iv_profile_cover);
        iv_profile_image = findViewById(R.id.iv_profile_image);
        fab_changeProfileImage = findViewById(R.id.fab_change_profile);
        btn_history = findViewById(R.id.btn_profile_history);
        tv_average = findViewById(R.id.tv_profile_average);
        tv_gamesCounter = findViewById(R.id.tv_profile_played_games_counter);
        tv_draw = findViewById(R.id.tv_profile_draw);
        tv_lost = findViewById(R.id.tv_profile_lost);
        tv_won = findViewById(R.id.tv_profile_won);

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