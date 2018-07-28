package com.bracu.project;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.net.URISyntaxException;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //this BroadCastReceiver will be trigged when the pendingIntent of AlarmActivity time is hitted here
        //In that alarm Fixed alarm will be started
        Toast.makeText(context, "ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();
        //calling the soundService for starting the mediaPlayer sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, AlarmSoundService.class));
        }else
            context.startService(new Intent(context, AlarmSoundService.class));

        //start the notification of alarm
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmNotificationService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
    }
}
