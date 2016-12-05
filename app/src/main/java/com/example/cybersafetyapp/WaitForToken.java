package com.example.cybersafetyapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WaitForToken extends AppCompatActivity implements JsonResultReceiver.Receiver {
    private JsonResultReceiver mReceiver;
    public String email;
    public String usernameToBeSearched;
    public String osnName;
    public int requestType;

    private boolean checkIfValidIntent(Bundle messages)
    {
        try {
            this.email = messages.getString(IntentSwitchVariables.email);
            this.usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
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
        setContentView(R.layout.activity_wait_for_token);
        Bundle messages = getIntent().getExtras();
        if(!this.checkIfValidIntent(messages))
        {
            Intent intent = new Intent(Dashboard.class.getName());
            intent.putExtra(IntentSwitchVariables.email,this.email);
            startActivity(intent);
        }

        if (osnName.contains(IntentSwitchVariables.INSTAGRAM))
        {
            TextView textview1 = (TextView)findViewById(R.id.activity_wait_for_token_textview1);
            textview1.setText("Please wait while we collect your access token from instagram.");
            String url = messages.getString(IntentSwitchVariables.InstagramLoginCode);
            Log.i(UtilityVariables.tag,"Inside waitfortoken class. the url is: "+url);


            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.url,url);
            instagramIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.request, IntentServiceInstagram.GET_TOKEN);
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
                if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                {
                    Log.i(UtilityVariables.tag,"Yay! got the access token for Instagram. Finally! "+accessToken);
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
                }
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.i(UtilityVariables.tag,error);
                break;
        }

    }
}
