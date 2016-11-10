package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddNewProfile extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_profile);
    }

    public void buttonOnClickSearchByEmail(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.SearchResultProfiles");
        startActivity(intent);
    }

    public void buttonOnClickSearchByUserName(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.SearchByUserName");
        startActivity(intent);
    }
}
