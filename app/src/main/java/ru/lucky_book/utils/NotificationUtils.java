package ru.lucky_book.utils;


import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

import ru.lucky_book.R;

public class NotificationUtils {
    
    private static Context mContext;

    public static void  init(Context context) {
        mContext = context;
    }

    public static final int PROGRESS_ID = 100;

    public static NotificationCompat.Builder getNotificationBuilder(@StringRes int title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(mContext.getString(title));
        builder.setTicker(mContext.getString(title));
        builder.setOngoing(true);
        return builder;
    }
}
