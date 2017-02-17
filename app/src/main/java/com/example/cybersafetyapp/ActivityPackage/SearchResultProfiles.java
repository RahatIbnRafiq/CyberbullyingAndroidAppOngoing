package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
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
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceServer;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ToastMessagesVariables;
import com.example.cybersafetyapp.UtilityPackage.ErrorMessageVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class SearchResultProfiles extends AppCompatActivity implements JsonResultReceiver.Receiver {
    private String email;
    private DatabaseHelper databaseHelper;
    private Bundle messages;
    private int requstType;
    private TableLayout tableSearchResult;
    private String classname = this.getClass().getSimpleName();
    private int alreadyMonitoring;
    private int noOfCheckedCheckboxes ;
    private String userids;
    private String usernames;


    private void addTableRow(String username, String userid, String profileUrl, int i) {
        TableRow tr_head = new TableRow(this);
        tr_head.setId(i);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        TextView label_username = new TextView(this);
        label_username.setText(username);
        label_username.setTextColor(Color.WHITE);
        label_username.setPadding(0, 2, 0, 1);
        tr_head.addView(label_username);


        ImageView profilePicture = new ImageView(this);
        new DownloadImageTask(profilePicture).execute(profileUrl);

        tr_head.addView(profilePicture);

        CheckBox toMonitorCheckbox = new CheckBox(this);
        toMonitorCheckbox.setText(R.string.Monitor);
        //ltrb
        toMonitorCheckbox.setPadding(2, 2, 5, 1);
        tr_head.addView(toMonitorCheckbox);

        TextView label_userid = new TextView(this);
        label_userid.setText(userid);
        label_userid.setVisibility(View.INVISIBLE);
        label_userid.setPadding(0, 2, 0, 1);
        tr_head.addView(label_userid);


        tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        profilePicture.getLayoutParams().height = 100;
        profilePicture.getLayoutParams().width = 100;
    }


    private void addDataToTable(JSONArray userdata) {
        if (userdata.length() == 0) {
            Toast.makeText(this, "No user found for this user name. Please try again with a different user name", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SearchByUserName.class);
            intent.putExtra(IntentSwitchVariables.EMAIL, this.email);
            startActivity(intent);
        } else {
            tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
            if (this.requstType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH) {
                for (int i = 0; i < userdata.length(); i++) {
                    JSONObject record = userdata.optJSONObject(i);
                    addTableRow(record.optString("username"), record.optString("id"), record.optString("profile_picture"), i);
                }
            }

        }
    }


    private void showInstagramSearchProfiles() {
        try {
            JSONObject resultjson = new JSONObject(messages.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
            JSONArray userdata = resultjson.optJSONArray("data");
            this.requstType = IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH;
            addDataToTable(userdata);


        } catch (Exception ex) {
            Toast.makeText(this, "Something unexpected happened", Toast.LENGTH_LONG).show();
            Log.i(UtilityVariables.tag, this.classname + "Exception in showInstagramSearchProfiles function: " + ex.toString());
            Intent intent = new Intent(this, SearchByUserName.class);
            intent.putExtra(IntentSwitchVariables.EMAIL, this.email);
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
        this.alreadyMonitoring = -1;
        this.noOfCheckedCheckboxes = 0;
        this.userids = "";
        this.usernames = "";
        if (messages.getInt(IntentSwitchVariables.REQUEST) == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH) {
            showInstagramSearchProfiles();
        }
    }


    private void getUsersFromCheckboxes() {


        TableLayout tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);

        for (int i = 0; i < tableSearchResult.getChildCount(); i++) {
            TableRow mRow = (TableRow) tableSearchResult.getChildAt(i);
            TextView userid = (TextView) mRow.getChildAt(3);
            TextView username = (TextView) mRow.getChildAt(0);
            CheckBox mCheckbox = (CheckBox) mRow.getChildAt(2);
            if (mCheckbox.isChecked()) {
                noOfCheckedCheckboxes++;
                this.userids = this.userids+","+userid.getText().toString();
                this.usernames = this.usernames+","+username.getText().toString();
            }
        }
        this.userids = this.userids.substring(1,this.userids.length());
        this.usernames = this.usernames.substring(1,this.usernames.length());
        Log.i(UtilityVariables.tag,this.userids);
        Log.i(UtilityVariables.tag,this.usernames);


        if (noOfCheckedCheckboxes > UtilityVariables.NO_OF_TO_BE_MONITORED_USERS) {
            Toast.makeText(this, ErrorMessageVariables.CANNOT_MONITOR_MORE_THAN_TWO, Toast.LENGTH_SHORT).show();
        } else if (noOfCheckedCheckboxes == 0) {
            Toast.makeText(this, ErrorMessageVariables.DID_NOT_SELECT_ANY, Toast.LENGTH_SHORT).show();
        }
        /*else {
            if(this.alreadyMonitoring == -1) {
                JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
                mReceiver.setReceiver(this);
                Intent intent = new Intent(this, IntentServiceServer.class);
                intent.putExtra(IntentSwitchVariables.EMAIL, this.email);
                intent.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
                intent.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_MONITORING_COUNT);
                startService(intent);
            }
            else
            {
                if(this.noOfCheckedCheckboxes + this.alreadyMonitoring > UtilityVariables.NO_OF_TO_BE_MONITORED_USERS)
                {
                    Toast.makeText(this, "You are already monitoring "+this.alreadyMonitoring+"" +
                            "users.You can only monitor "+UtilityVariables.NO_OF_TO_BE_MONITORED_USERS
                            +" users. Please select again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    performMonitoringRequest();
                }
            }
        }*/

    }


    private void performMonitoringRequest()
    {
        JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(this, IntentServiceServer.class);
        intent.putExtra(IntentSwitchVariables.EMAIL, this.email);
        intent.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
        intent.putExtra(IntentSwitchVariables.INSTAGRAM_USERIDS, this.userids);
        intent.putExtra(IntentSwitchVariables.INSTAGRAM_USERNAMES, this.usernames);
        intent.putExtra(IntentSwitchVariables.INSTAGRAM_MONITORING_REQUEST_COUNT, this.noOfCheckedCheckboxes);
        intent.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_MONITOR_USER);
        startService(intent);
    }


    public void buttonOnClickStartMonitoring(View v)
    {
        try {
            getUsersFromCheckboxes();
            performMonitoringRequest();
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+" buttonOnClickStartMonitoring: "+ex.toString());
        }

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                Log.i(UtilityVariables.tag,"Running code intent service");
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                int requesttype = resultData.getInt(IntentSwitchVariables.REQUEST);
                if (requesttype == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                {
                    Intent intent = new Intent(this,SearchResultProfiles.class);
                    intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                    intent.putExtra(IntentSwitchVariables.REQUEST,requesttype);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON,resultData.getString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON));
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,resultData.getString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                    startActivity(intent);
                }
                else if (requesttype == IntentSwitchVariables.REQUEST_INSTAGRAM_MONITOR_USER)
                {
                    //Log.i(UtilityVariables.tag,this.classname+" REQUEST_INSTAGRAM_MONITOR_USER has been processed");
                    String message = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE);
                    String success = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS);
                    if(success!= null && success.equals("success"))
                    {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,Dashboard.class);
                        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                        intent.putExtra(IntentSwitchVariables.TO_BE_MONITORED_USERIDS,this.userids);
                        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                        startActivity(intent);

                    }
                    else
                    {
                        Log.i(UtilityVariables.tag,"going to SearchByUsername class");
                        Toast.makeText(this, message+". Please Try Again.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,SearchByUserName.class);
                        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                        startActivity(intent);
                    }

                }
                /*else if (requesttype == IntentSwitchVariables.REQUEST_INSTAGRAM_MONITORING_COUNT)
                {
                    this.alreadyMonitoring = resultData.getInt(IntentSwitchVariables.SERVER_INSTAGRAM_MONITORING_COUNT);
                    if(this.noOfCheckedCheckboxes + this.alreadyMonitoring > UtilityVariables.NO_OF_TO_BE_MONITORED_USERS)
                    {
                        Toast.makeText(this, "You are already monitoring "+this.alreadyMonitoring+"" +
                                "users.You can only monitor "+UtilityVariables.NO_OF_TO_BE_MONITORED_USERS
                                +" users. Please select again", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        performMonitoringRequest();
                    }
                }*/
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.i(UtilityVariables.tag,error);
                break;
        }

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
