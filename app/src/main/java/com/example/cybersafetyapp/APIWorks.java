package com.example.cybersafetyapp;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RahatIbnRafiq on 1/19/2017.
 */

public class APIWorks {

    private static APIWorks instance = null;


    public static APIWorks getInstance()
    {
        if(instance == null) {
            instance = new APIWorks();
        }
        return instance;
    }

    public void instagramUserPostInformation(String userid,String accessToken)
    {
        try {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            URL url = new URL("https://api.instagram.com/v1/users/"+userid+"/media/recent/?access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            //Log.i(UtilityVariables.tag,"status code "+statusCode);
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                //Log.i(UtilityVariables.tag,"User's post information found for instagram: "+response);
                JSONObject resultjson = new JSONObject(response);
                JSONArray userdata = resultjson.optJSONArray("data");
                for(int i=0;i<userdata.length();i++)
                {
                    JSONObject record = userdata.optJSONObject(i);
                    String postid = record.optString("id");
                    String link = record.optString("link");
                    String createdTime = record.optString("created_time");
                    Log.i(UtilityVariables.tag,postid+","+link+","+createdTime);

                }
            }
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in getUserPostInformation function in APIWorks class : "+ex.toString());
        }

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
}
