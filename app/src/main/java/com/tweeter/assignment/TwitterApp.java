package com.tweeter.assignment;

import android.app.Application;
import android.content.Context;

public class TwitterApp extends Application {

    public static TwitterClient getRestClient(Context context) {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, context);
    }
}