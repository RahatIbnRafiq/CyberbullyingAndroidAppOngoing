package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void buttonOnClickAddNewProfile(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.AddNewProfile");
        startActivity(intent);
    }

    public void buttonOnclickCheckNotifications(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.Notifications");
        startActivity(intent);
    }

    public void buttonOnClickEditProfile(View v)
    {
        //intent = new Intent("com.example.cybersafetyapp.Notifications");
        //startActivity(intent);
    }


}
