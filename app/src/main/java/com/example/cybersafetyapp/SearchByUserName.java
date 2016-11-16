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
    private String tag = "cybersafetyapp";
    private String logmsg = this.getClass().getName().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_user_name);


    }

    public void buttonOnclickSearchByUserName(View v)
    {
        RadioButton radioVine = (RadioButton)findViewById(R.id.radio_searchbyusername_vine);
        String userToSearch = ((EditText)findViewById(R.id.edittext_searchbyusername_username)).getText().toString().trim();
        userToSearch = userToSearch.replace(" ","%20");

        if(radioVine.isChecked())
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            String urlVineUserSearch = "https://api.vineapp.com/search/users/"+userToSearch;
            Intent jsonIntentService = new Intent(this, IntentServiceGetJson.class);
            jsonIntentService.putExtra("url",urlVineUserSearch);
            jsonIntentService.putExtra("receiver", mReceiver);
            startService(jsonIntentService);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                String[] results = resultData.getStringArray("jsonResult");
                for (int i = 0; i < results.length; i++)
                {
                    Log.i(tag,logmsg+"->username:"+results[i]);
                }
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.i(tag,logmsg+"Error while getting the usernames in intent:"+error);
                break;
        }

    }
}
