package de.semesterprojekt.paf_android_quiz_client.restservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.semesterprojekt.paf_android_quiz_client.util.Helper;
import de.semesterprojekt.paf_android_quiz_client.config.ServerConfig;


/**
 * Sends requests to the REST Server API
 */
public class RestServiceClient {

    private final Context ctx;
    private final RequestQueue requestQueue;
    Gson gson = new Gson();

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

        JSONObject jsonObject = new JSONObject();

        // JSON Request
        try {
            jsonObject.put("userName", username);
            jsonObject.put("password", password);
            Response.Listener<JSONObject> successListener = response -> {
                try {
                    listener.onLogin(response.getString("token"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "JSON Exception oben: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (error instanceof AuthFailureError) {
                        listener.onAuthFailure(ctx);

                    } else if (error instanceof TimeoutError) {
                        listener.onTimeoutError(ctx);

                    } else if (error instanceof NoConnectionError) {
                        listener.onNoConnectionError(ctx);
                    } else {
                        listener.onError(ctx, error);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ServerConfig.LOGIN_API, jsonObject, successListener, errorListener);
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
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    if (error instanceof TimeoutError) {
                        listener.onTimeoutError(ctx);
                    } else if (error instanceof NoConnectionError) {
                        listener.onNoConnectionError(ctx);
                    } else {
                        listener.onError(ctx, error);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ServerConfig.REGISTER_API, jsonObject, successListener, errorListener);
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "JSON Exception unten: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Gets Highscores from RestAPI
     *
     * @param userToken userToken, user receivev when he logs in
     * @param listener  reacts on the gethighscore-response from the restAPI
     */
    public void getHighScores(String userToken, RestServiceListener listener) {

        Response.Listener<JSONArray> successListener = response -> {

            try {
                listener.onGetHighScores(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            if (error instanceof AuthFailureError) {
                listener.onSessionExpired();
            } else if (error instanceof TimeoutError) {
                listener.onTimeoutError(ctx);

            } else if (error instanceof NoConnectionError) {
                listener.onNoConnectionError(ctx);
            } else {
                listener.onError(ctx, error);
            }
            Log.d("GetRequest", error.toString());
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ServerConfig.HIGHSCORE_API, null, successListener, errorListener) {
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

    /**
     * Gets PlayedGames and statistic of played Games from RestAPI
     *
     * @param userToken userToken, user received when he logs in
     * @param listener  reacts on the gethistory-response from the restAPI
     */
    public void getPlayedGames(String userToken, RestServiceListener listener) {

        Response.Listener<JSONObject> successListener = response -> {
            try {
                listener.onGetPlayedGames(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            if (error instanceof AuthFailureError) {
                listener.onSessionExpired();
            } else if (error instanceof TimeoutError) {
                listener.onTimeoutError(ctx);

            } else if (error instanceof NoConnectionError) {
                listener.onNoConnectionError(ctx);
            } else if ((error instanceof ClientError) && (error.networkResponse.statusCode == 400)) {
                listener.onGetNoPlayedGames();
            }
            else {
                listener.onError(ctx, error);
            }
            Log.d("GetRequest", error.toString());
            error.printStackTrace();
        };
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ServerConfig.PLAYED_GAMES_API, null, successListener, errorListener) {

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

    public void uploadImage(String userToken, Bitmap bitmap, RestServiceListener listener) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ServerConfig.UPLOAD_FILE_API,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(ctx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof AuthFailureError) {
                            listener.onSessionExpired();
                        } else if (error instanceof TimeoutError) {
                            listener.onTimeoutError(ctx);

                        } else if (error instanceof NoConnectionError) {
                            listener.onNoConnectionError(ctx);
                        } else {
                            listener.onError(ctx, error);
                        }
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
                params.put("file", new DataPart(imagename + ".png", Helper.getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        requestQueue.add(volleyMultipartRequest);
    }

}