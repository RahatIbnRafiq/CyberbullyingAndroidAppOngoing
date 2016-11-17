package com.example.cybersafetyapp;

import android.content.Intent;
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

import java.io.InputStream;

public class SearchResultProfiles extends AppCompatActivity {
    private static String tag  = "cybersafetyapp";
    private static String logmsg ="SearchResultProfiles:";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_profiles);
        Bundle messages = getIntent().getExtras();
        Log.i(tag,logmsg+" Inside the SearchResultProfiles now. Extracting the data from the intent");
        String [] results = messages.getStringArray("searchResult");
        int requestType = messages.getInt("request");

        TableLayout tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
        if(requestType == SearchByUserName.REQUEST_VINE_USER_SEARCH)
        {
            Log.i(tag,logmsg+" result type is vine user search.");
            for (int i = 0; i < results.length; i++)
            {
                String[]data = results[i].split(",");
                String username = data[0];
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


                tableSearchResult.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                profilePicture.getLayoutParams().height = 100;
                profilePicture.getLayoutParams().width = 100;
            }
        }

    }

    public void buttonOnClickStartMonitoring(View v)
    {
        Log.i(tag,logmsg+": Start monitoring button has been clicked.");
        TableLayout tableSearchResult = (TableLayout) findViewById(R.id.TableSearchResult);
        for(int i=0;i<tableSearchResult.getChildCount();i++)
        {
            TableRow mRow = (TableRow) tableSearchResult.getChildAt(i);
            TextView mTextView = (TextView) mRow.getChildAt(0);
            CheckBox mCheckbox = (CheckBox)mRow.getChildAt(2);
            Log.i(tag,logmsg+": Username:"+mTextView.getText().toString()+", Checked: "+mCheckbox.isChecked());
        }

        //intent = new Intent("com.example.cybersafetyapp.Dashboard");
        //startActivity(intent);
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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
