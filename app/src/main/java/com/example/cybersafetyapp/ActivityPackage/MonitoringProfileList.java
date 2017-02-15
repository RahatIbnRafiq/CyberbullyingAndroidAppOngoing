package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceInstagram;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.util.Hashtable;
import java.util.Set;

public class MonitoringProfileList extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver {
    private DatabaseHelper databaseHelper;
    private String email;
    private TableLayout tableSearchResult;
    private String classname = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_monitoring_profile_list);
            this.tableSearchResult = (TableLayout) findViewById(R.id.activity_monitoring_profile_list_table);
            this.databaseHelper = new DatabaseHelper(this);
            Bundle messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.EMAIL);

            Hashtable<String,String> monitorInformation = databaseHelper.getMonitoringInformationDetailByGuardianEmail(DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE,this.email);
            int i = 0;
            if(monitorInformation.size() > 0)
            {
                String str;
                Set<String> keys = monitorInformation.keySet();
                for (String key : keys) {
                    str = key;
                    addTableRow(monitorInformation.get(str), str, "Instagram", i++);
                }
            }



        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in class: "+this.classname+" onCreate function Exception: "+ex.toString());
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
        clickToViewPosts.setText(R.string.Details);
        clickToViewPosts.setTextColor(Color.WHITE);
        clickToViewPosts.setPadding(0,15,0,15);
        clickToViewPosts.setOnClickListener(this);
        tr_head.addView(clickToViewPosts);

        Button removeUser = new Button(this);
        removeUser.setText(R.string.Remove);
        removeUser.setTextColor(Color.WHITE);
        removeUser.setPadding(0,15,2,15);
        removeUser.setOnClickListener(this);
        tr_head.addView(removeUser);



        TextView label_userid = new TextView(this);
        label_userid.setText(userid);
        label_userid.setVisibility(View.INVISIBLE);
        label_userid.setPadding(0,15,0,15);
        tr_head.addView(label_userid);

        this.tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        Button buttonClicked = (Button)v;
        if(buttonClicked.getText().toString().equals("Remove"))
        {
            Log.i(UtilityVariables.tag,this.classname+" : Remove user button was clicked.");
            TableRow row = (TableRow) buttonClicked.getParent();
            TextView osnName = (TextView) row.getChildAt(0);
            TextView userid = (TextView) row.getChildAt(4);
            if (osnName.getText().toString().equals("Instagram"))
            {
                this.databaseHelper.deleteMonitoredUserFromTable(DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE,
                        this.email,userid.getText().toString());
            }
            Intent intent = new Intent(this,Dashboard.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
            startActivity(intent);
        }
        else {
            TableRow row = (TableRow) buttonClicked.getParent();
            TextView osnName = (TextView) row.getChildAt(0);
            TextView userid = (TextView) row.getChildAt(4);
            if (osnName.getText().toString().equals("Instagram")) {
                Log.i(UtilityVariables.tag, this.classname+ " the user is from instagram");
                JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
                mReceiver.setReceiver(this);
                Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
                instagramIntentService.putExtra(IntentSwitchVariables.USERID, userid.getText().toString());
                instagramIntentService.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, databaseHelper.getAccessTokenForGuardian(this.email, DatabaseHelper.NAME_COL_INSTAGRAM_TOKEN));
                instagramIntentService.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
                instagramIntentService.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL);
                startService(instagramIntentService);

            }
        }

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                Intent intent = new Intent(this,UserSocialNetworkInformationDetail.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.REQUEST,resultData.getInt(IntentSwitchVariables.REQUEST));
                intent.putExtra(IntentSwitchVariables.USERID, resultData.getString(IntentSwitchVariables.USERID));
                if(resultData.getInt(IntentSwitchVariables.REQUEST) == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
                {
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                }
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                break;
        }

    }
}
