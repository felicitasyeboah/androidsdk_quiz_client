package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import de.semesterprojekt.paf_android_quiz_client.model.GameMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompFrame;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompHeader;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.listener.StompMessageListener;


public class InGameActivity extends AppCompatActivity {

    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_getQuestion, btn_quitSession;
    TextView tv_question, tv_timer, tv_userScore, tv_opponentScore, tv_top_message_box;
    ProgressBar prog_timer;

    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + "/websocket"));

    String userToken;
    Gson gson = new Gson();
    RestServiceSingleton restServiceSingleton;
    GameMessageObject gameMessageObject;
    SharedPreferences sharedPreferences;

    final int secondsToSolveQuestion = 10;
    int secondsRemaining = secondsToSolveQuestion;

    CountDownTimer solveQuestionTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_timer.setText(Integer.toString(secondsRemaining) + "sec"); //TODO: make Resource String for seconds
            secondsRemaining--;

            prog_timer.setProgress(secondsToSolveQuestion - secondsRemaining);
        }

        @Override
        public void onFinish() {
            btn_answer1.setEnabled(false);
            btn_answer2.setEnabled(false);
            btn_answer3.setEnabled(false);
            btn_answer4.setEnabled(false);

            tv_timer.setText("0sec");
            tv_top_message_box.setText("Question X/Y - Time is up!"); //TODO: make Resource String for timeup message

            final Handler handler = new Handler();

            // Delay for getting next question after time is up
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQuestion();
                    //TODO: set text from top_message box to question x/y
                }
            }, 4000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        // Open SharedPref file
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);

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
        tv_top_message_box = findViewById(R.id.tv_top_message_box);

        prog_timer = findViewById(R.id.prog_timer);

        // Get RestServerSingleton-Instance
        restServiceSingleton = RestServiceSingleton.getInstance(getApplicationContext());
        // Get userToken from instance
        userToken = RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken();


        // Wait for a connection to establish
        boolean connected;
        stompSocket.addHeader(StompHeader.TOKEN.toString(), userToken);
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
        stompSocket.subscribe("/user/topic/game", new StompMessageListener() {
            @Override
            public void onMessage(StompFrame stompFrame) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        solveQuestionTimer.cancel();
                        loadQuestion(stompFrame.getBody());
                        Log.d("Quiz", "gamemessage: " + stompFrame.getBody());
                        secondsRemaining = secondsToSolveQuestion;
                        solveQuestionTimer.start();
                    }

                });
            }
        });
        // Load first question
        requestQuestion();
        Log.d("Quiz", "Websocket channel subscribed.");

        // OnClick Listeners
        View.OnClickListener answerButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button answerButton = (Button) v;
                solveQuestionTimer.cancel();
                int timeNeeded = secondsToSolveQuestion - secondsRemaining;
                sendAnswer(answerButton, timeNeeded);
            }
        };
        btn_answer1.setOnClickListener(answerButtonClickListener);
        btn_answer2.setOnClickListener(answerButtonClickListener);
        btn_answer3.setOnClickListener(answerButtonClickListener);
        btn_answer4.setOnClickListener(answerButtonClickListener);

        //TODO: Buttons entfernen
        //Die Button erstmal noch zum Testen drin, kommen später weg
        btn_getQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request question from server
                requestQuestion();
                Log.d("Quiz", "Requested Question.");
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
     * Assign GameMessageObject to Layout
     *
     * @param message GameMessageObject from Server
     */
    public void loadQuestion(String message) {
        // Converts JSONObject String into GameMessageObject
        gameMessageObject = gson.fromJson(message, GameMessageObject.class);
        gameMessageObject.getUser().setToken(sharedPreferences.getString(getString(R.string.user_token), restServiceSingleton.getUser().getToken()));

        // next line can be deleted, if right userobject is send within the gamemessageobject
        gameMessageObject.getUser().setUsername(sharedPreferences.getString(getString(R.string.username), restServiceSingleton.getUser().getUsername()));

        // Updates Login User Instance with userId, and readyStatus //TODO: missing userimage
        restServiceSingleton.setUser(gameMessageObject.getUser());

        Log.d("Quiz", restServiceSingleton.getUser().getToken());
        Log.d("Quiz", restServiceSingleton.getUser().toString());

        // Set Button and TextView values from gameMessageObject
        btn_answer1.setText(gameMessageObject.getAnswer1());
        btn_answer2.setText(gameMessageObject.getAnswer2());
        btn_answer3.setText(gameMessageObject.getAnswer3());
        btn_answer4.setText(gameMessageObject.getAnswer4());
        tv_question.setText(gameMessageObject.getQuestion());

        btn_answer1.setEnabled(true);
        btn_answer2.setEnabled(true);
        btn_answer3.setEnabled(true);
        btn_answer4.setEnabled(true);

        String txtFieldUserScore = restServiceSingleton.getUser().getUsername() + " " + gameMessageObject.getUserScore() + "pts";
        String txtFieldOpponentScore = gameMessageObject.getOpponent().getUsername() + " " + gameMessageObject.getOpponentScore() + "pts";

        tv_userScore.setText(txtFieldUserScore);
        tv_opponentScore.setText(txtFieldOpponentScore);
    }

    /**
     * Send selected answer and time needed to server
     *
     * @param button selected button
     * @param timer  needed time for selecting an answer
     */
    public void sendAnswer(Button button, int timer) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("answer", button.getText().toString());
            jsonObject.put("time needed", timer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message = jsonObject.toString();
        stompSocket.send("/app/game", message);
        Log.d("Quiz", "Sent answer: " + jsonObject.toString());

    }

    /**
     * Request question from server
     */
    public void requestQuestion() {
        stompSocket.send("/app/game", null, null);
    }


    //TODO: funktionen noch entfernen
    // Erstmal nur zum Testen drin
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