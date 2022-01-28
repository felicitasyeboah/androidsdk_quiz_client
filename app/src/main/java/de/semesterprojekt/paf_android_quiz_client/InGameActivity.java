package de.semesterprojekt.paf_android_quiz_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.GameMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.MessageType;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.ResultMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.ScoreMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.StartMessage;
import de.semesterprojekt.paf_android_quiz_client.model.game.dto.TimerMessage;
import de.semesterprojekt.paf_android_quiz_client.model.ServerData;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompHeader;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;

public class InGameActivity extends AppCompatActivity {
    SessionManager sessionManager;
    Button btn_answer1, btn_answer2, btn_answer3, btn_answer4;
    Button btn_clicked;
    TextView tv_userName, tv_opponentName,tv_userScore, tv_opponentScore,
            tv_timer, tv_category, tv_question,
            tv_awaitingStart, tv_pvp,
            tv_gameStartIn, tv_startCounter, tv_dsUserName, tv_dsOpponentName,
            tv_dsUserScore, tv_dsOpponentScore, tv_dsNextQuestionTimer,
            tv_dr_textBox, tv_drWinner, tv_drUserName, tv_drOpponentName, tv_drUserScore, tv_drOpponentScore,
            tv_drHighscore;
    ImageView iv_userImage, iv_opponentImage, iv_dsUser, iv_dsOpponent, iv_drUser, iv_drOpponent;
    ProgressBar prog_timer;

    ConstraintLayout layoutInGameView;

    Dialog startDialog;
    Dialog scoreDialog;
    Dialog resultDialog;
    Dialog quitSessionDialog;
    Dialog answerDialog;

    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + ServerData.STOMP_ENDPOINT));

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
        quitSessionDialog = getQuitSessionDialog();
        answerDialog = getAnswerDialog();
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
        btn_answer1.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer2.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer3.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer4.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);

        // TextViews
        // at InGameView
        tv_userName = findViewById(R.id.tv_user_name);
        tv_opponentName = findViewById(R.id.tv_opponent_name);
        tv_userScore = findViewById(R.id.tv_user_score);
        tv_opponentScore = findViewById(R.id.tv_opponent_score);
        tv_category = findViewById(R.id.tv_category);
        tv_timer = findViewById(R.id.tv_timer);
        tv_question = findViewById(R.id.tv_question);

        // Progressbar
        prog_timer = findViewById(R.id.prog_timer);

        // ImageViews
        iv_userImage = findViewById(R.id.iv_user_image);
        iv_opponentImage = findViewById(R.id.iv_opponent_image);

        // Layout
        layoutInGameView = findViewById(R.id.lo_inGame);


    }

    /**
     * Init ClassVars
     */
    protected void initClassVars() {
        sessionManager = new SessionManager(getApplicationContext());
        userToken = sessionManager.getUserDatafromSession().get(getString(R.string.user_token));
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
                            if (scoreTimer.getTimeLeft() == 1) {
                                answerDialog.dismiss();
                            }
                            break;

                        case SCORE_MESSAGE:
                            Log.d("Quiz", messageType.toString());
                            // check if answer was correct and highlight correct/wrong answer
                            checkAnswer(btn_clicked);

                            // creates GameMessage from STOMP message respond
                            scoreMessage = getScoreMessageObject(message);

                            // init Score Dialog
                            showDialog(scoreDialog);

                            // load ScoreMessage into Score Dialog
                            setScoreDialog();
                            break;

                        case RESULT_MESSAGE:
                            Log.d("Quiz", messageType.toString());

                            // creates ResultMessage from STOMP message respond
                            resultMessage = getResultMessageObject(message);

                            // init Result Dialog
                            showDialog(resultDialog);

                            // load ResultMessage into UI
                            setResultDialog();
                            break;
                        case DISCONNECT_MESSAGE:

                            // init QuitSession Dialog
                            showDialog(quitSessionDialog);
                            break;
                    }
                    Log.d("Quiz", "BODY: " + message);

                }
            });
        });
        Log.d("Quiz", "Websocket channel subscribed.");

        // send authorization message with usertoken (jwt)
        sendInitAuthMessage();

        // init StartDialog
        showDialog(startDialog);
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
            btn_clicked = answerButton; // to check answer outside of clicklistener
            // highlight selected button
            answerButton.setBackgroundResource(R.drawable.btn_rounded_corner_yellow);
            // send selected answer and time to pick the answer to server
            sendAnswer(answerButton, timeNeeded);

            // show waiting screen, when waiting for opponent to answer
            showDialog(answerDialog);
        };
        btn_answer1.setOnClickListener(answerButtonClickListener);
        btn_answer2.setOnClickListener(answerButtonClickListener);
        btn_answer3.setOnClickListener(answerButtonClickListener);
        btn_answer4.setOnClickListener(answerButtonClickListener);
    }

    /**
     * Init Start Dialog
     * @return Dialog startDialog
     */
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
    //TODO: Wenn Zeit ist, userbilder auch in startdialog einbauen

    /**
     * Assign views to startDialog
     */
    protected void setStartDialog() {
        tv_awaitingStart = startDialog.findViewById(R.id.tv_awaiting_start);
        tv_gameStartIn = startDialog.findViewById(R.id.tv_game_start_in);
        tv_startCounter = startDialog.findViewById(R.id.tv_start_counter);
        tv_pvp = startDialog.findViewById(R.id.tv_pvp);
        tv_gameStartIn.setVisibility(View.VISIBLE);
        tv_startCounter.setVisibility(View.VISIBLE);
        tv_pvp.setVisibility(View.VISIBLE);
        tv_awaitingStart.setText("Player found.");
        tv_pvp.setText(sessionManager.getUserDatafromSession().get(getApplicationContext().getString(R.string.username)) + " vs. " +
                startMessage.getOpponent().getUserName());
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
        Log.d("Quiz", gameMessage.getUser().toString());

        // reset Button colors to default
        btn_answer1.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer2.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer3.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);
        btn_answer4.setBackgroundResource(R.drawable.btn_rounded_corner_ingame);

        // Set Button and TextView values from gameMessage
        btn_answer1.setText(gameMessage.getAnswer1());
        btn_answer2.setText(gameMessage.getAnswer2());
        btn_answer3.setText(gameMessage.getAnswer3());
        btn_answer4.setText(gameMessage.getAnswer4());
        tv_question.setText(gameMessage.getQuestion());
        tv_category.setText(gameMessage.getCategory());

        btn_answer1.setEnabled(true);
        btn_answer2.setEnabled(true);
        btn_answer3.setEnabled(true);
        btn_answer4.setEnabled(true);

        tv_userName.setText(gameMessage.getUser().getUserName());
        tv_opponentName.setText(gameMessage.getOpponent().getUserName());
        tv_userScore.setText(Integer.toString(gameMessage.getUserScore()));
        tv_opponentScore.setText(Integer.toString(gameMessage.getOpponentScore()));

        Picasso.get().load(ServerData.PROFILE_IMAGE_API + gameMessage.getUserName()).fit().centerInside().into(iv_userImage);
        Picasso.get().load(ServerData.PROFILE_IMAGE_API + gameMessage.getOpponentName()).fit().centerInside().into(iv_opponentImage);
    }

    /**
     * Updates InGameLayout
     */
    protected void updateInGameLayout() {
        prog_timer.setProgress(SECONDS_TO_SOLVE_QUESTION - questionTimer.getTimeLeft());
        tv_timer.setText(Integer.toString(questionTimer.getTimeLeft()) + "s");
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
                }
            }, 1000);
        }
    }

    /**
     * init Score Dialog
     * @return Dialog scoreDialog
     */
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

    /**
     * Assigning views to scoreDialog
     */
    protected void setScoreDialog() {
        tv_dsUserName = scoreDialog.findViewById(R.id.tv_ds_user_name);
        tv_dsOpponentName = scoreDialog.findViewById(R.id.tv_ds_opponent_name);
        tv_dsUserScore = scoreDialog.findViewById(R.id.tv_ds_user_score);
        tv_dsOpponentScore = scoreDialog.findViewById(R.id.tv_ds_opponent_score);
        tv_dsNextQuestionTimer = scoreDialog.findViewById(R.id.tv_ds_next_timer);

        iv_dsUser = scoreDialog.findViewById(R.id.iv_ds_user);
        iv_dsOpponent = scoreDialog.findViewById(R.id.iv_ds_opponent);

        tv_dsUserName.setText(scoreMessage.getUser().getUserName());
        tv_dsOpponentName.setText(scoreMessage.getOpponent().getUserName());
        tv_dsOpponentScore.setText("+" + Integer.toString(scoreMessage.getOpponentScore()));
        tv_dsUserScore.setText("+" + Integer.toString(scoreMessage.getUserScore()));
        String imageUrlUser = ServerData.PROFILE_IMAGE_API + scoreMessage.getUser().getUserName();
        Picasso.get().load(imageUrlUser).fit().centerInside().into(iv_dsUser);
        String imageUrlOpponent = ServerData.PROFILE_IMAGE_API + scoreMessage.getOpponent().getUserName();
        Picasso.get().load(imageUrlOpponent).fit().centerInside().into(iv_dsOpponent);
    }

    /**
     * updates score Dialog
     */
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

    /**
     * init Result Dialog
     * @return Dialog resultDialog
     */
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

    /**
     * Assigning views to resultDialog
     */
    protected void setResultDialog() {
        tv_drWinner = resultDialog.findViewById(R.id.tv_dr_winner);
        tv_drUserName = resultDialog.findViewById(R.id.tv_dr_user_name);
        tv_drOpponentName = resultDialog.findViewById(R.id.tv_dr_opponent_name);
        tv_drUserScore = resultDialog.findViewById(R.id.tv_dr_user_score);
        tv_drOpponentScore = resultDialog.findViewById(R.id.tv_dr_opponent_score);
        tv_dr_textBox = resultDialog.findViewById(R.id.tv_dr_text_box);
        iv_drUser = resultDialog.findViewById(R.id.iv_dr_user);
        iv_drOpponent = resultDialog.findViewById(R.id.iv_dr_opponent);
        if(resultMessage.getWinner() != null) {
            tv_drWinner.setText(resultMessage.getWinner().getUserName());
        } else {
            tv_drWinner.setVisibility(View.INVISIBLE);
            tv_dr_textBox.setText("There is a draw!");
        }
        tv_drUserName.setText(resultMessage.getUserName());
        tv_drOpponentName.setText(resultMessage.getOpponentName());
        tv_drUserScore.setText(Integer.toString(resultMessage.getUserScore()));
        tv_drOpponentScore.setText(Integer.toString(resultMessage.getOpponentScore()));
        tv_drHighscore = resultDialog.findViewById(R.id.tv_dr_highscore);
        String imageUrlUser = ServerData.PROFILE_IMAGE_API + resultMessage.getUserName();
        String imageUrlOpponent = ServerData.PROFILE_IMAGE_API + resultMessage.getOpponentName();
        Picasso.get().load(imageUrlUser).fit().centerInside().into(iv_drUser);
        Picasso.get().load(imageUrlOpponent).fit().centerInside().into(iv_drOpponent);

        if(resultMessage.isHighScore()) {
            tv_drHighscore.setText(resultMessage.getWinner().getUserName() +" "+ tv_drHighscore.getText());
        } else {
            tv_drHighscore.setVisibility(View.GONE);
        }

    }

    /**
     * init opponent left the game dialog
     * @return Dialog quitSessionDialog
     */
    protected Dialog getQuitSessionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_quit_session, null));
        builder.setPositiveButton("Back to Lobby", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Button -> Back to Lobby -> Disconnects from websocket and brings user back to lobby
                quitSession();
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    /**
     * Init Start Dialog
     * @return Dialog startDialog
     */
    protected Dialog getAnswerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_awaiting_answer, null));
        builder.setCancelable(false);
        return builder.create();
    }

    /**
     * show Dialogs
     */
    protected void showDialog(Dialog dialog) {
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        dialog.getWindow().setGravity(Gravity.TOP);
        wmlp.y = 300;
        dialog.getWindow().setAttributes(wmlp);
        dialog.show();
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

        Log.d("Quiz", "GAMEMESSAGEOBJECT: " + gameMessage.toString());
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

    public void checkAnswer(Button btn_clicked) {
        if(btn_clicked != null) {
            String chosenAnswer = btn_clicked.getText().toString();
            // if answer is correct, change buttoncolor to green
            if (gameMessage.getAnswers().get(gameMessage.getCorrectAnswer()).equals(chosenAnswer)) {
                Log.d("Quiz", "Richtige anwort.");

                btn_clicked.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_correct_answer);
            }
            // if answer was incorrect, change buttoncolor to red and highlight button with correct answer green
            else {
                Log.d("Quiz", "Falsche Antwort.");
                btn_clicked.setBackgroundResource(R.drawable.btn_rounded_corner_ingame_wrong_answer);

                for (int key : gameMessage.getAnswers().keySet()) {
                    if (key == gameMessage.getCorrectAnswer()) {
                        switch (key) {
                            case 1:
                                btn_answer1.setBackgroundResource(R.drawable.border);
                                break;
                            case 2:
                                btn_answer2.setBackgroundResource(R.drawable.border);
                                break;
                            case 3:
                                btn_answer3.setBackgroundResource(R.drawable.border);
                                break;
                            case 4:
                                btn_answer4.setBackgroundResource(R.drawable.border);
                                break;
                        }
                    }
                }
            }
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