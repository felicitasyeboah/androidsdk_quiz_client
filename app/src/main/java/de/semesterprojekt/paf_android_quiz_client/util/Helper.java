package de.semesterprojekt.paf_android_quiz_client.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import de.semesterprojekt.paf_android_quiz_client.SessionManager;

/**
 * The Helper class is an utility class which holds different static methods to make the life easier
 */
public final class Helper {
    private static LruCache picassoCache;
    private static Picasso picassoInstance;

    /**
     * Constructor
     */
    private Helper() {
    }

    /**
     * Formats a timeStamp String YYYY-MM-DD HH:MM:SS into Local (German) Format
     *
     * @param timeStamp String YYYY-MM-DD HH:MM:SS+HH:SS
     * @return String DD.MM.YYYY, HH:MM
     */
    public static String formatDate(String timeStamp) {
        DateTimeFormatter formatter
                = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.GERMANY);

        LocalDateTime dateTime = LocalDateTime.parse(timeStamp.substring(0, timeStamp.length() - 6));
        return dateTime.format(formatter);
    }

    /**
     * Creats a Session Expired Dialog with submit Button, to logout user, when jwt has
     * expired
     *
     * @param context ApplicationContext
     * @return Dialog
     */
    public static Dialog getSessionExpiredDialog(Context context) {
        SessionManager sessionManager = SessionManager.getSingletonInstance(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // clears session data and brings user back to the app startscreen to log in again
                sessionManager.logout();
            }
        });
        builder.setMessage("Your session has expired. Please log in again.")
                .setTitle("Session expired");

        builder.setCancelable(false);
        return builder.create();
    }

    /**
     * Transforms Bitmapdata into ByteArray for the ImageUpload
     *
     * @param bitmap Bitmap
     * @return byte[] Array
     */
    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    /* #### SINGLETON PATTERN FOR CUSTOM PICASSO INSTANCE #### */

    /**
     * Creates a custom Picasso instance to control caching
     *
     * @param appContext ApplicationContext
     */
    public static void setPicasso(Context appContext) {
        Picasso.Builder builder = new Picasso.Builder(appContext);
        picassoCache = new LruCache(appContext);
        builder.memoryCache(picassoCache);
        picassoInstance = builder.build();
    }

    /**
     * Returns the custom picasso instance if one already exists, Otherwise it creates a new custom
     * Picasso instance.
     *
     * @param appContext ApplicationContext
     * @return Picasso instance
     */
    public static synchronized Picasso getPicassoInstance(Context appContext) {
        if (picassoInstance == null) {
            setPicasso(appContext);
            Log.d("PicassoSet", picassoInstance.toString());

        }
        Log.d("PicassoGet", picassoInstance.toString());
        return picassoInstance;
    }

    /**
     * Clears cache from custom Picasso instance
     */
    public static void clearPicassoCache() {
        picassoCache.clear();
    }

    /**
     * Stops Picasso instance from accepting further requests
     * and sets instance to null, so on next instantiation there will be create a new instance
     */
    public static void shutdownPicasso() {
        picassoInstance.shutdown();
        picassoInstance = null;
    }

}
