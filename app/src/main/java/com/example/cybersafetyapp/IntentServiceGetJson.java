package com.example.cybersafetyapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RahatIbnRafiq on 11/15/2016.
 */

public class IntentServiceGetJson extends IntentService {
    private String tag = "cybersafetyapp";
    private String logmsg = this.getClass().getName().toString();
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public IntentServiceGetJson()
    {
        super("IntentServiceGetJson");
    }
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        Log.i(tag,logmsg+": Converting input stream to string");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

            /* Close Stream */
        if (null != inputStream) {
            Log.i(tag,logmsg+": Closing input stream");
            inputStream.close();
        }
        Log.i(tag,logmsg+": Conversion of stream to string is done. Getting out.");


        return result;
    }

    private String[] parseResult(String result) {

        Log.i(tag,logmsg+": Parsing the result to json.");

        String[] users = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.getJSONObject("data");
            JSONArray records = data.optJSONArray("records");
            users = new String[records.length()-1];
            for(int i=0;i<records.length()-1;i++)
            {
                JSONObject record = records.optJSONObject(i);
                String username = record.optString("username");
                users[i] = username;
            }


        } catch (JSONException e) {
            Log.i(tag,logmsg+": Exception happend while parsing into json. "+e.toString());
            e.printStackTrace();
        }

        Log.i(tag,logmsg+": Parsing is done.");

        return users;
    }


    private String[] getJSONObjectFromURL(String requestUrl) throws IOException, JSONException {
        Log.i(tag,logmsg+": Starting to get the input stream");

        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        URL url = new URL(requestUrl);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        int statusCode = urlConnection.getResponseCode();

        if(statusCode == 200)
        {
            Log.i(tag,logmsg+": Status code is a success. Getting the stream now.");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            String[] results = parseResult(response);
            Log.i(tag,logmsg+": Returning the json.");
            return results;
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i(tag,logmsg+": Starting the intentservicegetjson intent now.");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();

        try {
            String[] jsonResult = getJSONObjectFromURL(url);
            Log.i(tag,logmsg+": Got the json results.");

            if (jsonResult != null && jsonResult.length > 0) {
                bundle.putStringArray("jsonResult", jsonResult);
                Log.i(tag,logmsg+": Sending the result back from intent to activity");
                receiver.send(STATUS_FINISHED, bundle);
            }
            else
            {
                Log.i(tag,logmsg+": No Users have been found.");
                bundle.putString(Intent.EXTRA_TEXT, "No Users Found. Try again with a different username.");
                receiver.send(STATUS_FINISHED, bundle);
            }
        } catch (Exception e) {
            Log.i(tag,logmsg+": Exception happened while getting users. "+e.toString());
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }
}

