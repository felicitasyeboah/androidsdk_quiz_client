package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;
import de.semesterprojekt.paf_android_quiz_client.model.User;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    TextView lnk_register;
    EditText et_username, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // assigning values to button, textView and editText on layout
        btn_login = findViewById(R.id.btn_login);
        lnk_register = findViewById(R.id.tv_registerLink);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        // This happens, when clicking on the login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                //validating inputs
                if (TextUtils.isEmpty(username)) {
                    et_username.setError("Please enter your username");
                    et_username.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    et_password.setError("Please enter your password");
                    et_password.requestFocus();
                    return;
                }

                final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(LoginActivity.this.getApplication());

                restServiceSingleton.login(username, password, new RestServiceListener() {
                    @Override
                    public void onLogin(User user) {
                        super.onLogin(user);
                        if (user != null) {

                            storeUserDataToSharedPref(user);

                            restServiceSingleton.setUser(user);
                            Intent intent = new Intent(getApplicationContext(), StartmenueActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "User " + user.getUsername() + " logged in", Toast.LENGTH_SHORT).show();
                            Log.d("Quiz", user.toString());
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // This happens, when clicking on the register link
        lnk_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    /**
     * @param user the logged in user
     */
    public void storeUserDataToSharedPref(User user) {
        // Open SharedPref file
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        // Write to SharedPref file
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.user_token), user.getToken());
        editor.putString(getString(R.string.username), user.getUsername());
        Log.d("Quiz", user.getToken() + " " + user.getUsername());

        // apply for async
        editor.apply(); // or ed.commit(); for sync
    }
}


//TODO: Absicherung, dass man sich nciht doppelt einloggen kann. Was ist mit if (user!= null);