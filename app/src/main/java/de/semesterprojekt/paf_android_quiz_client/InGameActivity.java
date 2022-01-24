package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.User;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.GameMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.MessageType;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.ResultMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.ScoreMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.StartMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.TimerMessage;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompHeader;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;

public class InGameActivity extends AppCompatActivity {
    SessionManager sessionManager;
    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4, btn_quitSession;
    TextView tv_question, tv_timer, tv_userScore, tv_opponentScore, tv_top_message_box, tv_awaitingStart,
            tv_gameStartIn, tv_startCounter, tv_dsUserName, tv_dsOpponentName,
            tv_dsUserScore, tv_dsOpponentScore, tv_dsNextQuestionTimer,
            tv_drResult;
    ImageView iv_dsUser, iv_dsOpponent;
    ProgressBar prog_timer;

    ConstraintLayout layoutInGameView;

    Dialog startDialog;
    Dialog scoreDialog;
    Dialog resultDialog;

    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + ServerData.STOMP_ENDPOINT));

    RestServiceSingleton restServiceSingleton;
    SharedPreferences sharedPreferences;

    String userToken;
    Gson gson = new Gson();

    StartMessage startMessage;
    GameMessage gameMessage;
    ScoreMessage scoreMessage;
    ResultMessage resultMessage;
    TimerMessage questionTimer;
    TimerMessage startTimer;
    TimerMessage scoreTimer;

    final static public int SECONDS_TO_SOLVE_QUESTION = 10;

    @Override
    protected void onStart() {
        super.onStart();
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        initClassVars();
        initDialogs();
        initViews();
        connectToStompSocket();
        subscribeToStompTopic(ServerData.STOMP_TOPIC);
        setOnClickListeners();
    }

    /**
     * Init Dialogs from Layouts
     */
    protected void initDialogs() {
        //Dialogs
        startDialog = getStartDialog();
        scoreDialog = getScoreDialog();
        resultDialog = getResultDialog();
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

        // Progressbar
        prog_timer = findViewById(R.id.prog_timer);

        // Layout
        layoutInGameView = findViewById(R.id.lo_inGame);

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

                        case START_MESSAGE:
                            startMessage = getStartMessageObject(message);
                            Log.d("Quiz", startMessage.toString());
                            break;

                        case START_TIMER_MESSAGE:
                            Log.d("Quiz", "START_TIMER_MESSAGE erhalten.");

                            // creates TimerMessage from STOMP message respond
                            startTimer = getTimerMessageObject(message);
                            // Load StartTimerMessage into UI
                            setStartDialog();

                            // Handle StartDialog UI
                            updateStartDialog();

                            break;

                        case GAME_MESSAGE:
                            Log.d("Quiz", "MESSAGE: GAMEMESSAGE ERHALTEN.");

                            // creates GameMessage from STOMP message respond
                            getGameMessageObject(message);

                            // load GameMessage into UI
                            initInGameScreen(gameMessage);

                            break;

                        case QUESTION_TIMER_MESSAGE:
                            Log.d("Quiz", "QUESTION_TIMER_MESSAGE erhalten");

                            // creates TimeMessage from STOMP message respond
                            questionTimer = getTimerMessageObject(message);

                            // handle InGame UI
                            updateInGameLayout();

                            break;

                        case SCORE_TIMER_MESSAGE:
                            Log.d("Quiz", messageType.toString());

                            // creates TimeMessage from STOMP message respond
                            scoreTimer = getTimerMessageObject(message);

                            // handle Score Dialog UI
                            updateScoreDialog();
                            break;

                        case SCORE_MESSAGE:
                            Log.d("Quiz", messageType.toString());

                            // creates GameMessage from STOMP message respond
                            scoreMessage = getScoreMessageObject(message);

                            // init Score Dialog
                            showScoreDialog();

                            // load ScoreMessage into Score Dialog
                            setScoreDialog();

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
                            showResultDialog();
                            // load ResultMessage into UI
                            setResultDialog();

                            break;
                        case DISCONNECT_MESSAGE:
                            Log.d("Quiz", message.toString());
                            //TODO: Show DIsconnect Dialog with back to Lobby button for opponent who didnt disconected
                             break;

                    }
                    Log.d("Quiz", "BODY: " + message);

                }
            });
        });
        Log.d("Quiz", "Websocket channel subscribed.");
        sendInitAuthMessage();
        showStartDialog();


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
            // check if answer was correct and highlight correct/wrong answer
            checkAnswer(view, answerButton, answerButton.getText().toString());

            // send selected answer and time to pick the answer to server
            sendAnswer(answerButton, timeNeeded);

            // show waiting screen, when waiting for opponent to answer
            // initWaitingForOpponentAnswerScreen();

        };
        btn_answer1.setOnClickListener(answerButtonClickListener);
        btn_answer2.setOnClickListener(answerButtonClickListener);
        btn_answer3.setOnClickListener(answerButtonClickListener);
        btn_answer4.setOnClickListener(answerButtonClickListener);

        // change your button background

        //TODO: Button entfernen, nur zum testen drin
        //Button erstmal noch zum Testen drin, kommen später weg
        btn_quitSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InGameActivity.this, "Session closed: ", Toast.LENGTH_LONG).show();
                quitSession();
            }
        });
    }

    protected Dialog getStartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_awaiting_start, null));
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Button ->  Cancel -> Back to Lobby
                quitSession();
            }});
        builder.setCancelable(false);
        return builder.create();
    }
    protected void showStartDialog() {
        startDialog.show();
    }
    protected void setStartDialog() {
        tv_awaitingStart = startDialog.findViewById(R.id.tv_awaiting_start);
        tv_gameStartIn = startDialog.findViewById(R.id.tv_game_start_in);
        tv_startCounter = startDialog.findViewById(R.id.tv_start_counter);
        tv_gameStartIn.setVisibility(View.VISIBLE);
        tv_startCounter.setVisibility(View.VISIBLE);
        tv_awaitingStart.setText("Player found.\n" +
                sessionManager.getUserDatafromSession().get(getApplicationContext().getString(R.string.username)) + " vs. " +
                startMessage.getOpponent().getUsername());
    }
    protected void updateStartDialog() {
        tv_startCounter.setText(Integer.toString(startTimer.getTimeLeft()));
        if (startTimer.getTimeLeft() == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startDialog.dismiss();
                }
            }, 1000);
        }
    }

    /**
     * Assign GameMessage to LayoutViews
     *
     * @param gameMessage GameMessage from Server
     */
    public void initInGameScreen(GameMessage gameMessage) {
        layoutInGameView.setVisibility(View.VISIBLE);
        Log.d("Quiz", sharedPreferences.getString(getString(R.string.user_token), ""));
        Log.d("Quiz", gameMessage.getUser().toString());

        // reset Button colors to default
        btn_answer1.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer2.setBackground(getResources().getDrawable(R.drawable.btn_rounded_corner_ingame_correct_answer));
        btn_answer3.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer4.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);

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
    protected void updateInGameLayout() {
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

    protected void initWaitingForOpponentAnswerScreen() {

/*        layoutLobbyView.setVisibility(View.VISIBLE);
        layoutInGameView.setVisibility(View.INVISIBLE);
        tv_loLobbyGameStartIn.setVisibility(View.INVISIBLE);
        tv_startCounter.setVisibility(View.INVISIBLE);
        tv_awaitingStart.setVisibility(View.VISIBLE);
        tv_awaitingStart.setText("Wainting for Player 2 to answer.");*/
    }

    protected Dialog getScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_score, null));
        builder.setCancelable(false);
        return builder.create();
    }
    protected void showScoreDialog() {
        scoreDialog.show();
    }
    protected void setScoreDialog() {
        tv_dsUserName = scoreDialog.findViewById(R.id.tv_ds_user_name);
        tv_dsOpponentName = scoreDialog.findViewById(R.id.tv_ds_opponent_name);
        tv_dsUserScore = scoreDialog.findViewById(R.id.tv_ds_user_score);
        tv_dsOpponentScore = scoreDialog.findViewById(R.id.tv_ds_opponent_score);
        tv_dsNextQuestionTimer = scoreDialog.findViewById(R.id.tv_ds_next_timer);
        tv_dsUserName.setText(scoreMessage.getUser().getUsername() + ":");
        tv_dsOpponentName.setText(scoreMessage.getOpponent().getUsername() + ":");
        tv_dsOpponentScore.setText("+" + Integer.toString(scoreMessage.getOpponentScore()) + "p");
        tv_dsUserScore.setText("+" + Integer.toString(scoreMessage.getUserScore()) + "p");
        //TODO: set user and oppenent images
    }
    protected void updateScoreDialog() {
        tv_dsNextQuestionTimer.setText(Integer.toString(scoreTimer.getTimeLeft()));
        if (scoreTimer.getTimeLeft() == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scoreDialog.dismiss();
                }
            }, 1000);
        }
    }

    protected Dialog getResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_result, null));
        builder.setPositiveButton("Back to Lobby", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Button -> Back to Lobby -> Disconnects from websocket and brings user back to lobby
                quitSession();

            }});
        builder.setCancelable(false);
        return builder.create();
    }
    protected void showResultDialog() {
        resultDialog.show();
    }
    protected void setResultDialog() {
        tv_drResult = resultDialog.findViewById(R.id.tv_dr_result);
        tv_drResult.setText(resultMessage.toString());
    }

    /**
     * Create new GameMesssageObject from STOMP message response
     *
     * @param message GameMessage from Server
     */
    protected void getGameMessageObject(String message) {
        // Converts JSONObject String into GameMessage
        gameMessage = gson.fromJson(message, GameMessage.class);

        // Generates a Hashmap with the 4 answers
        Map<Integer, String> answers = new HashMap<>();
        answers.put(1, gameMessage.getAnswer1());
        answers.put(2, gameMessage.getAnswer2());
        answers.put(3, gameMessage.getAnswer3());
        answers.put(4, gameMessage.getAnswer4());
        gameMessage.setAnswers(answers);

        gameMessage.getUser().setToken(sharedPreferences.getString(getString(R.string.user_token), ""));
        Log.d("Quiz", "GAMEMESSAGEOBJECT: " + gameMessage.toString());
        // Updates Login User Instance with userId, and readyStatus //TODO: missing userimage
        //restServiceSingleton.setUser(gameMessage.getUser());
    }

    protected StartMessage getStartMessageObject(String message) {
        return gson.fromJson(message, StartMessage.class);
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
     * sends an initial message after subscribe with the usertoken to the server for authorization
     */
    public void sendInitAuthMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            // puts usertoken into JsonObject
            jsonObject.put(StompHeader.TOKEN.toString(), userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String message = jsonObject.toString();
        stompSocket.send("/app/game", message);
        Log.d("Quiz", "Send usertoken: " + jsonObject.toString());
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

    public void checkAnswer(View view, Button answerButton, String choosenAnswer) {
        String buttonName = getResources().getResourceEntryName(view.getId());
        String buttonLastChar = buttonName.substring((buttonName).length() - 1);
        int answerNumber = Integer.parseInt(buttonLastChar);
        if (answerNumber == gameMessage.getCorrectAnswer()) {
            // if(gameMessage.getAnswers().get(gameMessage.getCorrectAnswer()).equals(choosenAnswer)) {
            Log.d("Quiz", "Richtige anwort.");
            //TODO: Set Buttonfarbe to lightgreen
            // change your button background

            view.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);
            //answerButton.setBackground(getResources().getDrawable(R.drawable.btn_rounded_corner_ingame_correct_answer));
            //answerButton.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);

        } else if (answerNumber != gameMessage.getCorrectAnswer()) {
            Log.d("Quiz", "Falsche Antwort.");
            //answerButton.setBackground(getResources().getDrawable(R.drawable.btn_rounded_corner_ingame_wrong_answer));
            view.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_wrong_answer);

            //answerButton.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_wrong_answer);
            for (int key : gameMessage.getAnswers().keySet()) {
                if (key == gameMessage.getCorrectAnswer()) {
                    switch (key) {
                        case 1:
                            btn_answer1.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);
                            break;
                        case 2:
                            btn_answer2.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);
                            break;
                        case 3:
                            btn_answer3.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);
                            break;
                        case 4:
                            btn_answer4.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);
                            break;
                    }
                }
            }
            //TODO: Set Buttoncolor from correctAnswer to green and buttoncolor from choosenAnswer to red
        }
    }

    /**
     * quits stompseocket/websocket session and brings user back to Lobby
     */
    public void quitSession() {
        //Disconnect
        stompSocket.close();
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    /**
     * Clears SessionData in Sessionmanager
     */
    public void logout() {
        sessionManager.logout();
        Toast.makeText(InGameActivity.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays Menu in the upper right corner in App-/Toolbar
     * @param menu main menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    /**
     *
     * @param item menu item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.quit_session) {
            quitSession();
        }
        else if (itemId == R.id.logout) {
            quitSession();
            logout();
        }
        //TODO: APP muss sich komplett schließen, wenn nicht zu loesen, dann aus dem menu nehmen!
        else if (itemId == R.id.quit) {
            finish();
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: layout für kleine handys anzeigen, responsiv machen etc...

    /**
     * Add JWT Token to STOMP Header
     *
     * @param userToken JWT usertoken
     */
    protected void addJwtToStompSocketHeader(String userToken) {
        // add JWToken to websocket STOMP Header
        stompSocket.addHeader(StompHeader.TOKEN.toString(), userToken);
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