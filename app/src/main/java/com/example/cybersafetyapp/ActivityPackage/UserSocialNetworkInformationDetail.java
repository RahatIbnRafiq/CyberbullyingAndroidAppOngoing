package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceInstagram;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONObject;

import java.util.Iterator;

public class UserSocialNetworkInformationDetail extends AppCompatActivity implements JsonResultReceiver.Receiver,
        NavigationView.OnNavigationItemSelectedListener{
    private int requestType;
    private Bundle messages;
    private TableLayout tableSearchResult;
    private String userid,email;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_social_network_information_detail);
        setupToolbarMenu();
        setupNavigationDrawerMenu();
        try{
            messages = getIntent().getExtras();
            this.requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            tableSearchResult = (TableLayout) findViewById(R.id.activity_user_social_network_information_detail_TableUSerInformationDetail);
            this.userid = messages.getString(IntentSwitchVariables.USERID);
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
            {
                showInstagramUserDetailInformation();
            }


        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception onCreate function in class:"+this.classname+" Exception: "+ex.toString());
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_user_social_network_information_detail);
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

    public void buttonOnClickGetUserPostInformation(View v)
    {
        if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
        {
            Log.i(UtilityVariables.tag,this.classname+ " :Getting posts for this user from instagram");
            JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.USERID,this.userid);
            instagramIntentService.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,messages.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
            instagramIntentService.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION);
            startService(instagramIntentService);
        }
    }

    private void addDataToRow(JSONObject userdata)
    {

        Iterator<String> iter = userdata.keys();
        while (iter.hasNext()) {

            String key = iter.next();
            try {
                TableRow tr_head = new TableRow(this);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                String value = userdata.get(key).toString();
                //Log.i(UtilityVariables.tag,"key: "+key+" value: "+value);

                TextView t = new TextView(this);
                t.setText(key);
                t.setTextColor(Color.WHITE);
                t.setPadding(0,10,5,10);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(value);
                t.setTextColor(Color.WHITE);
                t.setPadding(0,10,5,10);
                tr_head.addView(t);
                tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            } catch (Exception ex) {
                Log.i(UtilityVariables.tag,this.classname+" :addDataToRow function: exception while ietaring through json object: "+ex.toString());
            }
        }



    }


    private void showInstagramUserDetailInformation()
    {
        try {
            JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON));
            JSONObject userdata = resultjson.getJSONObject("data");
            this.requestType = IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL;
            addDataToRow(userdata);
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+":showInstagramUserDetailInformation function: Something unexpected happened in showInstagramUserDetailInformation fucntion class: "+this.getClass().getName()+" :"+ex.toString());
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                Intent intent = new Intent(this,UserPostInformationActivity.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.REQUEST,resultData.getInt(IntentSwitchVariables.REQUEST));
                intent.putExtra(IntentSwitchVariables.USERID, resultData.getString(IntentSwitchVariables.USERID));
                if(resultData.getInt(IntentSwitchVariables.REQUEST) == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION)
                {
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_POST_INFORMATION_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_POST_INFORMATION_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                }
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                break;
        }
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
            Toast.makeText(this," Use the navigate menu to navigate please ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        Intent intent;

        switch (menuItem.getItemId())
        {
            case R.id.MenuAboutUs:
                intent = new Intent(this, Aboutus.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuLogIn:
                intent = new Intent(this, LogIn.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuRegister:
                intent = new Intent(this, Register.class);
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
            case R.id.MenuTurnAlarmOn:
                break;

        }
        return false;
    }
}
