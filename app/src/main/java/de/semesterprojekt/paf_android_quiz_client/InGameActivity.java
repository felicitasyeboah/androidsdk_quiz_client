package de.semesterprojekt.paf_android_quiz_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import java.net.URI;

import de.semesterprojekt.paf_android_quiz_client.model.stomp.StompFrame;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.StompClient;
import de.semesterprojekt.paf_android_quiz_client.model.stomp.client.listener.StompMessageListener;


public class InGameActivity extends AppCompatActivity {

    Button btn_answer1;
    public final static String WS_URL = "ws://" + ServerData.SERVER_ADDRESS;
    final StompClient stompSocket = new StompClient(URI.create(WS_URL + "/websocket"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

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


    public void sendMessage(String message) {
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