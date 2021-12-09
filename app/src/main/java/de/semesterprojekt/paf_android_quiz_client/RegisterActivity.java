package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.semesterprojekt.paf_android_quiz_client.model.AbstractRestServiceListener;
import de.semesterprojekt.paf_android_quiz_client.model.Model;
import de.semesterprojekt.paf_android_quiz_client.model.User;

public class RegisterActivity extends AppCompatActivity {
    Button btn_register;
    EditText et_username, et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // assigning values to button and editText on layout
        btn_register = findViewById(R.id.btn_register);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                final Model model = Model.getInstance(RegisterActivity.this.getApplication());
                model.register(username, password, new AbstractRestServiceListener() {
                    @Override
                    public void onRegister(String username) {
                        super.onRegister(username);
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "User " + username + " successfully registered", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}