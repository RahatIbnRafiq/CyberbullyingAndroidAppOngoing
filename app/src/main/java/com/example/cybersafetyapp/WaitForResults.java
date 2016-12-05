package com.example.cybersafetyapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WaitForResults extends AppCompatActivity implements JsonResultReceiver.Receiver {
    private JsonResultReceiver mReceiver;
    public String email;
    public String usernameToBeSearched;
    public String osnName;
    public int requestType;

    private boolean checkIfValidIntent(Bundle messages)
    {
        try {
            this.email = messages.getString(IntentSwitchVariables.email);
            this.osnName = messages.getString(IntentSwitchVariables.OSNName);
            this.requestType = messages.getInt(IntentSwitchVariables.request);
            return true;
        }
        catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in checkIfValidIntent in class: "+this.getClass().getName()+" : "+e.toString());
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_results);
        Bundle messages = getIntent().getExtras();
        if(!this.checkIfValidIntent(messages))
        {
            Intent intent = new Intent(Dashboard.class.getName());
            intent.putExtra(IntentSwitchVariables.email,this.email);
            startActivity(intent);
        }

        if (this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            this.usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            String InstagramAcessToken = messages.getString(IntentSwitchVariables.InstagramAccessToken);

            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED, this.usernameToBeSearched);
            instagramIntentService.putExtra(IntentSwitchVariables.request, this.requestType);
            instagramIntentService.putExtra(IntentSwitchVariables.InstagramAccessToken, InstagramAcessToken);
            startService(instagramIntentService);

        }
        else
        {
            Log.i(UtilityVariables.tag,"class name did not match while coming to waitfortoken class: "+osnName);
        }

    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                Log.i(UtilityVariables.tag,"Running code intent service");
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                String accessToken = resultData.getString(IntentSwitchVariables.InstagramAccessToken);
                Log.i(UtilityVariables.tag,"Yay! got the access token. Finally! "+accessToken);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.i(UtilityVariables.tag,error);
                break;
        }

    }
}
