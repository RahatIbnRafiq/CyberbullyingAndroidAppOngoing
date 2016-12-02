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

import java.io.InputStream;
import java.util.ArrayList;

public class SearchResultProfiles extends AppCompatActivity {
    private String email,osnName;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_profiles);
        databaseHelper = new DatabaseHelper(this);
        Bundle messages = getIntent().getExtras();
        if(messages == null ||
                messages.getString(IntentSwitchVariables.sourceClassName) == null ||
                !messages.getString(IntentSwitchVariables.sourceClassName).equals(SearchByUserName.class.getName()))
        {
            Intent intent = new Intent(IntentSwitchVariables.Dashboard);
            intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
            startActivity(intent);
        }
        email = messages.getString(IntentSwitchVariables.email);
        String [] results = messages.getStringArray(IntentSwitchVariables.SearchResult);
        int requestType = messages.getInt(IntentSwitchVariables.request);
        osnName = messages.getString(IntentSwitchVariables.OSNName);

        TableLayout tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
        if(results != null)
        {
            for (int i = 0; i < results.length; i++)
            {
                String[]data = results[i].split(",");
                String username = data[0];
                String userid = data[1];
                String image_url = data[2];
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
                new DownloadImageTask(profilePicture).execute(image_url);

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
        }

    }


    private String checkIfValidMonitoringRequest(View v)
    {
        ArrayList<String> userids = new ArrayList<String>();

        TableLayout tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
        int noOfCheckedCheckboxes = 0;
        for(int i=0;i<tableSearchResult.getChildCount();i++)
        {
            TableRow mRow = (TableRow) tableSearchResult.getChildAt(i);
            TextView userid = (TextView) mRow.getChildAt(3);
            CheckBox mCheckbox = (CheckBox)mRow.getChildAt(2);
            if (mCheckbox.isChecked()) {
                noOfCheckedCheckboxes++;
                userids.add(userid.getText().toString());
            }
        }

        if (noOfCheckedCheckboxes > ErrorMessageVariables.noOfToBeMonitoredUsers) //cant select more than  a certain number of monitors
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

        try
        {

            if(osnName.equals(IntentSwitchVariables.Vine))
            {
                ArrayList<String> alreadyMonitoring = new ArrayList<String>();
                res = databaseHelper.getMonitoring(DatabaseHelper.NAME_TABLE_VINE_MONITORING_USER_TABLE,email);
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
                        long insertionResult = databaseHelper.insertMonitoringTable(email,userids.get(i),DatabaseHelper.NAME_TABLE_VINE_MONITORING_USER_TABLE);
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



            }
        } catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception while looping through the cursor in search result profiles");
            Log.i(UtilityVariables.tag,e.toString());

        }finally {
            if(res != null)
                res.close();
        }
        return IntentSwitchVariables.SearchResultProfiles;

    }





    public void buttonOnClickStartMonitoring(View v)
    {
        Log.i(UtilityVariables.tag," going to check if valid function in search result profiles");
        String whereToSwitch = checkIfValidMonitoringRequest(v);
        Log.i(UtilityVariables.tag," switching to this activity from search result ptofiles activity "+whereToSwitch);
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
