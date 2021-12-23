package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import de.semesterprojekt.paf_android_quiz_client.model.GameMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompFrame;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.listener.StompMessageListener;


public class InGameActivity extends AppCompatActivity {

    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_getQuestion, btn_quitSession;
    TextView tv_question, tv_timer, tv_userScore, tv_opponentScore;
    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + "/websocket"));
    String userToken;
    Gson gson = new Gson();
    RestServiceSingleton restServiceSingleton;
    GameMessageObject gameMessageObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        // assigning values to button and textView ID's on layout
        btn_answer1 = findViewById(R.id.btn_answer1);
        btn_answer2 = findViewById(R.id.btn_answer2);
        btn_answer3 = findViewById(R.id.btn_answer3);
        btn_answer4 = findViewById(R.id.btn_answer4);
        btn_getQuestion = findViewById(R.id.btn_getQuestion);
        btn_quitSession = findViewById(R.id.btn_quitSession);

        tv_question = findViewById(R.id.tv_question);
        tv_timer = findViewById(R.id.tv_timer);
        tv_userScore = findViewById(R.id.tv_userScore);
        tv_opponentScore = findViewById(R.id.tv_opponentScore);

        // Get RestServerSingleton-Instance
        restServiceSingleton = RestServiceSingleton.getInstance(getApplicationContext());
        // Get Usertoken from instance
        userToken = RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken();

        // Wait for a connection to establish
        boolean connected;
        stompSocket.addHeader("token", userToken);
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
        stompSocket.subscribe("/user/topic/game", userToken, new StompMessageListener() {
            @Override
            public void onMessage(StompFrame stompFrame) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loadQuestion(stompFrame.getBody());
                        System.out.println("gamemessage: " + stompFrame.getBody());
                    }
                });
            }
        });
        initGame();
        Log.d("Websocket", "Channel Subscribed.");

        // OnClick Listener
        btn_answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send answer and expired time to Server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("answer1", btn_answer1.getText().toString());
                    jsonObject.put("expired time", 7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("Json object: " + jsonObject);
                System.out.println("Json object to string: " + jsonObject.toString());
                sendAnswer(jsonObject.toString());
                Log.d("Websocket", "Sent answer.");

            }
        });
        btn_answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send answer and expired time to Server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("answer2", btn_answer2.getText().toString());
                    jsonObject.put("expired time", 7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("Json object: " + jsonObject);
                System.out.println("Json object to string: " + jsonObject.toString());
                sendAnswer(jsonObject.toString());
                Log.d("Websocket", "Sent answer.");

            }
        });
        btn_answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send answer and expired time to Server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("answer3", btn_answer3.getText().toString());
                    jsonObject.put("expired time", 7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("Json object: " + jsonObject);
                System.out.println("Json object to string: " + jsonObject.toString());
                sendAnswer(jsonObject.toString());
                Log.d("Websocket", "Sent answer.");

            }
        });
        btn_answer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send answer and expired time to Server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("answer4", btn_answer4.getText().toString());
                    jsonObject.put("expired time", 7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("Json object: " + jsonObject);
                System.out.println("Json object to string: " + jsonObject.toString());
                sendAnswer(jsonObject.toString());
                Log.d("Websocket", "Sent answer.");

            }
        });

        btn_getQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request question from server
                requestQuestion("Antwort", userToken);
                Log.d("Websocket", "Requested Question.");

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

    /**
     * Loads GameMessageObject into Layout
     *
     * @param message GameMessageObject from Server
     */
    public void loadQuestion(String message) {
        // Converts JSONObject String into GameMessageObject
        gameMessageObject = gson.fromJson(message, GameMessageObject.class);

        // Updates Login User Instance
        restServiceSingleton.setUser(gameMessageObject.getUser());

        // Set Button and TextView values from gameMessageObject
        btn_answer1.setText(gameMessageObject.getAnswer1());
        btn_answer2.setText(gameMessageObject.getAnswer2());
        btn_answer3.setText(gameMessageObject.getAnswer3());
        btn_answer4.setText(gameMessageObject.getAnswer4());

        tv_question.setText(gameMessageObject.getQuestion());
        String timer = "22s";
        tv_timer.setText(timer);
        String txtFieldUserScore = restServiceSingleton.getUser().getUsername() + " " + gameMessageObject.getUserScore() + "pts";
        tv_userScore.setText(txtFieldUserScore);
    }


    /**
     * Sends picked answer an expired time to server
     *
     * @param message
     */
    public void sendAnswer(String message) {
        stompSocket.send("/app/game", message);
    }

    // Iniit game by loading first Question, when view-load is completed
    public void initGame() {
        stompSocket.send("/app/game", null, null);
    }

/*    public void initGame(String message, String token) {
        stompSocket.send("/app/game", message, token, null);
    }*/

    public void requestQuestion(String message, String token) {
        stompSocket.send("/app/game", message, token, null);
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