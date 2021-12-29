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

import de.semesterprojekt.paf_android_quiz_client.model.GameMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.ResultMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.ScoreMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.TimerMessageObject;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompHeader;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;

public class InGameActivity extends AppCompatActivity {

    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_getQuestion, btn_quitSession;
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
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + "/websocket"));

    RestServiceSingleton restServiceSingleton;
    SharedPreferences sharedPreferences;

    String userToken;
    Gson gson = new Gson();

    GameMessageObject gameMessageObject;
    TimerMessageObject gameTimerMessageObject;
    TimerMessageObject startTimerMessageObject;
    TimerMessageObject scoreTimerMessageObject;

    final static public int SECONDS_TO_SOLVE_QUESTION = 10;
    int questionCounter = 0;

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
     * Get views from layouts
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
        // Get userToken from instance
        // userToken = RestServiceSingleton.getInstance(getApplicationContext()).getUser().getToken();
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
    //TODO: komplette gamelogic refactorn
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
                    String messageType = stompFrame.getHeaders().get("type");

                    //TODO: refactoring
                    //Client gamelogic
                    switch (Objects.requireNonNull(messageType)) {
                        case "START_TIMER_MESSAGE":
                            Log.d("Quiz", "START_TIMER_MESAGE erhalten.");
                            tv_loLobbyGameStartIn.setVisibility(View.VISIBLE);
                            tv_loLobbyStartCounter.setVisibility(View.VISIBLE);
                            startTimerMessageObject = getTimerMessageObject(message);
                            tv_loLobbyStartCounter.setText(Integer.toString(startTimerMessageObject.getTimeLeft()));
                            tv_loLobbyWaiting.setText("Player found.\nPlaying vs. ");//TODO OPPONENT MITSENDEN + gameMessageObject.getOpponent().getUsername());
                            if (startTimerMessageObject.getTimeLeft() == 1) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutLobbyView.setVisibility(View.INVISIBLE);
                                        layoutInGameView.setVisibility(View.VISIBLE);
                                    }
                                }, 1000);
                            }
                            break;
                        case "QUESTION_TIMER_MESSAGE":
                            Log.d("Quiz", "QUESTION_TIMER_MESSAGE erhalten");
                            gameTimerMessageObject = getTimerMessageObject(message);
                            prog_timer.setProgress(SECONDS_TO_SOLVE_QUESTION - gameTimerMessageObject.getTimeLeft());
                            tv_timer.setText("Time left: " + Integer.toString(gameTimerMessageObject.getTimeLeft()) + " s");
                            if (gameTimerMessageObject.getTimeLeft() == 1) {
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
                            break;
                        case "SCORE_TIMER_MESSAGE":
                            Log.d("Quiz", messageType);
                            scoreTimerMessageObject = getTimerMessageObject(message);
                            tv_loScoreNextQuestionTimer.setText(Integer.toString(scoreTimerMessageObject.getTimeLeft()));
                            if (scoreTimerMessageObject.getTimeLeft() == 1) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutScoreView.setVisibility(View.INVISIBLE);
                                        layoutInGameView.setVisibility(View.VISIBLE);
                                    }
                                }, 1000);
                            }
                            break;
                        case "GAME_MESSAGE":
                            Log.d("Quiz", "MESSAGE: GAMEMESSAGE ERHALTEN.");
                            // creates GameMessageObject from STOMP message respond
                            getGameMessageObject(message);
                            // load GameMessageObject into UI
                            loadQuestion(gameMessageObject);
                            break;
                        case "SCORE_MESSAGE":
                            layoutScoreView.setVisibility(View.VISIBLE);
                            tv_loLobbyWaiting.setVisibility(View.INVISIBLE);
                            Log.d("Quiz", messageType);
                            //"user":{"userName":"alf","profileImage":"default2.png"},
                            // "opponent":{"userName":"feli","profileImage":"default2.png"},
                            // "userScore":945,
                            // "opponentScore":0,
                            // "type":"SCORE_MESSAGE"}
                            ScoreMessageObject scoreMessageObject = getScoreMessageObject(message);
                            tv_loScoreUserName.setText(scoreMessageObject.getUser().getUsername() + ":");
                            tv_loScoreOpponentName.setText(scoreMessageObject.getOpponent().getUsername() + ":");
                            tv_loScoreOpponentPoints.setText("+" + Integer.toString(scoreMessageObject.getOpponentPoints()) + "pts");
                            tv_loScoreUserPoints.setText("+" + Integer.toString(scoreMessageObject.getUserPoints()) + "pts");

                            break;
                        case "RESULT_MESSAGE":
                            layoutInGameView.setVisibility(View.INVISIBLE);
                            layoutResultView.setVisibility(View.VISIBLE);
                            tv_loLobbyWaiting.setVisibility(View.INVISIBLE);
                            Log.d("Quiz", messageType);
                            //{"isHighScore":false,
                            // "user":{"userName":"alf","profileImage":"default2.png"},
                            // "opponent":{"userName":"feli","profileImage":"default2.png"},
                            // "userScore":0,
                            // "opponentScore":0,
                            // "type":"RESULT_MESSAGE"}
                            ResultMessageObject resultMessageObject = getResultMessageObject(message);
                            tv_loResult.setText(resultMessageObject.toString());

                            break;
                    }
                    Log.d("Quiz", "BODY: " + message);

                }
            });
        });
        Log.d("Quiz", "Websocket channel subscribed.");
    }

    //TODO: refacoring
/*    protected void resetViews(String messageType) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(messageType.equals(StompMessageType.START_TIMER_MESSAGE.toString())) {
                    break;
                }
            }
        })

    }*/

    /**
     * Set onClickListener to Buttons
     */
    protected void setOnClickListeners() {
        // Answer Button Listener
        View.OnClickListener answerButtonClickListener = view -> {
            Button answerButton = (Button) view;
            // calculate time the user needed to answer
            int timeNeeded = SECONDS_TO_SOLVE_QUESTION - gameTimerMessageObject.getTimeLeft();
            // send selected answer and time to pick the answer to server
            sendAnswer(answerButton, timeNeeded);
            //checkAnswer(gameMessageObject.getCorrectAnswer(), answerButton.getText().toString());
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

    protected void initInGameLayout() {
        layoutInGameView.setVisibility(View.VISIBLE);
    }

    protected void initScoreLayout() {
        layoutInGameView.setVisibility(View.VISIBLE);
    }

    protected void initResultLayout() {
        layoutInGameView.setVisibility(View.VISIBLE);
    }

    /**
     * Create new GameMesssageObject from STOMP message response
     *
     * @param message GameMessageObject from Server
     */
    protected void getGameMessageObject(String message) {
        // Converts JSONObject String into GameMessageObject
        gameMessageObject = gson.fromJson(message, GameMessageObject.class);
        gameMessageObject.getUser().setToken(sharedPreferences.getString(getString(R.string.user_token), ""));
        Log.d("Quiz", "GAMEMESSAGEOBJECT: " + gameMessageObject.toString());
        // Updates Login User Instance with userId, and readyStatus //TODO: missing userimage
        //restServiceSingleton.setUser(gameMessageObject.getUser());
    }

    protected ScoreMessageObject getScoreMessageObject(String message) {
        return gson.fromJson(message, ScoreMessageObject.class);
    }

    protected ResultMessageObject getResultMessageObject(String message) {
        return gson.fromJson(message, ResultMessageObject.class);
    }

    protected TimerMessageObject getTimerMessageObject(String message) {
        return gson.fromJson(message, TimerMessageObject.class);
    }


    /**
     * Assign GameMessageObject to LayoutViews
     *
     * @param gameMessageObject GameMessageObject from Server
     */
    public void loadQuestion(GameMessageObject gameMessageObject) {
        questionCounter++;
        Log.d("Quiz", sharedPreferences.getString(getString(R.string.user_token), ""));
        Log.d("Quiz", gameMessageObject.getUser().toString());

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

/*    public void checkAnswer(int correctAnswer, String choosenAnswer) {
        int value = gameMessageObject.getAnswers().get(choosenAnswer);
        if (correctAnswer == value) {
            Log.d("Quiz", "Richtige anwort.");
            //TODO: Set Buttonfarbe to lightgreen
        }
        else {
            Log.d("Quiz", "Falsche Antwort.");
            //TODO: Set Buttoncolor from correctAnswer to green and buttoncolor from choosenAnswer to red
        }
    }*/

    /**
     * Request question from server
     */
    public void requestQuestion() {
        stompSocket.send("/app/game", null, null);
    }

    //TODO: funktionen noch entfernen, nur zum Testen drin

    public void requestQuestion(String message, String token) {
        stompSocket.send("/app/game", message, token, null);
    }

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