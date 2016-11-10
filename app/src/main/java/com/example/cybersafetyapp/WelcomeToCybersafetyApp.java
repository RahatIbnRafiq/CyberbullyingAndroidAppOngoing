package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeToCybersafetyApp extends AppCompatActivity {
    private Intent intent;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_cybersafety_app);
    }

    public void buttonOnClickAboutUs(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.Aboutus");
        startActivity(intent);
    }

    public void buttonOnClickLogIn(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.LogIn");
        startActivity(intent);
    }

    public void buttonOnClickRegister(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.Register");
        startActivity(intent);
    }
}
