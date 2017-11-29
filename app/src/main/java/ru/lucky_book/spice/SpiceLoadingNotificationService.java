package ru.lucky_book.spice;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.octo.android.robospice.notification.SpiceNotificationService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestProgress;

import ru.lucky_book.R;
import ru.lucky_book.features.order_screen.OrderActivity;

/**
 * Created by histler
 * on 01.09.16 13:02.
 */
public class SpiceLoadingNotificationService extends SpiceNotificationService {

    private static final float MAX_PROGRESS = 100;

    @Override
    public Notification onCreateNotificationForRequestFailure(SpiceException ex) {
        return createCustomSpiceNotification("Failure");
    }

    @Override
    public Notification onCreateNotificationForRequestSuccess() {
        return createCustomSpiceNotification("Success");
    }

    @Override
    public Notification onCreateNotificationForRequestProgress(RequestProgress requestProgress) {
        return createCustomSpiceNotification("download in progress", Math.round(requestProgress.getProgress() * MAX_PROGRESS));
    }

    private Notification createCustomSpiceNotification(String text) {
        return createCustomSpiceNotification(text, 0);
    }

    private Notification createCustomSpiceNotification(String text, int progress) {
        Intent intent = new Intent(SpiceLoadingNotificationService.this, OrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(SpiceLoadingNotificationService.this, 6, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(SpiceLoadingNotificationService.this)
                //.setContentTitle(getString(R.string.loading))
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);

        if (progress != 0) {
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

}
