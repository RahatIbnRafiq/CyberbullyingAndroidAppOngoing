package com.example.cybersafetyapp.ActivityPackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.cybersafetyapp.IntentServicePackage.IntentServiceNotification;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ToastMessagesVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity {
    private String email;
    private String alarmOn = "Turn Alarm On";
    private String alarmOff = "Turn Alarm Off";
    private String classname = this.getClass().getSimpleName();
    private Button notificationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.notificationButton = (Button)findViewById(R.id.button_dashboard_check_notifications);
        this.notificationButton.setText(this.alarmOn);



        try
        {
            Bundle messages = getIntent().getExtras();
            String sourceClassName = messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME);
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            if (sourceClassName.equals(LogIn.class.getName()))
            {
                Toast.makeText(this, ToastMessagesVariables.WELCOME_TO_DASHBOARD + this.email.substring(0, email.indexOf("@")), Toast.LENGTH_SHORT).show();
            }
            else if (sourceClassName.equals(SearchResultProfiles.class.getName()))
            {
                Toast.makeText(this, ToastMessagesVariables.YOU_ARE_NOW_MONITORING + messages.getString(IntentSwitchVariables.TO_BE_MONITORED_USERIDS), Toast.LENGTH_SHORT).show();
            }
            //setAlarm();

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+":Exception in onCreate function: "+ex.toString());
        }


    }

    private void setAlarm()
    {
        Log.i(UtilityVariables.tag,this.classname+": Inside setAlarm function");
        Intent notificationIntent = new Intent(this,IntentServiceNotification.class);
        notificationIntent.putExtra(IntentSwitchVariables.EMAIL,this.email);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Long alertTime = calendar.getTimeInMillis()+10*1000;
        if(!UtilityVariables.isAlarmOn)
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP,alertTime,PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),10000,PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            UtilityVariables.isAlarmOn = true;
            this.notificationButton.setText(this.alarmOff);
        }
        else
        {
            UtilityVariables.isAlarmOn = false;
            alarmManager.cancel(PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            this.notificationButton.setText(this.alarmOn);
        }
    }

    public void buttonOnClickAddNewProfile(View v)
    {
        Intent intent = new Intent(this,AddNewProfile.class);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,Dashboard.class.getName());
        intent.putExtra(IntentSwitchVariables.EMAIL,email);
        startActivity(intent);
    }

    public void buttonOnclickCheckNotifications(View v)
    {
        setAlarm();
    }

    public void buttonOnClickEditProfile(View v)
    {
    }

    public void buttonOnClickCheckMonitoringProfile(View v)
    {
        Intent intent = new Intent(this,MonitoringProfileList.class);
        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
        startActivity(intent);
    }


}
