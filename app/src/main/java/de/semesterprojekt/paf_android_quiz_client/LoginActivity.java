package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;

/**
 * This class controlls the LoginView
 */
public class LoginActivity extends AppCompatActivity {
    Button btn_login;
    TextView lnk_register;
    EditText et_username, et_password;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(getApplicationContext());
        initViews();
        setOnClickListeners();
    }

    /**
     * assigning values from buttons, textView and editText on layout
     */
    protected void initViews() {
        btn_login = findViewById(R.id.btn_login);
        lnk_register = findViewById(R.id.tv_registerLink);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
    }

    /**
     * Set onClick Listeners to the buttons
     */
    protected void setOnClickListeners() {
        btn_login.setOnClickListener(view -> onLoginButtonClick());
        lnk_register.setOnClickListener(view -> onRegisterButtonClick());
    }

    /**
     * This happens, when clicking on the login button
     */
    protected void onLoginButtonClick() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        validateInputs(username, password);
        loginToRestService(username, password);
    }

    /**
     * This happens, when clicking on the register link
     */
    protected void onRegisterButtonClick() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    /**
     * validate inputs
     *
     * @param username username from inputfield
     * @param password password from inputfield
     */
    protected void validateInputs(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            et_username.setError("Please enter your username");
            et_username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please enter your password");
            et_password.requestFocus();
        }
    }

    /**
     * Logs the user in to the restServer
     *
     * @param username username
     * @param password password
     */
    protected void loginToRestService(String username, String password) {
        RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(LoginActivity.this.getApplication());
        restServiceSingleton.login(username, password, new RestServiceListener() {
            @Override
            public void onLogin(String userToken) {
                super.onLogin(userToken);
                if (userToken != null) {
                    sessionManager.createLoginSession(username, userToken);
                    goToLobby();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Switch to the Lobby activity
     *
     */
    protected void goToLobby() {
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, "User " + sessionManager.getUserDatafromSession().get(getApplicationContext().getString(R.string.username)) + " logged in", Toast.LENGTH_SHORT).show();
        Log.d("Quiz", sessionManager.getUserDatafromSession().toString());
    }

    //TODO: Userobject erst in shared pref instanzieren?? Was ist mit if (user!= null);
}


