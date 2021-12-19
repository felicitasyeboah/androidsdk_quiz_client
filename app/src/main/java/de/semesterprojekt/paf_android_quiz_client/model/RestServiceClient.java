package de.semesterprojekt.paf_android_quiz_client.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Sends requests to the REST Server API
 */
public class RestServiceClient {

    // Base URL to Rest API Server
    public static final String BASE_URL = "http://192.168.77.106:8080/";
    private final Context ctx;
    private final RequestQueue requestQueue;

    //Constructor
    public RestServiceClient(Context context) {
        this.ctx = context;
        this.requestQueue = Volley.newRequestQueue(this.ctx);
    }

    /**
     * Logs in the User
     *
     * @param username UserName
     * @param password UserPassword
     * @param listener reacts on loginresponse from the restAPI
     */
    public void login(String username, String password, RestServiceListener listener) {
        String url = BASE_URL + "auth/login";

        JSONObject jsonObject = new JSONObject();

        // JSON Request
        try {
            jsonObject.put("userName", username);
            jsonObject.put("password", password);
            Response.Listener<JSONObject> successListener = response -> {
                try {
                    response.put("userName", username);
                    User user = User.getUser(response);
                    listener.onLogin(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "JSON Exception oben: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ctx, "error response : " + error, Toast.LENGTH_SHORT).show();
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, successListener, errorListener);
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "JSON Exception unten: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Registers a new User
     *
     * @param username username user typed in
     * @param password password user typed in
     * @param listener reacts on the registerresponse from the restAPI
     */
    public void register(String username, String password, RestServiceListener listener) {

        String url = BASE_URL + "auth/register";
        JSONObject jsonObject = new JSONObject();

        // JSON Request
        try {
            // set given Username and password into the JsonObject
            jsonObject.put("userName", username);
            jsonObject.put("password", password);

            Response.Listener<JSONObject> successListener = response -> {
                try {

                    listener.onRegister(response.getString("userName"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "JSON Exception oben: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            Response.ErrorListener errorListener = error -> Toast.makeText(ctx, "error response : " + error, Toast.LENGTH_SHORT).show();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, successListener, errorListener);
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "JSON Exception unten: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void getHighscore(String userToken, RestServiceListener listener) {
        String url = BASE_URL + "highscoreGlobal";

        Response.Listener<JSONArray> successListener = response -> {

            listener.onGetHighscore(response);

        };

        Response.ErrorListener errorListener = error -> Log.i("GetRequest", error.toString());//Toast.makeText(ctx, "error response : " + error, Toast.LENGTH_LONG).show();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + userToken);
                return headers;
            }
        };

        requestQueue.add(request);


    }
}
