package com.example.cleareyes.localoyeproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class CheckTaskService extends Service {

    SharedPreferences sharedPreferences = null;

    public CheckTaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i("CheckTaskService", "Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("CheckTaskService", "Destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Log.i("CheckTaskService", "StartCommand");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        DatabaseHandler db =  new DatabaseHandler(CheckTaskService.this, "LocalOye", null ,1);
        ArrayList<Task> currentTasks = new ArrayList<>();
        ArrayList<Task> pendingTasks = new ArrayList<>();
        currentTasks = db.getAllActiveTasks("current", false, 0);
        pendingTasks = db.getAllActiveTasks("pending", false, 0);

        if(currentTasks.size() > 0) {
            for(int i =0 ;i < currentTasks.size();i++) {
                Date currentDate = new Date(System.currentTimeMillis());
                Date endDate = new Date(currentTasks.get(i).getEndDate().longValue());
               // Log.i("dates", currentDate.toString()+" --- "+endDate.toString() + "-- "+endDate.getYear());
                if(currentDate.getYear() >= (endDate.getYear()-1900) && currentDate.getMonth() >= endDate.getMonth() && currentDate.getDate() > endDate.getDate()) {
                  //  Log.i("change date","true");
                    Task task = currentTasks.get(i);
                    task.setTaskState("pending");
                    db.updateTask(task);
                }
            }
        }

        if(pendingTasks.size() > 0) {
            for(int i=0;i< pendingTasks.size();i++) {
                Date currentDate = new Date(System.currentTimeMillis());
                Date endDate = new Date(pendingTasks.get(i).getEndDate().longValue());
                if(currentDate.getYear() <= (endDate.getYear()-1900) && currentDate.getMonth() <= endDate.getMonth() && currentDate.getDate() <= endDate.getDate()) {
                  //  Log.i("change date","true");
                    Task task = pendingTasks.get(i);
                    task.setTaskState("current");
                    db.updateTask(task);
                }
            }
        }

        currentTasks = db.getAllActiveTasks("current", false, 0);
        pendingTasks = db.getAllActiveTasks("pending", false, 0);

        if(currentTasks.size() > 0) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("LocalOye - Tasks")
                            .setOnlyAlertOnce(true)
                            .setContentText("You have few current tasks to complete");

            Intent resultIntent = new Intent(this, NotificationBroadcastReceiver.class);
            resultIntent.putExtra("id1", 17);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, resultIntent, 0);
            mBuilder.setContentIntent(clickIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            /*int addedCurrentNotification = sharedPreferences.getInt("AddedCurrentNotification", 2);

            if(addedCurrentNotification == 2 || addedCurrentNotification == 0) {*/
                mNotificationManager.notify(17, mBuilder.build());
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("AddedCurrentNotification", 1);
                editor.commit();
            }*/
        }
        else {
            try{
                NotificationManager notificationManager = (NotificationManager) CheckTaskService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(17);
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("AddedCurrentNotification", 0);
                editor.commit();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(pendingTasks.size() > 0) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("LocalOye - Tasks")
                            .setOnlyAlertOnce(true)
                            .setContentText("You have few pending tasks to complete");

            Intent resultIntent = new Intent(this, NotificationBroadcastReceiver.class);
            resultIntent.putExtra("id2", 18);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, resultIntent, 0);
            mBuilder.setContentIntent(clickIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            /*int addedPendingNotification = sharedPreferences.getInt("AddedPendingNotification", 2);
            if(addedPendingNotification == 2 || addedPendingNotification == 0) {*/
                mNotificationManager.notify(18, mBuilder.build());
               /* SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("AddedPendingNotification", 1);
                editor.commit();
            }*/
        }
        else {
            try{
                NotificationManager notificationManager = (NotificationManager) CheckTaskService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(18);
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("AddedPendingNotification", 0);
                editor.commit();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.stopSelf();
        return 1;
    }

    private boolean isNotificationVisible(int id) {
        Intent notificationIntent = new Intent(this, NotificationBroadcastReceiver.class);
        PendingIntent current = PendingIntent.getService(this, id, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return current != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }
}
