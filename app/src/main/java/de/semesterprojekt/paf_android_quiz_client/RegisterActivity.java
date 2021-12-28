package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.restservice.RestServiceSingleton;

/**
 * Controls Register View / Layout
 */
public class RegisterActivity extends AppCompatActivity {
    Button btn_register;
    EditText et_username, et_password;
    TextView lnk_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // assigning values from button and editText on layout
        btn_register = findViewById(R.id.btn_register);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        lnk_login = findViewById(R.id.tv_loginLink);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get username and password from Inputs
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                // Validate uername and password from Inputs
                validateInputs(username, password);

                // Get singleton instance to RestAPI-Server
                final RestServiceSingleton restServiceSingleton = RestServiceSingleton.getInstance(RegisterActivity.this.getApplication());

                // Register user in Database
                restServiceSingleton.register(username, password, new RestServiceListener() {

                    public void onRegister(String username) {
                        super.onRegister(username);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "User " + username + " successfully registered", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // This happens, when clicking on the login link
        lnk_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }

    /**
     * Validates user inputs
     *
     * @param username username
     * @param password password
     */
    public void validateInputs(String username, String password) {
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
    }
}