package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SearchResultProfiles extends AppCompatActivity {
    private static String tag  = "cybersafetyapp";
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_profiles);
        Bundle messages = intent.getExtras();
        String json = messages.getString("json");
        Log.i(tag,json);

    }

    public void buttonOnClickStartMonitoring(View v)
    {
        intent = new Intent("com.example.cybersafetyapp.Dashboard");
        startActivity(intent);
    }
}
