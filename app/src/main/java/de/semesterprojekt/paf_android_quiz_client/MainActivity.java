package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.semesterprojekt.paf_android_quiz_client.LoginActivity;
import de.semesterprojekt.paf_android_quiz_client.RegisterActivity;
import de.semesterprojekt.paf_quiz_client.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /** Called when the user taps the Login button */
    public void gotToLogin(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    /** Called when the user taps the Register button */
    public void gotToRegister(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }
}