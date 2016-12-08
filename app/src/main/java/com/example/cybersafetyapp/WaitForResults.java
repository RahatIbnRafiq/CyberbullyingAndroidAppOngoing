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
    //public String osnName;
    public int requestType;

    private boolean checkIfValidIntent(Bundle messages)
    {
        try {
            this.email = messages.getString(IntentSwitchVariables.email);
            //this.osnName = messages.getString(IntentSwitchVariables.OSNName);
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
        else if (this.requestType == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH)
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            this.usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            String vineAccessToken = messages.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN);

            Intent vineIntentService = new Intent(this, IntentServiceVine.class);
            vineIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            vineIntentService.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED, this.usernameToBeSearched);
            vineIntentService.putExtra(IntentSwitchVariables.request, this.requestType);
            vineIntentService.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN, vineAccessToken);
            startService(vineIntentService);
        }
        else
        {
            Log.i(UtilityVariables.tag,"Something bad happened in "+this.getClass().getName()+" request type : "+this.requestType);
        }

    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                Log.i(UtilityVariables.tag,"Running code intent service");
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                int requesttype = resultData.getInt(IntentSwitchVariables.request);
                if (requesttype == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                {
                    Intent intent = new Intent(SearchResultProfiles.class.getName());
                    intent.putExtra(IntentSwitchVariables.email,this.email);
                    intent.putExtra(IntentSwitchVariables.request,requesttype);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.InstagramAccessToken,resultData.getString(IntentSwitchVariables.InstagramAccessToken));
                    startActivity(intent);
                }
                else if (requesttype == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH)
                {
                    Intent intent = new Intent(SearchResultProfiles.class.getName());
                    intent.putExtra(IntentSwitchVariables.email,this.email);
                    intent.putExtra(IntentSwitchVariables.request,requesttype);
                    intent.putExtra(IntentSwitchVariables.VINE_USER_SEARCH_RESULT_JSON,resultData.getString(IntentSwitchVariables.VINE_USER_SEARCH_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
                    startActivity(intent);
                }
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.i(UtilityVariables.tag,error);
                break;
        }

    }
}
