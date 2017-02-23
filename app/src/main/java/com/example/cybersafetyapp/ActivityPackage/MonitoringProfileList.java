package com.example.cybersafetyapp.ActivityPackage;

import android.app.ProgressDialog;
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

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.HelperClassesPackage.DatabaseWorks;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceInstagram;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceServer;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ErrorMessageVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.ToastMessagesVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Set;

public class MonitoringProfileList extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver, NavigationView.OnNavigationItemSelectedListener{
    //private DatabaseHelper databaseHelper;
    private String email;
    private TableLayout tableSearchResult;
    private String classname = this.getClass().getSimpleName();
    private ProgressDialog progressDialog;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_monitoring_profile_list);
            setupToolbarMenu();
            setupNavigationDrawerMenu();
            this.tableSearchResult = (TableLayout) findViewById(R.id.activity_monitoring_profile_list_table);
            //this.databaseHelper = new DatabaseHelper(this);
            Bundle messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            this.progressDialog = new ProgressDialog(this);

            getMonitoringUsersFromServerInstagram();


        } catch (Exception ex) {
            Log.i(UtilityVariables.tag, "Exception in class: " + this.classname + " onCreate function Exception: " + ex.toString());
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_monitoring_profile_list);
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


    private void getMonitoringUsersFromServerInstagram()
    {
        JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intentServiceServer = new Intent(this, IntentServiceServer.class);
        intentServiceServer.putExtra(IntentSwitchVariables.EMAIL,this.email);
        intentServiceServer.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
        intentServiceServer.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_SERVER_GET_INSTAGRAM_MONITORING_USERS);
        startService(intentServiceServer);

    }


    private void addTableRow(String username,String userid,String OSNName,int i)
    {
        TableRow tr_head = new TableRow(this);
        tr_head.setId(i);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_OSNName = new TextView(this);
        label_OSNName.setText(OSNName);
        label_OSNName.setPadding(2,15,5,15);
        tr_head.addView(label_OSNName);


        TextView label_username = new TextView(this);
        label_username.setText(username);
        label_username.setTextColor(Color.WHITE);
        label_username.setPadding(0,15,2,15);
        tr_head.addView(label_username);

        Button clickToViewPosts = new Button(this);
        clickToViewPosts.setText(R.string.Details);
        clickToViewPosts.setTextColor(Color.WHITE);
        clickToViewPosts.setPadding(0,15,0,15);
        clickToViewPosts.setOnClickListener(this);
        tr_head.addView(clickToViewPosts);

        Button removeUser = new Button(this);
        removeUser.setText(R.string.Remove);
        removeUser.setTextColor(Color.WHITE);
        removeUser.setPadding(0,15,2,15);
        removeUser.setOnClickListener(this);
        tr_head.addView(removeUser);



        TextView label_userid = new TextView(this);
        label_userid.setText(userid);
        label_userid.setVisibility(View.INVISIBLE);
        label_userid.setPadding(0,15,0,15);
        tr_head.addView(label_userid);

        this.tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }


    private void removeMonitoringUser(Button buttonClicked)
    {
        //Log.i(UtilityVariables.tag,this.classname+" : Remove user button was clicked.");
        TableRow row = (TableRow) buttonClicked.getParent();
        TextView osnName = (TextView) row.getChildAt(0);
        TextView username = (TextView) row.getChildAt(1);
        TextView userid = (TextView) row.getChildAt(4);
        if (osnName.getText().toString().equals("Instagram"))
        {
        }
        Log.i(UtilityVariables.tag,this.classname+" remove button has been clicked for username:"+username.getText().toString());
        /*Intent intent = new Intent(this,Dashboard.class);
        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
        startActivity(intent);*/
    }

    private void getUserSocialNetworkInformation(Button buttonClicked)
    {
        TableRow row = (TableRow) buttonClicked.getParent();
        TextView osnName = (TextView) row.getChildAt(0);
        TextView userid = (TextView) row.getChildAt(4);
        DatabaseWorks databaseWorks = DatabaseWorks.getInstance(getApplicationContext());
        if (osnName.getText().toString().equals("Instagram")) {
            String accessToken = databaseWorks.getAccessTokenForGuardian(this.email,DatabaseWorks.NAME_COL_INSTAGRAM_TOKEN);
            if(accessToken == null || accessToken.length() < 1)
            {
                Intent intent = new Intent(this,InstagramLogin.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.USERID_GET_DETAIL,userid.getText().toString());
                intent.putExtra(IntentSwitchVariables.REQUEST,IntentSwitchVariables.REQUEST_INSTAGRAM_ACCESS_TOKEN);
                intent.putExtra(IntentSwitchVariables.OSN_NAME,IntentSwitchVariables.INSTAGRAM);
                startActivity(intent);


            }
            else
            {
                JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
                mReceiver.setReceiver(this);
                Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
                instagramIntentService.putExtra(IntentSwitchVariables.USERID, userid.getText().toString());
                instagramIntentService.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, accessToken);
                instagramIntentService.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
                instagramIntentService.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL);
                startService(instagramIntentService);

            }


        }
    }


    @Override
    public void onClick(View v) {
        Button buttonClicked = (Button)v;
        if(buttonClicked.getText().toString().equals("Remove"))
        {
            removeMonitoringUser(buttonClicked);
        }
        else {
            this.progressDialog.show();
            getUserSocialNetworkInformation(buttonClicked);
        }

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                int requesttype = resultData.getInt(IntentSwitchVariables.REQUEST);
                if(requesttype  == IntentSwitchVariables.REQUEST_SERVER_GET_INSTAGRAM_MONITORING_USERS)
                {
                    this.progressDialog.dismiss();
                    if(resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS).equals("success")) {
                        //Log.i(UtilityVariables.tag, resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE));
                        //Log.i(UtilityVariables.tag, resultData.getString(IntentSwitchVariables.JSON_RESULT));
                        try {
                            String jsonResult = resultData.getString(IntentSwitchVariables.JSON_RESULT);
                            JSONArray jsonArray = new JSONArray(jsonResult);
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                addTableRow(jsonObject.optString("username"),jsonObject.optString("userid"),
                                        resultData.getString(IntentSwitchVariables.OSN_NAME),i);
                            }
                        }catch (Exception ex)
                        {
                            Toast.makeText(this, ErrorMessageVariables.UNEXPECTED_DATABASE_ERROR,Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(this, resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE),Toast.LENGTH_SHORT).show();
                    }
                }

                else if(requesttype  == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
                {
                    Intent intent = new Intent(this,UserSocialNetworkInformationDetail.class);
                    intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                    intent.putExtra(IntentSwitchVariables.REQUEST,resultData.getInt(IntentSwitchVariables.REQUEST));
                    intent.putExtra(IntentSwitchVariables.USERID, resultData.getString(IntentSwitchVariables.USERID));
                    Log.i(UtilityVariables.tag,this.classname+" response user details: "+resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                    startActivity(intent);
                }
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
