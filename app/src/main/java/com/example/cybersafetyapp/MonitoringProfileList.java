package com.example.cybersafetyapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class MonitoringProfileList extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver {
    DatabaseHelper databaseHelper;
    private String email;
    TableLayout tableSearchResult;
    private JsonResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_monitoring_profile_list);
            tableSearchResult = (TableLayout) findViewById(R.id.activity_monitoring_profile_list_table);
            databaseHelper = new DatabaseHelper(this);
            Bundle messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.email);

            Hashtable<String,String> monitorInformation = databaseHelper.getMonitoringInformationDetailByGuardianEmail(DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE,email);
            int i = 0;
            if(monitorInformation.size() > 0)
            {
                String str;
                Set<String> keys = monitorInformation.keySet();
                Iterator<String> itr = keys.iterator();
                while (itr.hasNext()) {
                    str = itr.next();
                    //Log.i(UtilityVariables.tag,"Key: "+str+" & Value: "+monitorInformation.get(str));
                    addTableRow(monitorInformation.get(str),str,"Instagram",i++);
                }
            }

            monitorInformation = databaseHelper.getMonitoringInformationDetailByGuardianEmail(DatabaseHelper.NAME_TABLE_VINE_MONITORING_USER_TABLE,email);
            if(monitorInformation.size() > 0)
            {
                String str;
                Set<String> keys = monitorInformation.keySet();
                Iterator<String> itr = keys.iterator();
                while (itr.hasNext()) {
                    str = itr.next();
                    //Log.i(UtilityVariables.tag,"Key: "+str+" & Value: "+monitorInformation.get(str));
                    addTableRow(monitorInformation.get(str),str,"Vine",i++);
                }
            }



        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in class: "+this.getClass().getName()+" Exception: "+ex.toString());
        }

    }


    private void addTableRow(String username,String userid,String OSNName,int i)
    {
        TableRow tr_head = new TableRow(this);
        tr_head.setId(i);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_OSNName = new TextView(this);
        label_OSNName.setText(OSNName);
        label_OSNName.setPadding(2,15,5,15);
        tr_head.addView(label_OSNName);


        TextView label_username = new TextView(this);
        label_username.setText(username);
        label_username.setTextColor(Color.WHITE);
        label_username.setPadding(0,15,2,15);
        tr_head.addView(label_username);

        Button clickToViewPosts = new Button(this);
        clickToViewPosts.setText("See Details");
        clickToViewPosts.setTextColor(Color.WHITE);
        clickToViewPosts.setPadding(0,15,0,15);
        clickToViewPosts.setOnClickListener(this);
        tr_head.addView(clickToViewPosts);



        TextView label_userid = new TextView(this);
        label_userid.setText(userid);
        label_userid.setVisibility(View.INVISIBLE);
        label_userid.setPadding(0,15,0,15);
        tr_head.addView(label_userid);

        tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        Button clickToViewPosts = (Button)v;
        TableRow row = (TableRow)clickToViewPosts.getParent();
        TextView osnName = (TextView)row.getChildAt(0);
        TextView username = (TextView)row.getChildAt(1);
        TextView userid = (TextView)row.getChildAt(3);
        if(osnName.getText().toString().equals("Instagram"))
        {
            Log.i(UtilityVariables.tag,"the user is from instagram");
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.USERID,userid.getText().toString());
            instagramIntentService.putExtra(IntentSwitchVariables.InstagramAccessToken,databaseHelper.getAccessTokenForGuardian(this.email,DatabaseHelper.NAME_COL_INSTAGRAM_TOKEN));
            instagramIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL);
            startService(instagramIntentService);

        }
        else if (osnName.getText().toString().equals("Vine"))
        {
            Log.i(UtilityVariables.tag,"the user is from vine");
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent vineIntentService = new Intent(this, IntentServiceVine.class);
            vineIntentService.putExtra(IntentSwitchVariables.USERID,userid.getText().toString());
            vineIntentService.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,databaseHelper.getAccessTokenForGuardian(this.email,DatabaseHelper.NAME_COL_VINE_TOKEN));
            vineIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            vineIntentService.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_VINE_USER_DETAIL);
            startService(vineIntentService);

        }
        //Log.i(UtilityVariables.tag,osnName.getText().toString()+","+username.getText().toString());

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                //Log.i(UtilityVariables.tag,"access token back to the login class "+resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
                Intent intent = new Intent(UserSocialNetworkInformationDetail.class.getName());
                intent.putExtra(IntentSwitchVariables.email,this.email);
                intent.putExtra(IntentSwitchVariables.request,resultData.getInt(IntentSwitchVariables.request));
                intent.putExtra(IntentSwitchVariables.USERID, resultData.getString(IntentSwitchVariables.USERID));
                if(resultData.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
                {
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.InstagramAccessToken,resultData.getString(IntentSwitchVariables.InstagramAccessToken));
                }
                else if(resultData.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_VINE_USER_DETAIL)
                {
                    intent.putExtra(IntentSwitchVariables.VINE_USER_DETAIL_RESULT_JSON,resultData.getString(IntentSwitchVariables.VINE_USER_DETAIL_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
                }
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                break;
        }

    }
}
