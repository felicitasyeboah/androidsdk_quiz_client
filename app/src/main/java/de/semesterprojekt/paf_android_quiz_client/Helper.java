package de.semesterprojekt.paf_android_quiz_client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

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
}
