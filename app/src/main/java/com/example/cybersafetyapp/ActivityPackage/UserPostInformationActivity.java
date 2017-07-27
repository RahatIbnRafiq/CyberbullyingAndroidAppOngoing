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
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONObject;

public class UserPostInformationActivity extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver,
         NavigationView.OnNavigationItemSelectedListener{
    private String email;
    private int requestType;
    private Bundle messages;
    private TableLayout userPostInfoTable;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_post_information);
            setupToolbarMenu();
            setupNavigationDrawerMenu();
            messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            //String userid = messages.getString(IntentSwitchVariables.USERID);

            this.userPostInfoTable = (TableLayout)findViewById(R.id.activity_user_post_information_table);
            this.requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            if(messages.getInt(IntentSwitchVariables.REQUEST) == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION)
            {
                InstagramPostInformation();
            }

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in onCreate function class:"+this.classname+" Exception: "+ex.toString());
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_user_post_information);
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

    private void addPostDataToRowInstagram(JSONArray resultarray) throws Exception
    {
        if(resultarray.length() > 1)
        {
            for(int i=0;i<resultarray.length();i++)
            {
                JSONObject postData = resultarray.getJSONObject(i);
                //Log.i(UtilityVariables.tag,"Single post info for instagram: "+postData.toString());
                TableRow tr_head = new TableRow(this);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));





                Button clickToViewPostsInfo = new Button(this);
                clickToViewPostsInfo.setText(R.string.SeePost);
                clickToViewPostsInfo.setTextColor(Color.WHITE);
                clickToViewPostsInfo.setPadding(20,10,0,10);
                clickToViewPostsInfo.setOnClickListener(this);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(170,TableLayout.LayoutParams.WRAP_CONTENT);
                clickToViewPostsInfo.setLayoutParams(layoutParams);
                tr_head.addView(clickToViewPostsInfo);

                TextView t = new TextView(this);
                t.setText(postData.getJSONObject("likes").getString("count")+" likes");
                t.setTextColor(Color.WHITE);
                //t.setPadding(12,10,2,10);
                layoutParams = new TableRow.LayoutParams(150,TableLayout.LayoutParams.WRAP_CONTENT);
                t.setLayoutParams(layoutParams);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(postData.getJSONObject("comments").getString("count")+" comments");
                t.setTextColor(Color.WHITE);
                //t.setPadding(10,10,2,10);
                layoutParams = new TableRow.LayoutParams(150,TableLayout.LayoutParams.WRAP_CONTENT);
                t.setLayoutParams(layoutParams);
                tr_head.addView(t);

                try{
                    t = new TextView(this);
                    t.setText("Caption:"+postData.getJSONObject("caption").getString("text"));
                    t.setTextColor(Color.WHITE);
                    layoutParams = new TableRow.LayoutParams(350,TableLayout.LayoutParams.WRAP_CONTENT);
                    t.setLayoutParams(layoutParams);
                    //t.setPadding(10,10,2,10);
                    tr_head.addView(t);

                }catch (Exception ex)
                {
                    t = new TextView(this);
                    t.setText(R.string.NoCaption);
                    t.setTextColor(Color.WHITE);
                    //t.setPadding(10,10,2,10);
                    layoutParams = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                    t.setLayoutParams(layoutParams);
                    tr_head.addView(t);
                }



                t = new TextView(this);
                t.setText(postData.getString("id"));
                t.setTextColor(Color.WHITE);
                //t.setPadding(2,10,2,10);
                t.setVisibility(View.INVISIBLE);
                layoutParams = new TableRow.LayoutParams(0,0);
                t.setLayoutParams(layoutParams);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(postData.getString("link"));
                t.setTextColor(Color.WHITE);
                //t.setPadding(2,10,2,10);
                layoutParams = new TableRow.LayoutParams(0,0);
                t.setLayoutParams(layoutParams);
                t.setVisibility(View.INVISIBLE);
                tr_head.addView(t);


                this.userPostInfoTable.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            }
        }
    }

    private void InstagramPostInformation() throws Exception
    {
        JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_POST_INFORMATION_RESULT_JSON));
        JSONArray resultarray = resultjson.getJSONArray("data");
        addPostDataToRowInstagram(resultarray);
    }

    @Override
    public void onClick(View v) {

        Button clickToViewPosts = (Button)v;
        TableRow row = (TableRow)clickToViewPosts.getParent();
        String postid = ((TextView)row.getChildAt(4)).getText().toString();
        if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION)
        {
            JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.POSTID,postid);
            instagramIntentService.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,messages.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
            instagramIntentService.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS);
            startService(instagramIntentService);
        }

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                Intent intent = new Intent(this,PostInformationDetails.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.REQUEST,resultData.getInt(IntentSwitchVariables.REQUEST));
                intent.putExtra(IntentSwitchVariables.POSTID, resultData.getString(IntentSwitchVariables.POSTID));
                if(resultData.getInt(IntentSwitchVariables.REQUEST) == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS)
                {
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON));
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
