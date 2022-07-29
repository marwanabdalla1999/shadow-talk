package com.example.myapplication15.Notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.media.app.NotificationCompat;

import com.example.myapplication15.MainActivity;
import com.example.myapplication15.R;
import com.example.myapplication15.chatactivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.installations.FirebaseInstallationsApi;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.installations.internal.FidListener;
import com.google.firebase.installations.internal.FidListenerHandle;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class myfirebaseservice extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sented=remoteMessage.getData().get("sented");


            sentNotifaction(remoteMessage);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sentNotifaction(RemoteMessage remoteMessage)  {
    String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        String img=remoteMessage.getData().get("img");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
       // int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MainActivity.class);
        Uri defultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);

        Bitmap image=null;
           try {
               URL url = new URL(img);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
           }catch (Exception e){

           }
        Notification notification1=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSound(defultsound)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.appicon,"replay",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setLargeIcon(image)
                .setSmallIcon(R.drawable.appicon)
                .build();
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(1,notification1);


    }
}