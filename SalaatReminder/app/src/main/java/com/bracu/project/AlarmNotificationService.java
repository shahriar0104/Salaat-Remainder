package com.bracu.project;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class AlarmNotificationService extends IntentService {
    private NotificationManager alarmNotificationManager;
    public static final int NOTIFICATION_ID = 1;

    //initialize channel name,id,and importance for android version >= oreo
    String CHANNEL_ID = "my_channel_01";
    CharSequence name = "Azan";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    public AlarmNotificationService() {
        super("AlarmNotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("Wake Up! Time for Salaat");
    }

    private void sendNotification(String msg) {
        //notification service started
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        //if click on notification the go to alarmActivity class
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class).putExtra("check","checkMate"), PendingIntent.FLAG_UPDATE_CURRENT);

        //if android version >= oreo then channel id,name will be also added with notificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            alarmNotificationManager.createNotificationChannel(mChannel);
        }
        //notification structure is being set
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setChannelId(CHANNEL_ID)
                .setContentText(msg).setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);
        //notication Build Up
        alarmNotificationManager.notify(NOTIFICATION_ID, alamNotificationBuilder.build());
    }


}
