package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.VolleyMultipartRequest;

public class ProfileActivity extends AppCompatActivity {

    SessionManager sessionManager;
    String userToken;
    String won, lost, draw, average, gameCount;

    TextView tv_average, tv_gamesCounter, tv_won, tv_lost, tv_draw;

    ImageView iv_cover, iv_profile_image;
    FloatingActionButton fab_changeProfileImage;
    Button btn_history;

    // ###
    private final static String ROOT_URL = "http://192.168.77.106:8080/upload";
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
        String url = ServerData.PROFILE_IMAGE_API + sessionManager.getUserDatafromSession().get(getString(R.string.username));
        Picasso.get().load(url).fit().centerInside().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE).into(iv_profile_image);

        fab_changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileActivity.this)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);//(Bitmap) data.getExtras().get("data");

                iv_profile_image.setImageBitmap(bitmap);
                uploadImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + userToken);
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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