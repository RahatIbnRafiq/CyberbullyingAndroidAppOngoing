package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SearchByUserName extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_user_name);
    }

    public void buttonOnclickSearchByUserName(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.SearchResultProfiles");
        startActivity(intent);
    }
}
