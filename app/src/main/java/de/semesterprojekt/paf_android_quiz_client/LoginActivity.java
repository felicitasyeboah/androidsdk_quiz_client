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

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText et_username, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // assigning values to button and editText on layout
        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                final Model model = Model.getInstance(LoginActivity.this.getApplication());
                model.login(username, password, new AbstractRestServiceListener() {
                    @Override
                    public void onLogin(User user) {
                        super.onLogin(user);
                        model.setUser(user);
                        Intent intent = new Intent(LoginActivity.this, StartmenueActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "User " + user.getUsername() + " logged in", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
