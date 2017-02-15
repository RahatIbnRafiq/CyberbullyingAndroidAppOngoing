package com.example.cybersafetyapp.IntentServicePackage;

import android.app.IntentService;
import android.content.Intent;


public class IntentServiceVine extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;


    public IntentServiceVine()
    {
        super("IntentServiceVine");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


    }
}
