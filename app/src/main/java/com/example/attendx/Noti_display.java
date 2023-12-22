package com.example.attendx;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Noti_display {


    private String Channel_id = "Notification from attandx";

    public void display_notification(Context context, String title, String body){
        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(context,Channel_id)
                .setSmallIcon(R.drawable.attendx)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,mbuilder.build());
    }

}