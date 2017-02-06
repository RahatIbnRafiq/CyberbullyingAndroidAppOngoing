package com.example.cybersafetyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by RahatIbnRafiq on 1/18/2017.
 */

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(UtilityVariables.tag,"Inside AlertReceiver class");
        //createNotification(context,"Times up", "5 Seconds have passed","alert");
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert)
    {
        PendingIntent notificationIntent = PendingIntent.getActivity(context,0,new Intent(context,Notifications.class),0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msg)
                        .setContentText(msgAlert).setContentText(msgText);

        mBuilder.setContentIntent(notificationIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,mBuilder.build());
    }
}
