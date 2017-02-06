package com.example.cybersafetyapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.GregorianCalendar;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity {
    String email;
    String alarmOn = "Turn Alarm On";
    String alarmOff = "Turn Alarm Off";
    Button notificationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        notificationButton = (Button)findViewById(R.id.button_dashboard_check_notifications);
        notificationButton.setText(this.alarmOn);



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
            //setAlarm();

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in class: "+this.getClass().getName()+" Exception:  "+ex.toString());
        }


    }

    public void setAlarm()
    {
        Log.i(UtilityVariables.tag,"Inside setAlarm function");
        Intent notificationIntent = new Intent(this,IntentServiceNotification.class);
        notificationIntent.putExtra(IntentSwitchVariables.email,this.email);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        //Long alertTime = calendar.getTimeInMillis()+10*1000;
        if(!UtilityVariables.isAlarmOn)
        {
            //alarmManager.set(AlarmManager.RTC_WAKEUP,alertTime,PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),10000,PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
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
        Intent intent = new Intent(IntentSwitchVariables.AddNewProfile);
        intent.putExtra(IntentSwitchVariables.sourceClassName,IntentSwitchVariables.Dashboard);
        intent.putExtra(IntentSwitchVariables.email,email);
        startActivity(intent);
    }

    public void buttonOnclickCheckNotifications(View v)
    {
        /*Intent intent = new Intent(IntentSwitchVariables.Notifications);
        intent.putExtra(IntentSwitchVariables.email,email);
        intent.putExtra(IntentSwitchVariables.sourceClassName,IntentSwitchVariables.Dashboard);
        startActivity(intent);*/
        //setAlarm(v);
        setAlarm();
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
