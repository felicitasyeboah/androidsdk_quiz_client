package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Objects;

import de.semesterprojekt.paf_android_quiz_client.model.game.dto.GameMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.MessageType;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.ResultMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.ScoreMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.TimerMessage;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompHeader;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;

public class InGameActivity extends AppCompatActivity {

    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_quitSession;
    TextView tv_question, tv_timer, tv_userScore, tv_opponentScore, tv_top_message_box, tv_loLobbyWaiting,
            tv_loLobbyGameStartIn, tv_loLobbyStartCounter, tv_loScoreUserName, tv_loScoreOpponentName,
            tv_loScoreUserPoints, tv_loScoreOpponentPoints, tv_loScoreNextQuestionTimer,
            tv_loResult;
    ProgressBar prog_timer;

    LinearLayout layoutLobbyView;
    ConstraintLayout layoutInGameView;
    TableLayout layoutScoreView;
    LinearLayout layoutResultView;

    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + ServerData.STOMP_ENDPOINT));

    RestServiceSingleton restServiceSingleton;
    SharedPreferences sharedPreferences;

    String userToken;
    Gson gson = new Gson();

    GameMessage gameMessage;
    ScoreMessage scoreMessage;
    ResultMessage resultMessage;
    TimerMessage questionTimer;
    TimerMessage startTimer;
    TimerMessage scoreTimer;

    final static public int SECONDS_TO_SOLVE_QUESTION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        initClassVars();
        initViews();
        addJwtToStompSocketHeader(userToken);
        connectToStompSocket();
        subscribeToStompTopic(ServerData.STOMP_TOPIC);
        setOnClickListeners();
    }

    /**
     * Get views from layouts
     */
    protected void initViews() {
        // Buttons
        btn_answer1 = findViewById(R.id.btn_answer1);
        btn_answer2 = findViewById(R.id.btn_answer2);
        btn_answer3 = findViewById(R.id.btn_answer3);
        btn_answer4 = findViewById(R.id.btn_answer4);
        btn_quitSession = findViewById(R.id.btn_quitSession);
        // TextViews
        // at InGameView
        tv_question = findViewById(R.id.tv_question);
        tv_timer = findViewById(R.id.tv_timer);
        tv_userScore = findViewById(R.id.tv_userScore);
        tv_opponentScore = findViewById(R.id.tv_opponentScore);
        tv_top_message_box = findViewById(R.id.tv_top_message_box);
        // at LobbyView
        tv_loLobbyWaiting = findViewById(R.id.tv_lo_lobby_waiting);
        tv_loLobbyGameStartIn = findViewById(R.id.tv_lo_lobby_gameStartIn);
        tv_loLobbyStartCounter = findViewById(R.id.tv_lo_lobby_startCounter);
        // at ScoreView
        tv_loScoreOpponentName = findViewById(R.id.tv_lo_score_opponentName);
        tv_loScoreUserName = findViewById(R.id.tv_lo_score_userName);
        tv_loScoreOpponentPoints = findViewById(R.id.tv_lo_score_opponentPoints);
        tv_loScoreUserPoints = findViewById(R.id.tv_lo_score_userPoints);
        tv_loScoreNextQuestionTimer = findViewById(R.id.tv_lo_score_nextQuestionCounter);
        // at ResultView
        tv_loResult = findViewById(R.id.tv_lo_result);
        // Progressbar
        prog_timer = findViewById(R.id.prog_timer);

        // Layouts
        layoutLobbyView = findViewById(R.id.lo_lobby);
        layoutInGameView = findViewById(R.id.lo_inGame);
        layoutScoreView = findViewById(R.id.lo_score);
        layoutResultView = findViewById(R.id.lo_result);
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
                @SuppressLint("SetTextI18n")
                public void run() {
                    String message = stompFrame.getBody();
                    MessageType messageType = MessageType.valueOf(stompFrame.getHeaders().get("type"));
                    //TODO: refactoring

                    //Client gamelogic
                    switch (Objects.requireNonNull(messageType)) {

                        case START_TIMER_MESSAGE:
                            Log.d("Quiz", "START_TIMER_MESSAGE erhalten.");

                            // creates TimerMessage from STOMP message respond
                            startTimer = getTimerMessageObject(message);

                            // Load StartTimerMessage into UI
                            initStartGameLayout();

                            // Handle StartGame UI
                            handleStartGameLayout();

                            break;

                        case GAME_MESSAGE:
                            Log.d("Quiz", "MESSAGE: GAMEMESSAGE ERHALTEN.");

                            // creates GameMessage from STOMP message respond
                            getGameMessageObject(message);

                            // load GameMessage into UI
                            initInGameLayout(gameMessage);

                            break;

                        case QUESTION_TIMER_MESSAGE:
                            Log.d("Quiz", "QUESTION_TIMER_MESSAGE erhalten");

                            // creates TimeMessage from STOMP message respond
                            questionTimer = getTimerMessageObject(message);

                            // handle InGame UI
                            handleInGameLayout();

                            break;

                        case SCORE_TIMER_MESSAGE:
                            Log.d("Quiz", messageType.toString());

                            // creates TimeMessage from STOMP message respond
                            scoreTimer = getTimerMessageObject(message);

                            // handle Score UI
                            handleScoreLayout();
                            break;

                        case SCORE_MESSAGE:
                            Log.d("Quiz", messageType.toString());

                            // creates GameMessage from STOMP message respond
                            scoreMessage = getScoreMessageObject(message);

                            // load ScoreMessage into UI
                            initScoreLayout();

                            break;

                        case RESULT_MESSAGE:
                            Log.d("Quiz", messageType.toString());
                            //{"isHighScore":false,
                            // "user":{"userName":"alf","profileImage":"default2.png"},
                            // "opponent":{"userName":"feli","profileImage":"default2.png"},
                            // "userScore":0,
                            // "opponentScore":0,
                            // "type":"RESULT_MESSAGE"}

                            // creates ResultMessage from STOMP message respond
                            resultMessage = getResultMessageObject(message);

                            // load ResultMessage into UI
                            initResultLayout();

                            break;
                    }
                    Log.d("Quiz", "BODY: " + message);

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
            // calculate time the user needed to answer
            int timeNeeded = SECONDS_TO_SOLVE_QUESTION - questionTimer.getTimeLeft();
            // send selected answer and time to pick the answer to server
            sendAnswer(answerButton, timeNeeded);
            //checkAnswer(gameMessage.getCorrectAnswer(), answerButton.getText().toString());
            layoutInGameView.setVisibility(View.INVISIBLE);
            layoutLobbyView.setVisibility(View.VISIBLE);
            tv_loLobbyGameStartIn.setVisibility(View.INVISIBLE);
            tv_loLobbyStartCounter.setVisibility(View.INVISIBLE);
            tv_loLobbyWaiting.setVisibility(View.VISIBLE);
            tv_loLobbyWaiting.setText("Wainting for Player 2 to answer.");
        };

        btn_answer1.setOnClickListener(answerButtonClickListener);
        btn_answer2.setOnClickListener(answerButtonClickListener);
        btn_answer3.setOnClickListener(answerButtonClickListener);
        btn_answer4.setOnClickListener(answerButtonClickListener);

        //TODO: Buttons entfernen
        //Button erstmal noch zum Testen drin, kommen später weg
        btn_quitSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InGameActivity.this, "Session closed: ", Toast.LENGTH_LONG).show();
                quitSession();
            }
        });
    }

    protected void initStartGameLayout() {
        tv_loLobbyGameStartIn.setVisibility(View.VISIBLE);
        tv_loLobbyStartCounter.setVisibility(View.VISIBLE);
        tv_loLobbyWaiting.setText("Player found.\nPlaying vs. ");//TODO OPPONENT MITSENDEN + gameMessage.getOpponent().getUsername());

    }

    protected void handleStartGameLayout() {
        tv_loLobbyStartCounter.setText(Integer.toString(startTimer.getTimeLeft()));
        if (startTimer.getTimeLeft() == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutLobbyView.setVisibility(View.INVISIBLE);
                    //layoutInGameView.setVisibility(View.VISIBLE);
                }
            }, 1000);
        }
    }

    /**
     * Assign GameMessage to LayoutViews
     *
     * @param gameMessage GameMessage from Server
     */
    public void initInGameLayout(GameMessage gameMessage) {
        layoutInGameView.setVisibility(View.VISIBLE);
        Log.d("Quiz", sharedPreferences.getString(getString(R.string.user_token), ""));
        Log.d("Quiz", gameMessage.getUser().toString());

        // Set Button and TextView values from gameMessage
        btn_answer1.setText(gameMessage.getAnswer1());
        btn_answer2.setText(gameMessage.getAnswer2());
        btn_answer3.setText(gameMessage.getAnswer3());
        btn_answer4.setText(gameMessage.getAnswer4());
        tv_question.setText(gameMessage.getQuestion());

        btn_answer1.setEnabled(true);
        btn_answer2.setEnabled(true);
        btn_answer3.setEnabled(true);
        btn_answer4.setEnabled(true);

        String txtFieldUserScore = sharedPreferences.getString("username", "") + " " + gameMessage.getUserScore() + "pts";
        String txtFieldOpponentScore = gameMessage.getOpponent().getUsername() + " " + gameMessage.getOpponentScore() + "pts";

        tv_userScore.setText(txtFieldUserScore);
        tv_opponentScore.setText(txtFieldOpponentScore);
    }

    protected void handleInGameLayout() {
        prog_timer.setProgress(SECONDS_TO_SOLVE_QUESTION - questionTimer.getTimeLeft());
        tv_timer.setText("Time left: " + Integer.toString(questionTimer.getTimeLeft()) + " s");
        if (questionTimer.getTimeLeft() == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btn_answer1.setEnabled(false);
                    btn_answer2.setEnabled(false);
                    btn_answer3.setEnabled(false);
                    btn_answer4.setEnabled(false);
                    prog_timer.setProgress(SECONDS_TO_SOLVE_QUESTION);
                    tv_timer.setText("Time is up!");
                    layoutInGameView.setVisibility(View.INVISIBLE);
                }
            }, 1000);
            //tv_top_message_box.setText("Question x/y - Time is up!"); //TODO: make Resource String for timeup message
        }
    }

    protected void initScoreLayout() {
        layoutScoreView.setVisibility(View.VISIBLE);
        tv_loLobbyWaiting.setVisibility(View.INVISIBLE);

        tv_loScoreUserName.setText(scoreMessage.getUser().getUsername() + ":");
        tv_loScoreOpponentName.setText(scoreMessage.getOpponent().getUsername() + ":");
        tv_loScoreOpponentPoints.setText("+" + Integer.toString(scoreMessage.getOpponentPoints()) + "pts");
        tv_loScoreUserPoints.setText("+" + Integer.toString(scoreMessage.getUserPoints()) + "pts");
    }

    protected void handleScoreLayout() {
        tv_loScoreNextQuestionTimer.setText(Integer.toString(scoreTimer.getTimeLeft()));
        if (scoreTimer.getTimeLeft() == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutScoreView.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }
    }

    protected void initResultLayout() {
        layoutResultView.setVisibility(View.VISIBLE);
        tv_loResult.setText(resultMessage.toString());
    }

    /**
     * Create new GameMesssageObject from STOMP message response
     *
     * @param message GameMessage from Server
     */
    protected void getGameMessageObject(String message) {
        // Converts JSONObject String into GameMessage
        gameMessage = gson.fromJson(message, GameMessage.class);
        gameMessage.getUser().setToken(sharedPreferences.getString(getString(R.string.user_token), ""));
        Log.d("Quiz", "GAMEMESSAGEOBJECT: " + gameMessage.toString());
        // Updates Login User Instance with userId, and readyStatus //TODO: missing userimage
        //restServiceSingleton.setUser(gameMessage.getUser());
    }

    protected ScoreMessage getScoreMessageObject(String message) {
        return gson.fromJson(message, ScoreMessage.class);
    }

    protected ResultMessage getResultMessageObject(String message) {
        return gson.fromJson(message, ResultMessage.class);
    }

    protected TimerMessage getTimerMessageObject(String message) {
        return gson.fromJson(message, TimerMessage.class);
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

/*    public void checkAnswer(int correctAnswer, String choosenAnswer) {
        int value = gameMessage.getAnswers().get(choosenAnswer);
        if (correctAnswer == value) {
            Log.d("Quiz", "Richtige anwort.");
            //TODO: Set Buttonfarbe to lightgreen
        }
        else {
            Log.d("Quiz", "Falsche Antwort.");
            //TODO: Set Buttoncolor from correctAnswer to green and buttoncolor from choosenAnswer to red
        }
    }*/

    //TODO: funktion nur zum testen drin, später entfernen
    public void quitSession() {
        //Disconnect
        stompSocket.close();
    }

    //TODO: layout für kleine handys anzeigen, responsiv machen etc...
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