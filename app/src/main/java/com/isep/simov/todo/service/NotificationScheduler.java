package com.isep.simov.todo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationScheduler extends BroadcastReceiver {

    private static final String TAG = NotificationScheduler.class.toString();
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        notification.when = System.currentTimeMillis();
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
        Log.d(TAG, "Firing notification for notification: " + notification.when);

    }
}
