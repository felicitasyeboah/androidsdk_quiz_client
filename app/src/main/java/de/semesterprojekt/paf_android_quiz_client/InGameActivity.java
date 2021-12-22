package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.semesterprojekt.paf_android_quiz_client.model.GameMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.User;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompFrame;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.listener.StompMessageListener;


public class InGameActivity extends AppCompatActivity {

    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_getQuestion, btn_quitSession;
    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + "/websocket"));
    JSONObject jsonObject;
    String userToken;
    GameMessageObject gameMessageObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        btn_answer1 = findViewById(R.id.btn_answer1);
        btn_answer2 = findViewById(R.id.btn_answer2);
        btn_answer3 = findViewById(R.id.btn_answer3);
        btn_answer4 = findViewById(R.id.btn_answer4);
        btn_getQuestion = findViewById(R.id.btn_getQuestion);
        btn_quitSession = findViewById(R.id.btn_quitSession);
        userToken = RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken();

        //GameMessageObject gameMessageObjekct = Js
        //Gson g = new Gson();
        //GameMessageObject gameMessageObject = gameMessageObject.fromJson(jsonString, GameMessageObject.class)
        //JSONParser parser = new JSONParser();
        //{"category":"Essen & Trinken","question":"Aus welchem Land kommt der Gouda?","answer":["Ghana","Niederlande","Frankreich","Luxemburg"],"userScore":0,"opponentScore":0,"user":{"userId":0,"userName":"Bernd","profileImage":null,"ready":false},"opponent":{"userId":0,"userName":"Beate","profileImage":null,"ready":false}}

        // Wait for a connection to establish
        boolean connected;
        try {
            connected = stompSocket.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        if (!connected) {
            System.out.println("Failed to connect to the socket");
            return;
        }
        // Subscribing to a topic once STOMP connection is established
        stompSocket.subscribe("/topic/game", userToken, new StompMessageListener() {
            @Override
            public void onMessage(StompFrame stompFrame) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showQuestion(stompFrame.getBody());

                        try {
                            jsonObject = new JSONObject(stompFrame.getBody());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Gameobject: " + jsonObject);
                        try {
                            btn_answer1.setText(jsonObject.get("category").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        requestQuestion("isReady");
        Log.d("Websocket", "Channel Subscribed.");
        System.out.println("usertoken: " + userToken);

        btn_getQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request question from server
                requestQuestion("Antwort", userToken);
                Log.d("Websocket", "Requested Question.");
                System.out.println("usertoken: " + userToken);

            }
        });

        btn_quitSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InGameActivity.this, "Session closed: ", Toast.LENGTH_LONG).show();
                quitSession();
            }
        });


        // Sending JSON message to a server
        //String message = "{\"name\": \"Jack\"}";
        //stompSocket.send("/app/game", message);

    }

    public void showQuestion(String message) {
        Toast.makeText(InGameActivity.this, "Server message: " + message, Toast.LENGTH_LONG).show();
    }


    public void requestQuestion(String message) {
        stompSocket.send("/app/game", message);
    }

    public void requestQuestion() {
        stompSocket.send("/app/game", null);
    }

    public void requestQuestion(String message, String headers) {
        stompSocket.send("/app/game", message, headers, null);
    }

    public void quitSession() {
        //Disconnect
        stompSocket.close();
    }

        /*webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.addHeader("Connection", "Upgrade");
        webSocketClient.addHeader("Host", "http://192.168.77.106:8080");
        webSocketClient.addHeader("Origin", "http://192.168.77.106:8080");
        webSocketClient.addHeader("Sec-WebSocket-Version", "13");
        //webSocketClient.addHeader("Content-Type", "application/json");
        //webSocketClient.addHeader("Accept", "application/json");
        webSocketClient.addHeader("Upgrade", "websocket");
        webSocketClient.addHeader("Authorization", "Bearer " + RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken());
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }*/
}