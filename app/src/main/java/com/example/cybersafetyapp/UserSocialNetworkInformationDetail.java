package com.example.cybersafetyapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class UserSocialNetworkInformationDetail extends AppCompatActivity {
    private int requestType;
    private Bundle messages;
    TableLayout tableSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_social_network_information_detail);
        try{
            messages = getIntent().getExtras();
            this.requestType = messages.getInt(IntentSwitchVariables.request);
            tableSearchResult = (TableLayout) findViewById(R.id.activity_user_social_network_information_detail_TableUSerInformationDetail);
            if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
            {
                showInstagramUserDetailInformation();
            }
            else if (requestType == IntentSwitchVariables.REQUEST_VINE_USER_DETAIL)
            {
                showVineUserDetailInformation();
            }


        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in class:"+this.getClass().getName()+" Exception: "+ex.toString());
        }
    }

    private void addDataToRow(JSONObject userdata)
    {

        Iterator<String> iter = userdata.keys();
        while (iter.hasNext()) {

            String key = iter.next();
            try {
                TableRow tr_head = new TableRow(this);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                String value = userdata.get(key).toString();
                //Log.i(UtilityVariables.tag,"key: "+key+" value: "+value);

                TextView t = new TextView(this);
                t.setText(key);
                t.setTextColor(Color.WHITE);
                t.setPadding(0,10,5,10);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(value);
                t.setTextColor(Color.WHITE);
                t.setPadding(0,10,5,10);
                tr_head.addView(t);
                tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            } catch (Exception ex) {
                Log.i(UtilityVariables.tag,"exception while ietaring through json object: "+ex.toString());
            }
            //tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }



    }


    private void showInstagramUserDetailInformation()
    {
        try {
            JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON));
            JSONObject userdata = resultjson.getJSONObject("data");
            this.requestType = IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL;
            addDataToRow(userdata);
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Something unexpected happened in showInstagramUserDetailInformation fucntion class: "+this.getClass().getName()+" :"+ex.toString());
        }
    }

    private void showVineUserDetailInformation()
    {
        try {
            JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.VINE_USER_DETAIL_RESULT_JSON));
            JSONObject userdata = resultjson.getJSONObject("data");
            this.requestType = IntentSwitchVariables.REQUEST_VINE_USER_DETAIL;
            addDataToRow(userdata);
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Something unexpected happened in showVineUserDetailInformation fucntion class: "+this.getClass().getName()+" :"+ex.toString());
        }
    }
}
