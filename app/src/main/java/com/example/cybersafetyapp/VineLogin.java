package com.example.cybersafetyapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class VineLogin extends AppCompatActivity implements JsonResultReceiver.Receiver{

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
            requestType = messages.getInt(IntentSwitchVariables.request);
            email = messages.getString(IntentSwitchVariables.email);
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
            vineIntentService.putExtra(IntentSwitchVariables.email,vineEmail.getText().toString());
            vineIntentService.putExtra(IntentSwitchVariables.password,vinePassword.getText().toString());
            vineIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            vineIntentService.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_VINE_ACCESS_TOKEN);
            startService(vineIntentService);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                //Log.i(UtilityVariables.tag,"access token back to the login class "+resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
                databaseHelper.insertAccessTokenValue(DatabaseHelper.NAME_TABLE_GUARDIAN_INFORMATION,this.email,resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN),DatabaseHelper.NAME_COL_VINE_TOKEN);
                Intent intent = new Intent(WaitForResults.class.getName());
                intent.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
                intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,this.userToBeSearched);
                intent.putExtra(IntentSwitchVariables.request,this.requestType);
                intent.putExtra(IntentSwitchVariables.email,this.email);
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                break;
        }
    }
}
