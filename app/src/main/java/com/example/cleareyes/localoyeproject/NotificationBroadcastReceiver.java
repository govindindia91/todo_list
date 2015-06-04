package com.example.cleareyes.localoyeproject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public NotificationBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        if(intent.getIntExtra("id1", -1) == 17) {
            //notificationManager.cancel(17);
            context.startActivity(newIntent);
        }
        else if(intent.getIntExtra("id2", -1) == 18) {
            //notificationManager.cancel(18);
            context.startActivity(newIntent);
        }

    }
}
