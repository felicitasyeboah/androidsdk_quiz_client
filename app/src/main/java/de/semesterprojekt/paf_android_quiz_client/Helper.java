package de.semesterprojekt.paf_android_quiz_client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import de.semesterprojekt.paf_android_quiz_client.model.SessionManager;

public final class Helper {
        private Helper() {
        }
        public static String formatDate(String timeStamp) {
            DateTimeFormatter formatter
                    = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.GERMANY);

            LocalDateTime dateTime = LocalDateTime.parse(timeStamp.substring(0, timeStamp.length() - 6));
            return dateTime.format(formatter);

    }
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
}
