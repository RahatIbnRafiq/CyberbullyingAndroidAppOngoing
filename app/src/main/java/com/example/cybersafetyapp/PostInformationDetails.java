package com.example.cybersafetyapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PostInformationDetails extends AppCompatActivity {

    private String email;
    private int requestType;
    TableLayout tablePostDetails;
    Bundle messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post_information_details);
            messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.email);
            this.requestType = messages.getInt(IntentSwitchVariables.request);
            this.tablePostDetails = (TableLayout)findViewById(R.id.activity_post_information_details_table);
            if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS)
            {
                showInstagramPostDetails();
            }
            else if(this.requestType == IntentSwitchVariables.REQUEST_VINE_POST_DETAILS)
            {
                showVinePostDetails();
            }

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception class: "+this.getClass().getName()+" Exception: "+ex.toString());
        }
    }

    private void addInstagramDataToTable(JSONArray resultArray) throws Exception
    {
        if(resultArray.length() > 0)
        {
            for(int i=0; i< resultArray.length();i++)
            {
                TableRow tr_head = new TableRow(this);
                tr_head.setId(i);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                TextView t = new TextView(this);
                t.setText(resultArray.getJSONObject(i).getString("text"));
                t.setPadding(2,15,5,15);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(" BY: "+resultArray.getJSONObject(i).getJSONObject("from").getString("username"));
                t.setPadding(2,15,5,15);
                tr_head.addView(t);
                this.tablePostDetails.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }


    private void showInstagramPostDetails() throws  Exception
    {
        JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON));
        JSONArray resultArray = resultjson.getJSONArray("data");
        addInstagramDataToTable(resultArray);
    }

    private void addVineDataToTable(JSONArray resultArray) throws Exception
    {
        if(resultArray.length() > 0)
        {
            for(int i=0; i< resultArray.length();i++)
            {
                TableRow tr_head = new TableRow(this);
                tr_head.setId(i);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                TextView t = new TextView(this);
                t.setText(resultArray.getJSONObject(i).getString("comment"));
                t.setPadding(2,15,5,15);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(" BY: "+resultArray.getJSONObject(i).getString("username"));
                t.setPadding(2,15,5,15);
                tr_head.addView(t);
                this.tablePostDetails.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private void showVinePostDetails() throws  Exception
    {
        JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.VINE_POST_DETAILS_RESULT_JSON));
        JSONObject data = resultjson.getJSONObject("data");
        JSONArray resultArray = data.getJSONArray("records");
        addVineDataToTable(resultArray);
    }
}
