package com.example.cybersafetyapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class SearchResultProfiles extends AppCompatActivity {
    private String email,osnName;
    DatabaseHelper databaseHelper;
    private Bundle messages;
    private int requstType;
    TableLayout tableSearchResult;


    private void addTableRow(String username,String userid,String profileUrl,int i)
    {
        TableRow tr_head = new TableRow(this);
        tr_head.setId(i);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        TextView label_username = new TextView(this);
        label_username.setText(username);
        label_username.setTextColor(Color.WHITE);
        label_username.setPadding(0,2,0,1);
        tr_head.addView(label_username);


        ImageView profilePicture = new ImageView(this);
        new DownloadImageTask(profilePicture).execute(profileUrl);

        tr_head.addView(profilePicture);

        CheckBox toMonitorCheckbox = new CheckBox(this);
        toMonitorCheckbox.setText("monitor");
        //ltrb
        toMonitorCheckbox.setPadding(2,2,5,1);
        tr_head.addView(toMonitorCheckbox);

        TextView label_userid = new TextView(this);
        label_userid.setText(userid);
        label_userid.setVisibility(View.INVISIBLE);
        label_userid.setPadding(0,2,0,1);
        tr_head.addView(label_userid);



        tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        profilePicture.getLayoutParams().height = 100;
        profilePicture.getLayoutParams().width = 100;
    }


    private void addDataToTable(JSONArray userdata)
    {
        if(userdata.length() == 0)
        {
            Toast.makeText(this,"No user found for this user name. Please try again with a different user name",Toast.LENGTH_LONG);
            Intent intent = new Intent(SearchByUserName.class.getName());
            intent.putExtra(IntentSwitchVariables.email,this.email);
            startActivity(intent);
        }
        else
        {
            tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
            if(this.requstType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
            {
                for(int i=0;i<userdata.length();i++)
                {
                    JSONObject record = userdata.optJSONObject(i);
                    addTableRow(record.optString("username"),record.optString("id"),record.optString("profile_picture"),i);
                }
            }

            else if(this.requstType == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH)
            {
                for(int i=0;i<userdata.length();i++)
                {
                    JSONObject record = userdata.optJSONObject(i);
                    addTableRow(record.optString("username"),record.optString("userId"),record.optString("avatarUrl"),i);
                }
            }

        }
    }

    private void showVineSearchProfiles()
    {
        try{
            JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.VINE_USER_SEARCH_RESULT_JSON));
            JSONArray userdata = resultjson.getJSONObject("data").getJSONArray("records");
            this.requstType = IntentSwitchVariables.REQUEST_VINE_USER_SEARCH;
            addDataToTable(userdata);


        }catch (Exception ex)
        {
            Toast.makeText(this,"Something unexpected happened",Toast.LENGTH_LONG);
            Log.i(UtilityVariables.tag,ex.toString());
            Intent intent = new Intent(SearchByUserName.class.getName());
            intent.putExtra(IntentSwitchVariables.email,this.email);
            startActivity(intent);
        }

        //Log.i(UtilityVariables.tag,"instagram user search result response in search result ptofiles: "+messages.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
    }


    private void showInstagramSearchProfiles()
    {
        try{
            JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
            JSONArray userdata = resultjson.optJSONArray("data");
            this.requstType = IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH;
            addDataToTable(userdata);


        }catch (Exception ex)
        {
            Toast.makeText(this,"Something unexpected happened",Toast.LENGTH_LONG);
            Log.i(UtilityVariables.tag,ex.toString());
            Intent intent = new Intent(SearchByUserName.class.getName());
            intent.putExtra(IntentSwitchVariables.email,this.email);
            startActivity(intent);
        }

        //Log.i(UtilityVariables.tag,"instagram user search result response in search result ptofiles: "+messages.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_profiles);
        databaseHelper = new DatabaseHelper(this);
        messages = getIntent().getExtras();
        this.email = messages.getString(IntentSwitchVariables.email);
        if(messages.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
        {
            showInstagramSearchProfiles();
        }

        else if(messages.getInt(IntentSwitchVariables.request) == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH)
        {
            showVineSearchProfiles();
        }
    }


    private String checkIfValidMonitoringRequest(View v)
    {
        ArrayList<String> userids = new ArrayList<String>();
        ArrayList<String> usernames = new ArrayList<String>();

        TableLayout tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
        int noOfCheckedCheckboxes = 0;
        for(int i=0;i<tableSearchResult.getChildCount();i++)
        {
            TableRow mRow = (TableRow) tableSearchResult.getChildAt(i);
            TextView userid = (TextView) mRow.getChildAt(3);
            TextView username = (TextView) mRow.getChildAt(0);
            CheckBox mCheckbox = (CheckBox)mRow.getChildAt(2);
            if (mCheckbox.isChecked()) {
                noOfCheckedCheckboxes++;
                userids.add(userid.getText().toString());
                usernames.add(username.getText().toString());
            }
        }

        if (noOfCheckedCheckboxes > ErrorMessageVariables.noOfToBeMonitoredUsers)
        {
            Toast.makeText(this, ToastMessagesVariables.cantMonitorMoreThanTwo, Toast.LENGTH_SHORT).show();
            return IntentSwitchVariables.SearchResultProfiles;
        }

        else if(noOfCheckedCheckboxes == 0)
        {
            Toast.makeText(this, ToastMessagesVariables.didnotSelectAny, Toast.LENGTH_SHORT).show();
            return IntentSwitchVariables.SearchResultProfiles;
        }
        Cursor res=null;

        try {
            ArrayList<String> alreadyMonitoring = new ArrayList<String>();
            String tableName = "";
            if(this.requstType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                tableName = DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE;
            else if (this.requstType == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH)
                tableName = DatabaseHelper.NAME_TABLE_VINE_MONITORING_USER_TABLE;

            res = this.databaseHelper.getMonitorInformationByGuardianEmail(tableName,this.email);
            if(res != null)
            {
                if(res.moveToFirst())
                {
                    do {
                        alreadyMonitoring.add(res.getString(res.getColumnIndex(DatabaseHelper.NAME_COL_USERID)));

                    }while(res.moveToNext());
                }
            }

            if(alreadyMonitoring.size() == ErrorMessageVariables.noOfToBeMonitoredUsers)
            {
                Toast.makeText(this, ToastMessagesVariables.AlreadyMonitoringEnough, Toast.LENGTH_SHORT).show();
                return IntentSwitchVariables.Dashboard;
            }

            if(alreadyMonitoring.size()+noOfCheckedCheckboxes > ErrorMessageVariables.noOfToBeMonitoredUsers)
            {
                Toast.makeText(this, ToastMessagesVariables.cantMonitorMoreThanTwo, Toast.LENGTH_SHORT).show();
                return IntentSwitchVariables.SearchResultProfiles;
            }

            for(int i=0;i<alreadyMonitoring.size();i++)
            {
                Log.i(UtilityVariables.tag,"already monitoring: "+alreadyMonitoring.get(i));
            }
            for(int i=0;i<userids.size();i++)
            {
                Log.i(UtilityVariables.tag,"proposing monitoring for: "+userids.get(i));
            }

            for(int i=0;i<userids.size();i++)
            {
                if (alreadyMonitoring.indexOf(userids.get(i)) < 0)
                {
                    long insertionResult = databaseHelper.insertMonitoringTable(email,userids.get(i),usernames.get(i),tableName);
                    if(insertionResult == -1)
                    {
                        Toast.makeText(this, ErrorMessageVariables.UnexpectedDatabaseError, Toast.LENGTH_SHORT).show();
                        Log.i(UtilityVariables.tag,"unexpected error while inserting. breaking out of the insertion loop");
                        return IntentSwitchVariables.Dashboard;
                    }
                }
            }
            Toast.makeText(this, ToastMessagesVariables.YouAreNowMonitoring, Toast.LENGTH_SHORT).show();
            return IntentSwitchVariables.Dashboard;
        } catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception checkIfValidMonitoringRequest in class: "+this.getClass().getName());
            Log.i(UtilityVariables.tag,e.toString());
            return IntentSwitchVariables.Dashboard;

        }finally {
            if(res != null)
                res.close();
        }

    }





    public void buttonOnClickStartMonitoring(View v)
    {
        Log.i(UtilityVariables.tag," going to check if valid function in search result profiles");
        String whereToSwitch = checkIfValidMonitoringRequest(v);
        Log.i(UtilityVariables.tag," switching to this activity from search result profiles activity "+whereToSwitch);
        Intent intent = new Intent(whereToSwitch);
        intent.putExtra(IntentSwitchVariables.email,email);
        intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
        startActivity(intent);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
