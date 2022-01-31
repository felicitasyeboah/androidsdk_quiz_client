package de.semesterprojekt.paf_android_quiz_client.stomp.client.listener;

/**
 * STOMP connection established listener.
 */
public abstract class StompConnectionListener {

    /**
     * STOMP is in establishing connection state.
     */
    public void onConnecting() {
    }

    /**
     * STOMP has been successfully connected.
     */
    public void onConnected() {
    }

    /**
     * STOMP has been disconnected.
     */
    public void onDisconnected() {
    }
}
