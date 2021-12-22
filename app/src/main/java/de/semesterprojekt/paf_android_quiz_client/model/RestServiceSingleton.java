package de.semesterprojekt.paf_android_quiz_client.model;

import android.content.Context;


/**
 * Singleton class to set up an single instance to the RestAPI Service
 */
public class RestServiceSingleton {

    private static RestServiceSingleton instance;
    private final RestServiceClient restServiceClient;
    private static Context ctx;
    private User user;

    // Contructor
    private RestServiceSingleton(Context context) {
        ctx = context;
        restServiceClient = new RestServiceClient(ctx);
    }

    /* If your application makes constant use of the network, it's probably most efficient to set up
    a single instance of RequestQueue that will last the lifetime of your app. You can achieve this
    in various ways. The recommended approach is to implement a singleton class that encapsulates
    RequestQueue and other Volley functionality. Another approach is to subclass Application and set
    up the RequestQueue in Application.onCreate(). But this approach is discouraged; a static singleton
    can provide the same functionality in a more modular way.

    A key concept is that the RequestQueue must be instantiated with the Application context, not an
    Activity context. This ensures that the RequestQueue will last for the lifetime of your app,
    instead of being recreated every time the activity is recreated (for example, when the user
    rotates the device). */

    // DataServiceSingleton or RequestSingleton

    // Singleton

    /**
     * Returns the the RestServiceSingleton instance, if one already exists.
     * Otherwise it creates a new RestServiceSinglton instance.
     *
     * @param context Applicationcontext
     * @return RestServiceSingleton instance
     */
    public static synchronized RestServiceSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RestServiceSingleton(context);
        }
        return instance;
    }

    /**
     * Logs in the User
     *
     * @param username Username
     * @param password Password
     * @param listener onLogin listener
     */
    public void login(String username, String password, RestServiceListener listener) {
        restServiceClient.login(username, password, listener);
    }

    /**
     * Registers a new User
     *
     * @param username Username
     * @param password Password
     * @param listener onRegister listener
     */
    public void register(String username, String password, RestServiceListener listener) {
        restServiceClient.register(username, password, listener);
    }

    public void getHighscore(RestServiceListener listener) {
        String userToken = this.getUser().getToken();
        restServiceClient.getHighscore(userToken, listener);

    }


    // Getters & Setters
    public Context getCtx() {
        return ctx;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
