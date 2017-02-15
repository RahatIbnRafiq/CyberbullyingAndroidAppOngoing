package com.example.cybersafetyapp.ActivityPackage;

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

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ToastMessagesVariables;
import com.example.cybersafetyapp.UtilityPackage.ErrorMessageVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class SearchResultProfiles extends AppCompatActivity {
    private String email;
    private DatabaseHelper databaseHelper;
    private Bundle messages;
    private int requstType;
    private TableLayout tableSearchResult;
    private String classname = this.getClass().getSimpleName();


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
        toMonitorCheckbox.setText(R.string.Monitor);
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
            Toast.makeText(this,"No user found for this user name. Please try again with a different user name",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,SearchByUserName.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
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

        }
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
            Toast.makeText(this,"Something unexpected happened",Toast.LENGTH_LONG).show();
            Log.i(UtilityVariables.tag,this.classname+"Exception in showInstagramSearchProfiles function: "+ex.toString());
            Intent intent = new Intent(this,SearchByUserName.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_profiles);
        databaseHelper = new DatabaseHelper(this);
        messages = getIntent().getExtras();
        this.email = messages.getString(IntentSwitchVariables.EMAIL);
        if(messages.getInt(IntentSwitchVariables.REQUEST) == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
        {
            showInstagramSearchProfiles();
        }
    }


    private Class checkIfValidMonitoringRequest()
    {
        ArrayList<String> userids = new ArrayList<>();
        ArrayList<String> usernames = new ArrayList<>();

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

        if (noOfCheckedCheckboxes > UtilityVariables.NO_OF_TO_BE_MONITORED_USERS)
        {
            Toast.makeText(this, ErrorMessageVariables.CANNOT_MONITOR_MORE_THAN_TWO, Toast.LENGTH_SHORT).show();
            return SearchResultProfiles.class;
        }

        else if(noOfCheckedCheckboxes == 0)
        {
            Toast.makeText(this, ErrorMessageVariables.DID_NOT_SELECT_ANY, Toast.LENGTH_SHORT).show();
            return SearchResultProfiles.class;
        }
        Cursor res=null;

        try {
            ArrayList<String> alreadyMonitoring = new ArrayList<>();
            String tableName = "";
            if(this.requstType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                tableName = DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE;

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

            if(alreadyMonitoring.size() == UtilityVariables.NO_OF_TO_BE_MONITORED_USERS)
            {
                Toast.makeText(this, ToastMessagesVariables.ALREADY_MONITORING_ENOUGH, Toast.LENGTH_SHORT).show();
                return Dashboard.class;
            }

            if(alreadyMonitoring.size()+noOfCheckedCheckboxes > UtilityVariables.NO_OF_TO_BE_MONITORED_USERS)
            {
                Toast.makeText(this, ErrorMessageVariables.CANNOT_MONITOR_MORE_THAN_TWO, Toast.LENGTH_SHORT).show();
                return SearchResultProfiles.class;
            }

            for(int i=0;i<alreadyMonitoring.size();i++)
            {
                Log.i(UtilityVariables.tag,this.classname+ "already monitoring: "+alreadyMonitoring.get(i));
            }
            for(int i=0;i<userids.size();i++)
            {
                Log.i(UtilityVariables.tag,this.classname+ "proposing monitoring for: "+userids.get(i));
            }

            for(int i=0;i<userids.size();i++)
            {
                if (alreadyMonitoring.indexOf(userids.get(i)) < 0)
                {
                    long insertionResult = databaseHelper.insertMonitoringTable(email,userids.get(i),usernames.get(i),tableName);
                    if(insertionResult == -1)
                    {
                        Toast.makeText(this, ErrorMessageVariables.UNEXPECTED_DATABASE_ERROR, Toast.LENGTH_SHORT).show();
                        Log.i(UtilityVariables.tag,this.classname+ " : unexpected error while inserting. breaking out of the insertion loop");
                        return Dashboard.class;
                    }
                }
            }
            Toast.makeText(this, ToastMessagesVariables.YOU_ARE_NOW_MONITORING, Toast.LENGTH_SHORT).show();
            return Dashboard.class;
        } catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.classname+ ": Exception checkIfValidMonitoringRequest in class: "+this.getClass().getName());
            Log.i(UtilityVariables.tag,e.toString());
            return Dashboard.class;

        }finally {
            if(res != null)
                res.close();
        }

    }





    public void buttonOnClickStartMonitoring(View v)
    {
        Log.i(UtilityVariables.tag,this.classname+ " going to check if valid function in search result profiles");
        Class whereToSwitch = checkIfValidMonitoringRequest();
        Log.i(UtilityVariables.tag,this.classname+ " switching to this activity from search result profiles activity "+whereToSwitch);
        Intent intent = new Intent(this,whereToSwitch);
        intent.putExtra(IntentSwitchVariables.EMAIL,email);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
        startActivity(intent);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
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
