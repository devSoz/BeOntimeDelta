package com.example.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static com.example.myapplication.MainActivity.CHANNEL_ID;

public class AlarmService extends Service
{

    public static final String ANDROID_CHANNEL_NAME = "Background Service";
    public static final String ANDROID_CHANNEL_ID = "AlarmApp2";

    AlarmManager alarmManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            androidChannel.enableLights(false);
            androidChannel.enableVibration(false);
            androidChannel.setLightColor(Color.BLUE);
            //androidChannel.setImportance(NotificationManager.IMPORTANCE_NONE);
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(androidChannel);

        }

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(1, new NotificationCompat.Builder(this,
                ANDROID_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Alarms is running in background")
                .setContentIntent(pendingIntent)
                .build());
    }
}



