package com.example.cybersafetyapp.ActivityPackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cybersafetyapp.IntentServicePackage.IntentServiceNotification;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ToastMessagesVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private String email;
    private String alarmOn = "Turn Alarm On";
    private String alarmOff = "Turn Alarm Off";
    private String classname = this.getClass().getSimpleName();
    private Button notificationButton;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.notificationButton = (Button)findViewById(R.id.button_dashboard_check_notifications);

        try
        {
            Bundle messages = getIntent().getExtras();
            String sourceClassName = messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME);
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            setupToolbarMenu();
            setupNavigationDrawerMenu();
            if (sourceClassName.equals(LogIn.class.getName()))
            {
                Toast.makeText(this, ToastMessagesVariables.WELCOME_TO_DASHBOARD + this.email.substring(0, email.indexOf("@")), Toast.LENGTH_SHORT).show();
            }
            else if (sourceClassName.equals(SearchResultProfiles.class.getName()))
            {
                Toast.makeText(this, ToastMessagesVariables.YOU_ARE_NOW_MONITORING + messages.getString(IntentSwitchVariables.TO_BE_MONITORED_USERIDS), Toast.LENGTH_SHORT).show();
            }
            setAlarmText();

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+":Exception in onCreate function: "+ex.toString());
        }



    }

    private void setupNavigationDrawerMenu()
    {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        if(UtilityVariables.isLoggedIn) {
            navigationView.inflateMenu(R.menu.menu_loggedin);
            //TextView emailTextView = (TextView)findViewById(R.id.txvEmail);
            //emailTextView.setText(this.email);
        }
        else
        {
            navigationView.inflateMenu(R.menu.menu_loggedout);
        }

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_dashboard);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupToolbarMenu()
    {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("Menu");
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
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*60,PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            UtilityVariables.isAlarmOn = true;
        }
        else
        {
            UtilityVariables.isAlarmOn = false;
            alarmManager.cancel(PendingIntent.getService(this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT));
        }
        setAlarmText();
    }

    private void setAlarmText()
    {
        if(UtilityVariables.isAlarmOn)
        {
            this.notificationButton.setText(this.alarmOff);
        }
        else
        {
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

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else {
            Intent intent = new Intent(this, Dashboard.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
            intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        closeDrawer();
        Intent intent;

        switch (menuItem.getItemId())
        {
            case R.id.MenuAboutUs:
                intent = new Intent(this, Aboutus.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuLogout:
                UtilityVariables.isLoggedIn=false;
                intent = new Intent(this, WelcomeToCybersafetyApp.class);
                startActivity(intent);
                break;
            case R.id.MenuAddUser:
                break;
            case R.id.MenuDashboard:
                intent = new Intent(this, Dashboard.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuEditProfile:
                break;
            case R.id.MenuSettings:
                break;
            case R.id.MenuCheckMonitoringProfiles:
                intent = new Intent(this, MonitoringProfileList.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                startActivity(intent);
                break;
            case R.id.MenuRegister:
                break;
            case R.id.MenuTurnAlarmOn:
                break;
            case R.id.MenuLogIn:
                break;
        }
        return false;
    }
}
