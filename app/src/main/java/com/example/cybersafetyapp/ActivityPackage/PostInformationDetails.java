package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONArray;
import org.json.JSONObject;

public class PostInformationDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String email;
    private int requestType;
    private TableLayout tablePostDetails;
    private Bundle messages;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post_information_details);
            setupToolbarMenu();
            setupNavigationDrawerMenu();
            messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            this.requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            this.tablePostDetails = (TableLayout)findViewById(R.id.activity_post_information_details_table);
            if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS)
            {
                showInstagramPostDetails();
            }

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+": Exception onCreate: "+ex.toString());
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_post_information_details);
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

    private void addInstagramDataToTable(JSONArray resultArray) throws Exception
    {
        if(resultArray.length() > 0)
        {
            for(int i=0; i< resultArray.length();i++)
            {
                TableRow tr_head = new TableRow(this);
                tr_head.setId(i);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                TextView t = new TextView(this);
                t.setText(resultArray.getJSONObject(i).getString("text"));
                t.setPadding(2,15,5,15);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(" BY: "+resultArray.getJSONObject(i).getJSONObject("from").getString("username"));
                t.setPadding(2,15,5,15);
                tr_head.addView(t);
                this.tablePostDetails.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }


    private void showInstagramPostDetails() throws  Exception
    {
        JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON));
        JSONArray resultArray = resultjson.getJSONArray("data");
        addInstagramDataToTable(resultArray);
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
