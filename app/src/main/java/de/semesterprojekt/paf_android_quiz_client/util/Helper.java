package de.semesterprojekt.paf_android_quiz_client.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import de.semesterprojekt.paf_android_quiz_client.SessionManager;

public final class Helper {
        private Helper() {
        }

    /**
     * Formats a timeStamp String YYYY-MM-DD HH:MM:SS into Local (German) Format
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
     * @param context Application Context
     * @return Dialog
     */
    public static Dialog getSessionExpiredDialog(Context context) {
        SessionManager sessionManager = new SessionManager(context);
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
     * Transforms Bitmapdata into ByteArray
     * @param bitmap Bitmap
     * @return byte[] Array
     */
    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}