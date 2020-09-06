package com.onuryahsi.firebaseexample.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;


@IgnoreExtraProperties
public class User implements Serializable {
    public String userId;
    public String name;
    public String surname;
    public String phoneNumber;
    public String deviceName;
    public String deviceModel;
    public String firabaseToken;
    public String lastVisited;
}