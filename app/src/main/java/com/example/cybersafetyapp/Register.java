package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Register extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void buttonOnClickRegister(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.LogIn");
        startActivity(intent);
    }
}
