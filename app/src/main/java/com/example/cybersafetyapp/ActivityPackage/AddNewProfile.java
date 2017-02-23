package com.example.cybersafetyapp.ActivityPackage;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class AddNewProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private String email;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_profile);

        Bundle messages = getIntent().getExtras();
        /*if(messages == null ||
                messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME) == null ||
                !messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME).equals(Dashboard.class.getName()))
        {
            Intent intent = new Intent(this,Dashboard.class);

            try{
                this.email = messages.getString(IntentSwitchVariables.EMAIL);
                intent.putExtra(IntentSwitchVariables.EMAIL,email);

            }catch(Exception e)
            {
                Log.i(UtilityVariables.tag,this.classname+": add new profile did not get email.");
            }

            intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
            startActivity(intent);
        }*/
        if(messages != null)
        {
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            setupToolbarMenu();
            setupNavigationDrawerMenu();
        }
        else
        {
            Intent intent = new Intent(this,Dashboard.class);
            startActivity(intent);
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_add_new_profile);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void setupToolbarMenu()
    {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("Menu");
    }

    private void searchUserByUserName()
    {
        Intent intent = new Intent(this,SearchByUserName.class);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
        intent.putExtra(IntentSwitchVariables.EMAIL,email);
        startActivity(intent);
    }

    public void buttonOnClickSearchByUserName(View v)
    {
        searchUserByUserName();
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
                searchUserByUserName();
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
