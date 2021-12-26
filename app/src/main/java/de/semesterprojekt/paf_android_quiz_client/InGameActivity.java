package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompHeader;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;


public class InGameActivity extends AppCompatActivity {

    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_getQuestion, btn_quitSession;
    TextView tv_question, tv_timer, tv_userScore, tv_opponentScore, tv_top_message_box, tv_waiting, tv_gameStartIn, tv_startCounter;
    ProgressBar prog_timer;
    LinearLayout layoutLobby;
    ConstraintLayout layoutInGame;


    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + "/websocket"));

    String userToken;
    Gson gson = new Gson();
    RestServiceSingleton restServiceSingleton;
    GameMessageObject gameMessageObject;
    SharedPreferences sharedPreferences;

    final static public int SECONDS_TO_SOLVE_QUESTION = 10;
    int secondsRemaining = SECONDS_TO_SOLVE_QUESTION;

    int questionCounter = 0;

    final static public int SECONDS_TILL_GAMESTART = 5;
    CountDownTimer solveQuestionTimer;
    CountDownTimer startGameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        initClassVars();
        initViews();
        addJwtToStompSocketHeader(userToken);
        connectToStompSocket();
        subscribeToStompTopic("/user/topic/game");
        setOnClickListeners();
    }

    /**
     * Get views from layout
     */
    protected void initViews() {
        // Buttons
        btn_answer1 = findViewById(R.id.btn_answer1);
        btn_answer2 = findViewById(R.id.btn_answer2);
        btn_answer3 = findViewById(R.id.btn_answer3);
        btn_answer4 = findViewById(R.id.btn_answer4);
        btn_getQuestion = findViewById(R.id.btn_getQuestion);
        btn_quitSession = findViewById(R.id.btn_quitSession);
        // TextViews
        tv_question = findViewById(R.id.tv_question);
        tv_timer = findViewById(R.id.tv_timer);
        tv_userScore = findViewById(R.id.tv_userScore);
        tv_opponentScore = findViewById(R.id.tv_opponentScore);
        tv_top_message_box = findViewById(R.id.tv_top_message_box);
        tv_waiting = findViewById(R.id.tv_waiting);
        tv_gameStartIn = findViewById(R.id.tv_gameStartIn);
        tv_startCounter = findViewById(R.id.tv_startCounter);
        // Progressbar
        prog_timer = findViewById(R.id.prog_timer);

        layoutLobby = findViewById(R.id.lo_lobby);
        layoutInGame = findViewById(R.id.lo_inGame);
    }

    /**
     * Init ClassVars
     */
    protected void initClassVars() {
        // Open SharedPref file
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // Get JWT userToken from storage
        userToken = sharedPreferences.getString("token", "");

        // Get RestServerSingleton-Instance
        restServiceSingleton = RestServiceSingleton.getInstance(getApplicationContext());
        // Get userToken from instance
        // userToken = RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken();

        solveQuestionTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                prog_timer.setProgress(SECONDS_TO_SOLVE_QUESTION - secondsRemaining);
                tv_timer.setText("Time left: " + Integer.toString(secondsRemaining) + " s"); //TODO: make Resource String for seconds
                secondsRemaining--;
            }

            @Override
            public void onFinish() {
                btn_answer1.setEnabled(false);
                btn_answer2.setEnabled(false);
                btn_answer3.setEnabled(false);
                btn_answer4.setEnabled(false);

                prog_timer.setProgress(SECONDS_TO_SOLVE_QUESTION);
                tv_timer.setText("Time is up!");
                //tv_top_message_box.setText("Question x/y - Time is up!"); //TODO: make Resource String for timeup message

                final Handler handler = new Handler();
                // Delay for getting next question after time is up
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestQuestion();
                        //TODO: set text from top_message box to question x/y
                        //tv_timer.setText("Question x/y");
                    }
                }, 2000);
            }
        };

        startGameTimer = new CountDownTimer(5000, 1000) {
            int startGameCountdown = SECONDS_TILL_GAMESTART;

            @Override
            public void onTick(long millisUntilFinished) {
                tv_startCounter.setText(Integer.toString(startGameCountdown));
                tv_waiting.setText("Player found.\nPlaying vs. " + gameMessageObject.getOpponent().getUsername());
                startGameCountdown--;
            }

            @Override
            public void onFinish() {
                layoutLobby.setVisibility(View.INVISIBLE);
                layoutInGame.setVisibility(View.VISIBLE);
            }
        };
    }

    /**
     * Connect to STOMP Websocket Server
     */
    protected void connectToStompSocket() {

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
    }

    /**
     * Add JWT Token to STOMP Header
     *
     * @param userToken JWT usertoken
     */
    protected void addJwtToStompSocketHeader(String userToken) {
        // add JWToken to websocket STOMP Header
        stompSocket.addHeader(StompHeader.TOKEN.toString(), userToken);
    }

    /**
     * Subscribe to a topic once STOMP connection is established
     *
     * @param destination topic destination
     */
    protected void subscribeToStompTopic(String destination) {
        stompSocket.subscribe(destination, stompFrame -> {
            // new Runnable on UiThread
            runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("Quiz", "Questioncounter: " + questionCounter);

                    // creates GameMessageObject from STOMP message respond
                    createGameMessageObject(stompFrame.getBody());
                    if (questionCounter == 0) {
                        //start counter for gamestart
                        tv_gameStartIn.setVisibility(View.VISIBLE);
                        tv_startCounter.setVisibility(View.VISIBLE);
                        startGameTimer.start();
                        questionCounter++;
                    }
                    if (questionCounter == 1) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // load GameMessageObject into UI
                                loadQuestion(gameMessageObject);
                                Log.d("Quiz", "gamemessage: " + stompFrame.getBody());
                                // reset time to solve question and start solveQuestionTimer
                                secondsRemaining = SECONDS_TO_SOLVE_QUESTION;
                                solveQuestionTimer.start();
                            }
                        }, 5000);
                    }
                    if (questionCounter > 1 && questionCounter <= 3) {
                        // if a timer is already running, cancel it
                        solveQuestionTimer.cancel();
                        // load GameMessageObject into UI
                        loadQuestion(gameMessageObject);
                        Log.d("Quiz", "gamemessage: " + stompFrame.getBody());
                        // reset time to solve question and start solveQuestionTimer
                        secondsRemaining = SECONDS_TO_SOLVE_QUESTION;

                        solveQuestionTimer.start();

                    }
                    if (questionCounter > 3) {
                        // wenn drei Fragen gesendet wurden, dann zeige Gewinner
                        Toast.makeText(
                                getApplicationContext(),
                                "Es wurden drei Fragen ausgespielt. Es wird die Endresultat angezeigt. Dann zurueck zum Startmenue",
                                Toast.LENGTH_LONG).show();

                        Log.d("Quiz:", "Es wurden drei Fragen ausgespielt. Es wird die " +
                                "Endresultat angezeigt. Dann zurueck zum Startmenue");
                    }
                }
            });
        });
        Log.d("Quiz", "Websocket channel subscribed.");
    }

    /**
     * Set onClickListener to Buttons
     */
    protected void setOnClickListeners() {
        // Answer Button Listener
        View.OnClickListener answerButtonClickListener = view -> {
            Button answerButton = (Button) view;
            // if a timer is already running, cancel it
            solveQuestionTimer.cancel();
            // calculate time the user needed to answer
            int timeNeeded = SECONDS_TO_SOLVE_QUESTION - secondsRemaining;
            // send selected answer and time to pick the answer to server
            sendAnswer(answerButton, timeNeeded);
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
    }

    /**
     * Create new GameMesssageObject from STOMP message response
     *
     * @param message GameMessageObject from Server
     */
    protected void createGameMessageObject(String message) {
        // Converts JSONObject String into GameMessageObject
        gameMessageObject = gson.fromJson(message, GameMessageObject.class);
        gameMessageObject.getUser().setToken(sharedPreferences.getString(getString(R.string.user_token), restServiceSingleton.getUser().getToken()));

        // Updates Login User Instance with userId, and readyStatus //TODO: missing userimage
        restServiceSingleton.setUser(gameMessageObject.getUser());
    }

    /**
     * Assign GameMessageObject to LayoutViews
     *
     * @param gameMessageObject GameMessageObject from Server
     */
    public void loadQuestion(GameMessageObject gameMessageObject) {
        questionCounter++;
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

        String txtFieldUserScore = sharedPreferences.getString("username", "") + " " + gameMessageObject.getUserScore() + "pts";
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
            // put text from selected button and time needed into JsonObject
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