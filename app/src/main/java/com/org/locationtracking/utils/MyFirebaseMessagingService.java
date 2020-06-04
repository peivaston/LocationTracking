package com.org.locationtracking.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.org.locationtracking.R;
import com.org.locationtracking.activities.MainActivity;

import androidx.core.app.NotificationCompat;

//***************************************************************************************************************
public class MyFirebaseMessagingService
        extends FirebaseMessagingService
//***************************************************************************************************************
{


    //used to receive nottificaion
    //***************************************************************************************************************
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    //***************************************************************************************************************
    {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage == null || remoteMessage.getNotification() == null)
            return;

        String notification_title = remoteMessage.getNotification()
                                                 .getTitle();
        String notification_message = remoteMessage.getNotification()
                                                   .getBody();
        Intent countryDetailIntent = new Intent(
                getApplicationContext(),
                MainActivity.class);

        //create notification with click intent
        createNotification(getApplicationContext(), notification_title, notification_message,
                           countryDetailIntent);
    }

    //*****************************************************************************************
    private void createNotification(Context context, String title, String body, Intent intent)
    //*****************************************************************************************
    {
        //set notification title

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01sas";
        String channelName = "Channel Namesasasasasasa";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(body);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            mBuilder.setColor(context.getResources()
                                     .getColor(android.R.color.black));
        }
        else
        {
            mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
