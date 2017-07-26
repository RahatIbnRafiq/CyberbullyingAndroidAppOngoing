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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.HelperClassesPackage.DatabaseWorks;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;


public class SearchByUserName extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    private String email;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    public EditText searchByUserNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_user_name);
        Bundle messages = getIntent().getExtras();
        this.searchByUserNameEditText = (EditText)findViewById(R.id.edittext_searchbyusername_username);
        /*if(messages == null ||
                messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME) == null ||
                (!messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME).equals(AddNewProfile.class.getName())) ||
                (!messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME).equals(SearchResultProfiles.class.getName()))
                )
        {
            Intent intent = new Intent(this,Dashboard.class);

            try{
                assert messages != null;
                email = messages.getString(IntentSwitchVariables.EMAIL);
                intent.putExtra(IntentSwitchVariables.EMAIL,email);

            }catch(Exception e)
            {
                Log.i(UtilityVariables.tag,this.classname+" oncreate function search by user name did not get email.");
            }

            intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
            startActivity(intent);
        }*/
        email = messages.getString(IntentSwitchVariables.EMAIL);
        setupToolbarMenu();
        setupNavigationDrawerMenu();
        this.searchByUserNameEditText.setOnClickListener(this);
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_search_by_user_name);
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
                UtilityVariables.isLoggedIn = false;
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




    public void buttonOnclickSearchByUserName(View v)
    {
        RadioButton radioInstagram = (RadioButton)findViewById(R.id.radio_searchbyusername_instagram);
        String userToSearch = ((EditText)findViewById(R.id.edittext_searchbyusername_username)).getText().toString().trim();

        if (radioInstagram.isChecked())
        {
            try {
                DatabaseWorks databaseWorks = DatabaseWorks.getInstance(this);
                String token = databaseWorks.getAccessTokenForGuardian(this.email,DatabaseWorks.NAME_COL_INSTAGRAM_TOKEN);
                //databaseWorks.printAllDataFromTable(DatabaseWorks.NAME_TABLE_GUARDIAN_INFORMATION);
                Intent intent;
                if(token == null || token.length() < 1)
                {
                    intent = new Intent(this,InstagramLogin.class);
                }
                else
                {
                    intent = new Intent(this,WaitForResults.class);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,token);
                }
                intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,userToSearch);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH);
                startActivity(intent);

            }catch (Exception ex)
            {
                Log.i(UtilityVariables.tag,ex.toString());
            }

        }
    }

    @Override
    public void onClick(View v) {
        this.searchByUserNameEditText.setText("");
    }
}
