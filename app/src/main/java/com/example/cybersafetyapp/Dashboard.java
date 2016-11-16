package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    String username,email;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Bundle messages = getIntent().getExtras();
        email = messages.getString("email");
        Toast.makeText(this,"Welcome to the dashboard, "+email.substring(0,email.indexOf("@")),Toast.LENGTH_SHORT).show();
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
