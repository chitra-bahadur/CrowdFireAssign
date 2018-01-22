package com.example.crowdfireassign.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.crowdfireassign.ui.MainActivity;

/**
 * Created by chitra on 19/1/18.
 */

public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            new MainActivity().setAlarmForNotification();
        }
    }
}
