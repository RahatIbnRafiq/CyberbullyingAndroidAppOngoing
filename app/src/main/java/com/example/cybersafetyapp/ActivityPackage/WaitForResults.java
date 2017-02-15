package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceInstagram;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class WaitForResults extends AppCompatActivity implements JsonResultReceiver.Receiver {
    private  String email;
    private int requestType;
    private String classname = this.getClass().getSimpleName();

    private boolean checkIfValidIntent(Bundle messages)
    {
        try {
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            //this.osnName = messages.getString(IntentSwitchVariables.OSNName);
            this.requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            return true;
        }
        catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.classname+ ": Exception in checkIfValidIntent in class: "+" : "+e.toString());
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
            Intent intent = new Intent(this,Dashboard.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
            startActivity(intent);
        }

        if (this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
        {
            JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            String usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            String InstagramAcessToken = messages.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN);

            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED, usernameToBeSearched);
            instagramIntentService.putExtra(IntentSwitchVariables.REQUEST, this.requestType);
            instagramIntentService.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, InstagramAcessToken);
            startService(instagramIntentService);

        }
        else
        {
            Log.i(UtilityVariables.tag,"Something bad happened onCreate function in "+this.classname+" request type : "+this.requestType);
        }

    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                Log.i(UtilityVariables.tag,"Running code intent service");
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                int requesttype = resultData.getInt(IntentSwitchVariables.REQUEST);
                if (requesttype == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                {
                    Intent intent = new Intent(this,SearchResultProfiles.class);
                    intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                    intent.putExtra(IntentSwitchVariables.REQUEST,requesttype);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
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
