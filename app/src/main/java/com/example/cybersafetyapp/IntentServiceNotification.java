package com.example.cybersafetyapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;


public class IntentServiceNotification extends IntentService {
    private static DatabaseHelper databaseHelper;
    String email;


    public IntentServiceNotification() {
        super("IntentServiceNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle messages = intent.getExtras();
        Log.i(UtilityVariables.tag,"Inside "+getClass().getSimpleName()+" email is: "+messages.getString(IntentSwitchVariables.email));
        this.email = messages.getString(IntentSwitchVariables.email);
        notificationCheckForInstagram();


    }

    private void notificationCheckForInstagram()
    {
        ArrayList<String> useridList = getUsersFromDB(DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE);
        String accessToken = databaseHelper.getAccessTokenForGuardian(this.email,DatabaseHelper.NAME_COL_INSTAGRAM_TOKEN);
        APIWorks apiworks = APIWorks.getInstance();
        for(String userid:useridList)
        {
            Log.i(UtilityVariables.tag,"monitoring Instagram userid: "+userid);
            apiworks.instagramUserPostInformation(userid,accessToken);
        }



    }


    private ArrayList<String> getUsersFromDB(String tableName)
    {
        ArrayList<String> useridList = new ArrayList<>();
        this.databaseHelper = new DatabaseHelper(this);
        Hashtable<String,String> users = this.databaseHelper.getMonitoringInformationDetailByGuardianEmail(tableName,this.email);
        Set<String> keys = users.keySet();
        for(String key: keys){
            useridList.add(key.toString());
        }

        return useridList;
    }

}
