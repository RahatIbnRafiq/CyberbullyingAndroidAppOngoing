package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceServer;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class WaitForToken extends AppCompatActivity implements JsonResultReceiver.Receiver {
    private  String email;
    private String usernameToBeSearched;
    private String osnName;
    private String classname = this.getClass().getSimpleName();

    private boolean checkIfValidIntent(Bundle messages)
    {
        try {
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            this.usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            this.osnName = messages.getString(IntentSwitchVariables.OSN_NAME);
            int requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            return true;
        }
        catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in checkIfValidIntent in class: "+this.classname+" : "+e.toString());
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_token);
        Bundle messages = getIntent().getExtras();
        //DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if(!this.checkIfValidIntent(messages))
        {
            Intent intent = new Intent(this,Dashboard.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
            startActivity(intent);
        }

        if (osnName.contains(IntentSwitchVariables.INSTAGRAM))
        {
            TextView textview1 = (TextView)findViewById(R.id.activity_wait_for_token_textview1);
            textview1.setText(R.string.TokenWaitInstagram);
            String url = messages.getString(IntentSwitchVariables.INSTAGRAM_LOGIN_CODE);
            Log.i(UtilityVariables.tag,this.classname+ " class : Inside onCreate function. the url is: "+url);
            String code = (url != null ? url.split("=") : new String[0])[1];


            JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent intent = new Intent(this, IntentServiceServer.class);
            intent.putExtra(IntentSwitchVariables.SERVER_INSTAGRAM_ACCESS_TOKEN_CODE,code);
            intent.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
            intent.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_ACCESS_TOKEN);
            startService(intent);

        }

    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                String success = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS);
                String message = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE);
                Log.i(UtilityVariables.tag,"response from server when requesting access token: "+message+", "+success);
                /*String accessToken = resultData.getString(IntentSwitchVariables.InstagramAccessToken);
                if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                {
                    databaseHelper.insertAccessTokenValue(DatabaseHelper.NAME_TABLE_GUARDIAN_INFORMATION,this.email,accessToken,DatabaseHelper.NAME_COL_INSTAGRAM_TOKEN);
                    Intent intent = new Intent(WaitForResults.class.getName());
                    intent.putExtra(IntentSwitchVariables.email,this.email);
                    intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,this.usernameToBeSearched);
                    intent.putExtra(IntentSwitchVariables.request,this.requestType);
                    intent.putExtra(IntentSwitchVariables.OSNName,this.osnName);
                    intent.putExtra(IntentSwitchVariables.InstagramAccessToken,accessToken);
                    startActivity(intent);
                }
                else
                {
                    Log.i(UtilityVariables.tag,"something happened while getting requst: "+this.requestType);
                }*/
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                success = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS);
                message = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE);
                Log.i(UtilityVariables.tag,"response from server when requesting access token: "+message+", "+success);
                break;
        }

    }
}
