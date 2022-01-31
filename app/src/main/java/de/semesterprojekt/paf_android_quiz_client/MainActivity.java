package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.semesterprojekt.paf_android_quiz_client.util.Helper;

/**
 * Controlls the main View
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Helper.setPicassoCache(getApplicationContext());
    }

    /**
     * Called when the user taps the Login button
     */
    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user taps the Register button
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}