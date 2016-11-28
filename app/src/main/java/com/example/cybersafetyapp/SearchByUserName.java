package com.example.cybersafetyapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class SearchByUserName extends AppCompatActivity implements JsonResultReceiver.Receiver{


    private JsonResultReceiver mReceiver;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_user_name);
        Bundle messages = getIntent().getExtras();
        if(messages == null ||
                messages.getString(IntentSwitchVariables.sourceClassName) == null ||
                !messages.getString(IntentSwitchVariables.sourceClassName).equals(AddNewProfile.class.getName()))
        {
            Intent intent = new Intent(IntentSwitchVariables.Dashboard);

            try{
                email = messages.getString(IntentSwitchVariables.email);
                intent.putExtra(IntentSwitchVariables.email,email);

            }catch(Exception e)
            {
                Log.i(UtilityVariables.tag,UtilityVariables.AddNewProfile+" search by user name did not get email.");
            }

            intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
            startActivity(intent);
        }
        email = messages.getString(IntentSwitchVariables.email);
    }


    public void buttonOnclickSearchByUserName(View v)
    {
        RadioButton radioVine = (RadioButton)findViewById(R.id.radio_searchbyusername_vine);
        RadioButton radioInstagram = (RadioButton)findViewById(R.id.radio_searchbyusername_instagram);
        String userToSearch = ((EditText)findViewById(R.id.edittext_searchbyusername_username)).getText().toString().trim();
        userToSearch = userToSearch.replace(" ","%20");

        if(radioVine.isChecked())
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            String urlVineUserSearch = "https://api.vineapp.com/search/users/"+userToSearch;
            Intent jsonIntentService = new Intent(this, IntentServiceGetJson.class);
            jsonIntentService.putExtra(IntentSwitchVariables.url,urlVineUserSearch);
            jsonIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            jsonIntentService.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_VINE_USER_SEARCH);
            jsonIntentService.putExtra(IntentSwitchVariables.OSNName, IntentSwitchVariables.Vine);
            startService(jsonIntentService);
        }

        else if (radioInstagram.isChecked())
        {
            
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                Toast.makeText(this,ErrorMessageVariables.GettingUsers,Toast.LENGTH_SHORT).show();
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                String[] results = resultData.getStringArray(IntentSwitchVariables.JsonResult);
                Intent intent = new Intent(IntentSwitchVariables.SearchResultProfiles);
                intent.putExtra(IntentSwitchVariables.SearchResult,results);
                intent.putExtra(IntentSwitchVariables.request, resultData.getInt(IntentSwitchVariables.request));
                intent.putExtra(IntentSwitchVariables.email,this.email);
                intent.putExtra(IntentSwitchVariables.OSNName,resultData.getString(IntentSwitchVariables.OSNName));
                intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this,ErrorMessageVariables.ErrorWhileGettingUsers,Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
