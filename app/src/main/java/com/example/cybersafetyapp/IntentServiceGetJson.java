package com.example.cybersafetyapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.Html;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        if (null != inputStream) {
            inputStream.close();
        }
        return result;
}

    private String[] parseVineUserSearchResult(String result) {
        String[] searchData = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.getJSONObject("data");
            JSONArray records = data.optJSONArray("records");
            searchData = new String[records.length()-1];
            for(int i=0;i<records.length()-1;i++)
            {
                JSONObject record = records.optJSONObject(i);
                StringBuffer str = new StringBuffer();
                str.append(record.optString("username"));
                str.append(",");

                str.append(record.optString("userId"));
                str.append(",");

                str.append(record.optString("avatarUrl"));
                str.append(",");

                str.append(record.optString("location"));
                searchData[i] = str.toString();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return searchData;
    }


    private String[] getJSONObjectFromURL(String requestUrl,int requestType) throws IOException, JSONException {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        URL url = new URL(requestUrl);

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        int statusCode = urlConnection.getResponseCode();

        if(statusCode == 200)
        {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            if(requestType == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH) {
                String[] results = parseVineUserSearchResult(response);
                return results;
            }
            return null;
        }
        else
        {
            return null;
        }
    }

    private String getHTMLResponseFromURL(String url)
    {
        try {
            Document doc  = Jsoup.connect(url).get();
            Elements trWithStartingClassMyClass = doc.select("div[data-react-class^=UserBox]");
            //Log.i(UtilityVariables.tag,"success while getting html response for url "+url);
            return trWithStartingClassMyClass.toString();

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception while getting html response for url "+url);
            Log.i(UtilityVariables.tag,e.toString());
        }

        return null;

    }



    @Override
    protected void onHandleIntent(Intent intent)
    {
        final ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.receiver);
        String url = intent.getStringExtra(IntentSwitchVariables.url);
        int requestType = intent.getIntExtra(IntentSwitchVariables.request,0);
        String osnName = intent.getStringExtra(IntentSwitchVariables.OSNName);

        Bundle bundle = new Bundle();
        bundle.putInt(IntentSwitchVariables.request,requestType);
        bundle.putString(IntentSwitchVariables.OSNName,osnName);
        if(osnName.equals(IntentSwitchVariables.INSTAGRAM) && requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
        {
            String[] searchData = null;
            try {
                //Log.i(UtilityVariables.tag, "Request type is instagram user search in intentservice json");
                String userSearchHtml = getHTMLResponseFromURL(url);
                int index1 = userSearchHtml.indexOf("data-react-props") + 18;
                int index2 = userSearchHtml.length() - 8;
                userSearchHtml = userSearchHtml.substring(index1, index2);
                userSearchHtml = Jsoup.parse(userSearchHtml).text().toString();

                JSONObject response = new JSONObject(userSearchHtml);
                JSONArray data = response.getJSONArray("data");
                searchData = new String[data.length()];
                for(int i=0;i<data.length();i++) {
                    JSONObject record = data.optJSONObject(i);
                    StringBuffer str = new StringBuffer();
                    str.append(record.optString("username"));
                    str.append(",");

                    str.append(record.optString("id"));
                    str.append(",");

                    str.append(record.optString("profile_picture"));
                    searchData[i] = str.toString();
                    //Log.i(UtilityVariables.tag, str.toString());
                }
                bundle.putStringArray(IntentSwitchVariables.JsonResult, searchData);
                receiver.send(STATUS_FINISHED, bundle);

            }catch (Exception e)
            {
                Log.i(UtilityVariables.tag, "Exception in intagram user search parsing to json "+e.toString());
            }


        }
        else {
            try {
                String[] jsonResult = getJSONObjectFromURL(url, requestType);
                if (jsonResult != null && jsonResult.length > 0) {
                    bundle.putStringArray(IntentSwitchVariables.JsonResult, jsonResult);
                    receiver.send(STATUS_FINISHED, bundle);
                } else {
                    bundle.putString(Intent.EXTRA_TEXT, IntentSwitchVariables.NoUsersFound);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
    }
}

