package com.example.cybersafetyapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
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

public class UserPostInformationActivity extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver{
    private String email,userid;
    private int requestType;
    Bundle messages;
    TableLayout userPostInfoTable;
    private JsonResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_post_information);
            messages = getIntent().getExtras();
            this.email = messages.getString(IntentSwitchVariables.email);
            this.userid = messages.getString(IntentSwitchVariables.USERID);

            this.userPostInfoTable = (TableLayout)findViewById(R.id.activity_user_post_information_table);
            this.requestType = messages.getInt(IntentSwitchVariables.request);
            if(messages.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION)
            {
                InstagramPostInformation();
            }
            else if(messages.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_VINE_POST_INFORMATION)
            {
                Log.i(UtilityVariables.tag,"showing vine post info");
                VinePostInformation();
            }

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in class:"+this.getClass().getName()+" Exception: "+ex.toString());
        }

    }

    private void addPostDataToRowInstagram(JSONArray resultarray) throws Exception
    {
        if(resultarray.length() > 1)
        {
            for(int i=0;i<resultarray.length();i++)
            {
                JSONObject postData = resultarray.getJSONObject(i);
                //Log.i(UtilityVariables.tag,"Single post info for instagram: "+postData.toString());
                TableRow tr_head = new TableRow(this);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));



                Button clickToViewPostsInfo = new Button(this);
                clickToViewPostsInfo.setText("See Post");
                clickToViewPostsInfo.setTextColor(Color.WHITE);
                clickToViewPostsInfo.setPadding(0,10,0,10);
                clickToViewPostsInfo.setOnClickListener(this);
                tr_head.addView(clickToViewPostsInfo);

                TextView t = new TextView(this);
                t.setText(postData.getJSONObject("likes").getString("count")+" likes");
                t.setTextColor(Color.WHITE);
                t.setPadding(12,10,2,10);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(postData.getJSONObject("comments").getString("count")+" comments");
                t.setTextColor(Color.WHITE);
                t.setPadding(10,10,2,10);
                tr_head.addView(t);

                try{
                    t = new TextView(this);
                    t.setText("Caption:"+postData.getJSONObject("caption").getString("text"));
                    t.setTextColor(Color.WHITE);
                    t.setPadding(10,10,2,10);
                    tr_head.addView(t);

                }catch (Exception ex)
                {
                    t = new TextView(this);
                    t.setText("No Caption");
                    t.setTextColor(Color.WHITE);
                    t.setPadding(10,10,2,10);
                    tr_head.addView(t);
                }



                t = new TextView(this);
                t.setText(postData.getString("id"));
                t.setTextColor(Color.WHITE);
                t.setPadding(2,10,2,10);
                t.setVisibility(View.INVISIBLE);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(postData.getString("link"));
                t.setTextColor(Color.WHITE);
                t.setPadding(2,10,2,10);
                t.setVisibility(View.INVISIBLE);
                tr_head.addView(t);


                this.userPostInfoTable.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            }
        }
    }


    private void addPostDataToRowVine(JSONArray resultarray) throws Exception
    {
        if(resultarray.length() > 1)
        {
            for(int i=0;i<resultarray.length();i++)
            {
                JSONObject postData = resultarray.getJSONObject(i);
                //Log.i(UtilityVariables.tag,"Single post info for instagram: "+postData.toString());
                TableRow tr_head = new TableRow(this);
                tr_head.setBackgroundColor(Color.GRAY);
                tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));



                Button clickToViewPostsInfo = new Button(this);
                clickToViewPostsInfo.setText("See Post");
                clickToViewPostsInfo.setTextColor(Color.WHITE);
                clickToViewPostsInfo.setPadding(0,10,0,10);
                clickToViewPostsInfo.setOnClickListener(this);
                tr_head.addView(clickToViewPostsInfo);

                TextView t = new TextView(this);
                t.setText(postData.getJSONObject("likes").getString("count")+" likes");
                t.setTextColor(Color.WHITE);
                t.setPadding(12,10,2,10);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(postData.getJSONObject("comments").getString("count")+" comments");
                t.setTextColor(Color.WHITE);
                t.setPadding(10,10,2,10);
                tr_head.addView(t);

                try{
                    t = new TextView(this);
                    t.setText("Caption:"+postData.getString("description"));
                    t.setTextColor(Color.WHITE);
                    t.setPadding(10,10,2,10);
                    tr_head.addView(t);

                }catch (Exception ex)
                {
                    t = new TextView(this);
                    t.setText("No Caption");
                    t.setTextColor(Color.WHITE);
                    t.setPadding(10,10,2,10);
                    tr_head.addView(t);
                }



                t = new TextView(this);
                t.setText(postData.getString("postId"));
                t.setTextColor(Color.WHITE);
                t.setPadding(2,10,2,10);
                t.setVisibility(View.INVISIBLE);
                tr_head.addView(t);

                t = new TextView(this);
                t.setText(postData.getString("shareUrl"));
                t.setTextColor(Color.WHITE);
                t.setPadding(2,10,2,10);
                t.setVisibility(View.INVISIBLE);
                tr_head.addView(t);


                this.userPostInfoTable.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            }
        }
    }

    private void InstagramPostInformation() throws Exception
    {
        JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_POST_INFORMATION_RESULT_JSON));
        JSONArray resultarray = resultjson.getJSONArray("data");
        addPostDataToRowInstagram(resultarray);
    }


    private void VinePostInformation() throws Exception
    {
        //Log.i(UtilityVariables.tag,"showing vine post info VinePostInformation function");
        JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.VINE_POST_INFORMATION_RESULT_JSON));
        JSONArray resultarray = resultjson.getJSONObject("data").getJSONArray("records");
        //Log.i(UtilityVariables.tag,resultarray.toString());
        addPostDataToRowVine(resultarray);
    }

    @Override
    public void onClick(View v) {

        Button clickToViewPosts = (Button)v;
        TableRow row = (TableRow)clickToViewPosts.getParent();
        String postid = ((TextView)row.getChildAt(4)).getText().toString();
        if(this.requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION)
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent instagramIntentService = new Intent(this, IntentServiceInstagram.class);
            instagramIntentService.putExtra(IntentSwitchVariables.POSTID,postid);
            instagramIntentService.putExtra(IntentSwitchVariables.InstagramAccessToken,messages.getString(IntentSwitchVariables.InstagramAccessToken));
            instagramIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            instagramIntentService.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS);
            startService(instagramIntentService);
        }
        else if (this.requestType == IntentSwitchVariables.REQUEST_VINE_POST_INFORMATION)
        {
            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent vineIntentService = new Intent(this, IntentServiceVine.class);
            vineIntentService.putExtra(IntentSwitchVariables.POSTID,postid);
            vineIntentService.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,messages.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
            vineIntentService.putExtra(IntentSwitchVariables.receiver, mReceiver);
            vineIntentService.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_VINE_POST_DETAILS);
            startService(vineIntentService);

        }
        //Log.i(UtilityVariables.tag,link);
        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        //startActivity(browserIntent);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                Intent intent = new Intent(PostInformationDetails.class.getName());
                intent.putExtra(IntentSwitchVariables.email,this.email);
                intent.putExtra(IntentSwitchVariables.request,resultData.getInt(IntentSwitchVariables.request));
                intent.putExtra(IntentSwitchVariables.POSTID, resultData.getString(IntentSwitchVariables.POSTID));
                if(resultData.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS)
                {
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.InstagramAccessToken,resultData.getString(IntentSwitchVariables.InstagramAccessToken));
                }

                else if(resultData.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_VINE_POST_DETAILS)
                {
                    intent.putExtra(IntentSwitchVariables.VINE_POST_DETAILS_RESULT_JSON,resultData.getString(IntentSwitchVariables.VINE_POST_DETAILS_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.VINE_ACCESS_TOKEN));
                }
                startActivity(intent);
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                break;
        }

    }
}
