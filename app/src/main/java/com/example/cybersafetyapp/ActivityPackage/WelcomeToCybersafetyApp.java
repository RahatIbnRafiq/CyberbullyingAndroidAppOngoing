package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class WelcomeToCybersafetyApp extends AppCompatActivity {

    private String classname = this.getClass().getSimpleName();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_cybersafety_app);
    }

    public void buttonOnClickAboutUs(View v)
    {
        Intent intent = new Intent(this, Aboutus.class);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
        startActivity(intent);
    }

    public void buttonOnClickLogIn(View v)
    {
        Intent intent = new Intent(this, LogIn.class);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
        startActivity(intent);
    }

    public void buttonOnClickRegister(View v)
    {
        Intent intent = new Intent(this, Register.class);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
        startActivity(intent);
    }
}
