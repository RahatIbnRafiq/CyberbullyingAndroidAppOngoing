package com.example.cybersafetyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }


    public void clickSamplecomments(View v)
    {
        Toast.makeText(Notifications.this,"Sample Comments go here",Toast.LENGTH_SHORT).show();
    }

    public void clickWholeMediaSession(View v)
    {
        Toast.makeText(Notifications.this,"Whole media session goes here",Toast.LENGTH_SHORT).show();
    }
}
