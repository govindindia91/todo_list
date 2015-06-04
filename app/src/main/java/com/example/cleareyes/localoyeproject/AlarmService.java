package com.example.cleareyes.localoyeproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

public class AlarmService extends Service {

    AlarmManager alarmMgr = null;
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }


    @Override
    public void onCreate() {
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, CheckTaskService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        Calendar cal = Calendar.getInstance();
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 5000, pendingIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
