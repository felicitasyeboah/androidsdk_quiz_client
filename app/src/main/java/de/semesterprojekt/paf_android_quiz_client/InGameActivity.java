package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Button;

import java.net.URI;

import de.semesterprojekt.paf_android_quiz_client.stomp.StompFrame;
import de.semesterprojekt.paf_android_quiz_client.stomp.client.StompClient;
import de.semesterprojekt.paf_android_quiz_client.stomp.client.listener.StompMessageListener;


public class InGameActivity extends AppCompatActivity {

    private static final String TAG = "Websocket";
    Button btn_answer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        final StompClient stompSocket = new StompClient(URI.create("ws://192.168.77.106:8080/websocket"));

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
        // Subscribing to a topic once STOMP connection is established
        stompSocket.subscribe("/topic/game", new StompMessageListener() {

            @Override
            public void onMessage(StompFrame stompFrame) {
                System.out.println("Server message: " + stompFrame.getBody());

                // Disconnect
                //stompSocket.close();
            }
        });

        // Sending JSON message to a server
        String message = "{\"name\": \"Jack\"}";
        stompSocket.send("/app/game", message);
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