package com.example.cleareyes.localoyeproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class DateChangeBroadcastReceiver extends BroadcastReceiver {
    public DateChangeBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        context.stopService(new Intent(context, AlarmService.class));

        Intent alaramIntent = new Intent(context, AlarmService.class);
        context.startService(alaramIntent);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, CheckTaskService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, alarmIntent, 0);
        Calendar cal = Calendar.getInstance();
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 5000, pendingIntent);
    }
}
