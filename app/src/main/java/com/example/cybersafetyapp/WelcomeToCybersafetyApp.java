package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeToCybersafetyApp extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_cybersafety_app);
    }

    public void buttonOnClickAboutUs(View v)
    {
        Intent intent = new Intent(IntentSwitchVariables.AboutUs);
        intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
        startActivity(intent);
    }

    public void buttonOnClickLogIn(View v)
    {
        Intent intent = new Intent(IntentSwitchVariables.LogIn);
        intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
        startActivity(intent);
    }

    public void buttonOnClickRegister(View v)
    {
        Intent intent = new Intent(IntentSwitchVariables.Register);
        intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
        startActivity(intent);
    }
}
