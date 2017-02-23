package com.example.cybersafetyapp.ActivityPackage;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseWorks;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceServer;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ErrorMessageVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class LogIn extends AppCompatActivity implements JsonResultReceiver.Receiver,View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener{
    private String email,password;
    private ProgressDialog progressDialog;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setupToolbarMenu();
        setupNavigationDrawerMenu();
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

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_log_in);
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

    private String checkValidLogin()
    {
        if (this.email.length()< UtilityVariables.VALID_EMAIL_LENGTH)
            return ErrorMessageVariables.NOT_VALID_EMAIL;
        if (this.password.length() < UtilityVariables.VALID_PASSWORD_LENGTH)
            return ErrorMessageVariables.NOT_VALID_PASSWORD;
        return UtilityVariables.VALID;
    }

    public void onClick(View v)
    {
        onClickLoginButton(v);
    }



    public void onClickLoginButton(View v)
    {
        try {
            this.email = ((EditText)findViewById(R.id.editext_login_emailid)).getText().toString();
            this.password = ((EditText)findViewById(R.id.edittext_login_password)).getText().toString();
            String validMessage = this.checkValidLogin();
            if (validMessage.equals(UtilityVariables.VALID)) {
                this.progressDialog = new ProgressDialog(this);
                this.progressDialog.show();

                JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
                mReceiver.setReceiver(this);
                Intent intentServiceServer = new Intent(this, IntentServiceServer.class);
                intentServiceServer.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intentServiceServer.putExtra(IntentSwitchVariables.PASSWORD,this.password);
                intentServiceServer.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
                intentServiceServer.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_LOGIN);
                startService(intentServiceServer);

            } else {
                this.progressDialog.dismiss();
                Toast.makeText(this, validMessage, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,LogIn.class);
                startActivity(intent);


            }
        }catch (Exception e)
        {
            this.progressDialog.dismiss();
            Toast.makeText(LogIn.this, ErrorMessageVariables.LOGIN_UNEXPECTED_ERROR, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,LogIn.class);
            startActivity(intent);

        }

    }



    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                this.progressDialog.dismiss();
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                this.progressDialog.dismiss();
                String success = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS);
                String message = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE);
                if (success!=null && message != null)
                {
                    if(success.equals("success"))
                    {
                        DatabaseWorks databaseworks = DatabaseWorks.getInstance(getApplicationContext());
                        databaseworks.insertGuardianData(this.email);
                        UtilityVariables.isLoggedIn = true;
                        Intent intent = new Intent(this,Dashboard.class);
                        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                        startActivity(intent);

                        //databaseworks.printAllDataFromTable(DatabaseWorks.NAME_TABLE_GUARDIAN_INFORMATION);

                    }
                    else
                    {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this,LogIn.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    Log.i(UtilityVariables.tag,this.classname+" success and message came back as null");
                }

                break;
            case IntentServiceGetJson.STATUS_ERROR:
                this.progressDialog.dismiss();
                Toast.makeText(LogIn.this, ErrorMessageVariables.LOGIN_UNEXPECTED_ERROR, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,LogIn.class);
                startActivity(intent);
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
