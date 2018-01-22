package com.example.crowdfireassign.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.crowdfireassign.R;
import com.example.crowdfireassign.ui.MainActivity;

/**
 * Created by chitra on 19/1/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent to invoke app when click on notification.
        Intent intentToRepeat = new Intent(context, MainActivity.class);
        intentToRepeat.putExtra("Extras", true);
        //set flag to relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //pending intent to handle launch of activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmManager.ELAPSED_REALTIME_WAKEUP,
                intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build Notification
        Notification notification = buildNotification(context, pendingIntent).build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //send local notification
        manager.notify(1, notification);

        Log.e("AlarmReceiver", "Notification Sent");

    }

    public NotificationCompat.Builder buildNotification(Context context, PendingIntent intent){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "M_CH_ID")
                .setContentIntent(intent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Combination of clothes")
                .setAutoCancel(true);

        return builder;
    }
}
