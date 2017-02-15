package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceVine;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class VineLogin extends AppCompatActivity implements JsonResultReceiver.Receiver {

    public Bundle messages;
    public int requestType;
    public String userToBeSearched,email;
    private JsonResultReceiver mReceiver;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vine_login);
        databaseHelper = new DatabaseHelper(this);
        try {
            messages = getIntent().getExtras();
            requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            email = messages.getString(IntentSwitchVariables.EMAIL);
            userToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.getClass().toString()+" Exception: "+ex.toString());
        }
    }


    public void onclickLogInButton(View v)
    {
        TextView vineEmail = (TextView)findViewById(R.id.activity_vine_login_email);
        TextView vinePassword = (TextView)findViewById(R.id.activity_vine_login_password);
        if(vineEmail.getText().toString().length() > 0 && vinePassword.getText().toString().length() > 0)
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent vineIntentService = new Intent(this, IntentServiceVine.class);
            vineIntentService.putExtra(IntentSwitchVariables.EMAIL,vineEmail.getText().toString());
            vineIntentService.putExtra(IntentSwitchVariables.PASSWORD,vinePassword.getText().toString());
            vineIntentService.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                //Log.i(UtilityVariables.tag,"access token back to the login class "+resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));

                Intent intent = new Intent(this,WaitForResults.class);
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                break;
        }
    }
}
