package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        try
        {
            Bundle messages = getIntent().getExtras();
            String sourceClassName = messages.getString(IntentSwitchVariables.sourceClassName);
            this.email = messages.getString(IntentSwitchVariables.email);
            if (sourceClassName.equals(LogIn.class.getName()))
            {
                Toast.makeText(this, ToastMessagesVariables.WelcomeToDashboard + this.email.substring(0, email.indexOf("@")), Toast.LENGTH_SHORT).show();
            }
            else if (sourceClassName.equals(SearchResultProfiles.class.getName()))
            {
                //Toast.makeText(this, ToastMessagesVariables.YouAreNowMonitoring + messages.getString(IntentSwitchVariables.toBeMonitored), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in class: "+this.getClass().getName()+" Exception:  "+ex.toString());
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

    public void buttonOnClickCheckMonitoringProfile(View v)
    {
        Intent intent = new Intent(MonitoringProfileList.class.getName());
        intent.putExtra(IntentSwitchVariables.email,this.email);
        startActivity(intent);
    }


}
