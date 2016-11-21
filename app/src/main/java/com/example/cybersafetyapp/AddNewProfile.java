package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AddNewProfile extends AppCompatActivity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_profile);
        Bundle messages = getIntent().getExtras();
        if(messages == null ||
                messages.getString(IntentSwitchVariables.sourceClassName) == null ||
                !messages.getString(IntentSwitchVariables.sourceClassName).equals(Dashboard.class.getName()))
        {
            Intent intent = new Intent(IntentSwitchVariables.Dashboard);

            try{
                email = messages.getString(IntentSwitchVariables.email);
                intent.putExtra(IntentSwitchVariables.email,email);

            }catch(Exception e)
            {
                Log.i(UtilityVariables.tag,UtilityVariables.AddNewProfile+" add new profile did not get email.");
            }

            intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
            startActivity(intent);
        }
        email = messages.getString(IntentSwitchVariables.email);
    }

    public void buttonOnClickSearchByUserName(View v)
    {
        Intent intent = new Intent(IntentSwitchVariables.SearchByUserName);
        intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
        intent.putExtra(IntentSwitchVariables.email,email);
        startActivity(intent);
    }
}
