package com.onuryahsi.firebaseexample;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onuryahsi.firebaseexample.model.MyNotification;
import com.onuryahsi.firebaseexample.model.User;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ClientFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ClientFirebaseMessaging";

    private DatabaseReference mDatabase;


    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // readUserChangesFromRemoteDatabase();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "onMessageReceived: Firebase includes a payload");

        if (remoteMessage.getData().size() > 0) {

            MyNotification myNotification = new MyNotification();
            myNotification.title = remoteMessage.getData().get("title");
            myNotification.content = remoteMessage.getData().get("content");
            myNotification.channel_id = remoteMessage.getData().get("channel_id");
            myNotification.message_id = remoteMessage.getData().get("message_id");
            myNotification.isRead = false;

            writePushMessagesToRemoteDatabase(myNotification);

            String extra = "" + remoteMessage.getData().get("extra");
            String title = "" + remoteMessage.getData().get("title");
            String content = "" + remoteMessage.getData().get("content");
            String channel_id = "" + remoteMessage.getData().get("channel_id");
            String message_id = "" + remoteMessage.getData().get("message_id");

            createNotification(title, content, "CHANNEL_1", Integer.parseInt(message_id));

        }
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Firebase Refreshed token: " + token);

        // sendRegistrationToServer(token);

        try {
            Thread.sleep(1000);
            writeUserToRemoteDatabase(token);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("HardwareIds")
    private void writeUserToRemoteDatabase(String token) {
        User u = new User();

        u.userId = "" + Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        u.name = "Onur";
        u.surname = "Yahşi";
        u.phoneNumber = "905325005050";
        u.deviceName = android.os.Build.MANUFACTURER.toUpperCase();
        u.deviceModel = android.os.Build.MODEL;
        u.firabaseToken = token;
        u.lastVisited = getDate();
        mDatabase.child("users").child(u.userId).setValue(u);
    }

    private void writePushMessagesToRemoteDatabase(MyNotification myNotification) {
        mDatabase.child("push-messages").child(myNotification.message_id).setValue(myNotification);
    }

    private void createNotification(String textTitle, String textContent, String CHANNEL_ID, Integer message_id) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // mDatabase.child("push-messages").child(message_id.toString()).child("isRead").setValue(true);

        @SuppressLint("ResourceAsColor")
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_arrow_left_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setLights(0xFF0000FF, 100, 3000)
                .setColor(R.color.colorPrimaryDark);

        createNotificationChannel(CHANNEL_ID);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(message_id, builder.build());
    }

    private void createNotificationChannel(String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Push Notification";
            String description = "Firebase Push Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss a";

    private String getDate() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.UK).format(System.currentTimeMillis());
    }


}


// https://firebase.google.com/docs/cloud-messaging/migrate-v1

// https://fcm.googleapis.com/fcm/send
//
//      {
//        "notification": {
//        "body": {
//        "key1": "value1",
//        "key2": "value2"
//        },
//        "title": "firebase",
//        "sound": "default"
//        },
//        "data": {
//        "extra": "hellooo"
//        },
//        "to": "cSn80QaeTyaNIwwt5JYxIU:APA91bGCL07h1KXd8qugpCCgT6x8vLvGKW8cwQLg6sZgotMf5RcxNbCIWkbG7vbHg86aMBpInJwgiXb6GVjKghgyc4-QK4o3FCbXUcn8EFDVa1yr9KRzC1do-Oc9hUwu2ddboOzZXjIQ",
//        "priority": "high"
//      }