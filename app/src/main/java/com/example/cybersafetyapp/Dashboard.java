package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Bundle messages = getIntent().getExtras();
        if (messages != null) {
            String sourceClassName = messages.getString(IntentSwitchVariables.sourceClassName);
            if (sourceClassName!=null)
            {
                if (sourceClassName.equals(LogIn.class.getName()))
                {
                    email = messages.getString(IntentSwitchVariables.email);
                    if (email!=null)
                        Toast.makeText(this, ToastMessagesVariables.WelcomeToDashboard + email.substring(0, email.indexOf("@")), Toast.LENGTH_SHORT).show();
                }
                else if (sourceClassName.equals(SearchResultProfiles.class.getName()))
                {
                    //Toast.makeText(this, ToastMessagesVariables.YouAreNowMonitoring + messages.getString(IntentSwitchVariables.toBeMonitored), Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    public void buttonOnClickAddNewProfile(View v)
    {
        Intent intent = new Intent(IntentSwitchVariables.AddNewProfile);
        intent.putExtra(IntentSwitchVariables.sourceClassName,IntentSwitchVariables.Dashboard);
        intent.putExtra(IntentSwitchVariables.email,email);
        startActivity(intent);
    }

    public void buttonOnclickCheckNotifications(View v)
    {
        Intent intent = new Intent(IntentSwitchVariables.Notifications);
        intent.putExtra(IntentSwitchVariables.email,email);
        intent.putExtra(IntentSwitchVariables.sourceClassName,IntentSwitchVariables.Dashboard);
        startActivity(intent);
    }

    public void buttonOnClickEditProfile(View v)
    {
        //intent = new Intent("com.example.cybersafetyapp.Notifications");
        //startActivity(intent);
    }


}
