package com.example.cybersafetyapp;

import android.content.Intent;
import android.database.Cursor;
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
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_user_name);
        Bundle messages = getIntent().getExtras();
        databaseHelper = new DatabaseHelper(this);
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
        //userToSearch = userToSearch.replace(" ","%20");
        //userToSearch = userToSearch.replace(".","_");


        if(radioVine.isChecked())
        {
            String token = databaseHelper.getAccessTokenForGuardian(this.email,DatabaseHelper.NAME_COL_VINE_TOKEN);
            Intent intent;
            if(token.length() < 1)
            {
                intent = new Intent(this, VineLogin.class);
            }
            else
            {
                intent = new Intent(WaitForResults.class.getName());
                intent.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,token);
            }

            intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,userToSearch);
            intent.putExtra(IntentSwitchVariables.email,this.email);
            intent.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_VINE_USER_SEARCH);
            startActivity(intent);

        }

        else if (radioInstagram.isChecked())
        {

            String token = databaseHelper.getAccessTokenForGuardian(this.email,DatabaseHelper.NAME_COL_INSTAGRAM_TOKEN);
            Intent intent;
            if(token.length() < 1)
            {
                intent = new Intent(this, InstagramLogin.class);
            }
            else
            {
                intent = new Intent(WaitForResults.class.getName());
                intent.putExtra(IntentSwitchVariables.InstagramAccessToken,token);
            }
            intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,userToSearch);
            intent.putExtra(IntentSwitchVariables.email,this.email);
            intent.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH);
            startActivity(intent);
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
