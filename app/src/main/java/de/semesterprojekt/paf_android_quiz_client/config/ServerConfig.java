package de.semesterprojekt.paf_android_quiz_client.config;

/**
 * Enter the IP of the server here!!
 * on Android client we cant use localhost for the server, if its running on localhost,
 * because the android client thinks the localhost is the client itself...
 */
public class ServerConfig {
    // IP and Port from SERVER
    public final static String SERVER_ADDRESS = "192.168.77.106:8080";

    // RESTSERVICE
    public final static String LOGIN_API = "http://" + SERVER_ADDRESS + "/auth/login";
    public final static String REGISTER_API = "http://" + SERVER_ADDRESS + "/auth/register";
    public final static String HIGHSCORE_API = "http://" + SERVER_ADDRESS + "/highscore";
    public final static String PLAYED_GAMES_API = "http://" + SERVER_ADDRESS + "/playedGames";
    public final static String UPLOAD_FILE_API = "http://" + SERVER_ADDRESS + "/upload";
    public final static String PROFILE_IMAGE_API = "http://" + SERVER_ADDRESS + "/profileImage/";

    // WEBSOCKET
    public final static String STOMP_TOPIC = "/user/topic/game";
    public final static String WEBSOCKET_URL = "ws://" + SERVER_ADDRESS;
    public final static String STOMP_ENDPOINT = "/websocket";


}
