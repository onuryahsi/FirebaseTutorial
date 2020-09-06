package com.onuryahsi.firebaseexample.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MyNotification {

    public String title;
    public String content;
    public String channel_id;
    public String message_id;
    public boolean isRead;

    public MyNotification() {
    }
}
