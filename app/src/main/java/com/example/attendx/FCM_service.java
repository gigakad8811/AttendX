package com.example.attendx;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCM_service extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() != null){
            String title = remoteMessage.getNotification().getTitle();
            String txt_content = remoteMessage.getNotification().getBody();
            Noti_display nd = new Noti_display();
            nd.display_notification(getApplicationContext(),title,txt_content);
        }
    }
}
