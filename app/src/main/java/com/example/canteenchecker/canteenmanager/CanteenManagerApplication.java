package com.example.canteenchecker.canteenmanager;

import android.app.Application;

public class CanteenManagerApplication extends Application {
    private static final String FIREBASE_MESSAGING_TOPIC = "canteenManager";
    private static CanteenManagerApplication instance;
    private String authenticationToken;
    private Integer canteenId = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //FirebaseApp.initializeApp(this);
        //FirebaseMessaging.getInstance().subscribeToTopic(FIREBASE_MESSAGING_TOPIC);
    }

    public static CanteenManagerApplication getInstance() {
        return instance;
    }

    public synchronized String getAuthenticationToken() {
        return authenticationToken;
    }

    public synchronized void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public synchronized boolean isAuthenticated() {
        return getAuthenticationToken() != null;
    }

    public synchronized Integer getCanteenId(){return this.canteenId;}
    public synchronized void setCanteenId(int value){this.canteenId = value;}
}
