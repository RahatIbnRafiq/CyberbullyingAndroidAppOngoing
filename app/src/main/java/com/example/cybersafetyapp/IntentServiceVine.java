package com.example.cybersafetyapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.example.cybersafetyapp.IntentSwitchVariables.url;

public class IntentServiceVine extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    DatabaseHelper databaseHelper;


    public IntentServiceVine()
    {
        super("IntentServiceVine");
    }



    private void getUserSearchResult(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.receiver);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            String urlString = UtilityVariables.VINE_URL_USER_SEARCH+ intent.getStringExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED)+
                    "?vine-session-id="+intent.getStringExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN)+"&size=5";

            URL url = new URL(urlString);
            //Log.i(UtilityVariables.tag,"url string for vine user search "+urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setRequestProperty("vine-session-id",intent.getStringExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN));


            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            bundle.putString(IntentSwitchVariables.VINE_USER_SEARCH_RESULT_JSON,response);
            bundle.putString(IntentSwitchVariables.VINE_ACCESS_TOKEN,intent.getStringExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN));
            bundle.putString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,intent.getStringExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED));
            bundle.putInt(IntentSwitchVariables.request,IntentSwitchVariables.REQUEST_VINE_USER_SEARCH);
            receiver.send(STATUS_FINISHED, bundle);
            //Log.i(UtilityVariables.tag,"vine user search response: "+response);


        }catch (Exception ex)
        {
            ex.printStackTrace();
            Log.i(UtilityVariables.tag,"error :"+IntentServiceVine.class+"  :"+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }


    private void getUserInformation(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.receiver);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            String urlString = UtilityVariables.VINE_URL_USER_DETAIL+ intent.getStringExtra(IntentSwitchVariables.USERID)+
                    "?vine-session-id="+intent.getStringExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN);

            URL url = new URL(urlString);
            //Log.i(UtilityVariables.tag,"url string for vine user search "+urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setRequestProperty("vine-session-id",intent.getStringExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN));


            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            //Log.i(UtilityVariables.tag,"User information found for vine: "+response);
            bundle.putString(IntentSwitchVariables.VINE_USER_DETAIL_RESULT_JSON,response);
            bundle.putString(IntentSwitchVariables.USERID,intent.getStringExtra(IntentSwitchVariables.USERID));
            bundle.putString(IntentSwitchVariables.VINE_ACCESS_TOKEN,intent.getStringExtra(IntentSwitchVariables.VINE_ACCESS_TOKEN));
            bundle.putInt(IntentSwitchVariables.request,IntentSwitchVariables.REQUEST_VINE_USER_DETAIL);
            receiver.send(STATUS_FINISHED, bundle);
            //Log.i(UtilityVariables.tag,"vine user search response: "+response);
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in getUserInformation function in IntentServiceVine : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }


    private String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            }
            finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    private void getAccessToken(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.receiver);
        Bundle bundle = new Bundle();
        databaseHelper = new DatabaseHelper(this);
        try {

            URL url = new URL("https://community-vineapp.p.mashape.com/users/authenticate");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("X-Mashape-Key","gxShFmp7TCmshoB0O4PP8ya9QQfcp1pKUeBjsnzWJltjpv0o7B");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept","application/json");

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("username", intent.getStringExtra(IntentSwitchVariables.email))
                    .appendQueryParameter("password", intent.getStringExtra(IntentSwitchVariables.password));
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            String response = streamToString(conn.getInputStream());
            String token = new JSONObject(response).getJSONObject("data").getString("key");
            Log.i(UtilityVariables.tag,"got token value:  :"+token);
            bundle.putString(IntentSwitchVariables.VINE_ACCESS_TOKEN, token);
            receiver.send(STATUS_FINISHED, bundle);
        }catch (Exception ex)
        {
            ex.printStackTrace();
            Log.i(UtilityVariables.tag,"error :"+IntentServiceVine.class+"  :"+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        int requestType = intent.getIntExtra(IntentSwitchVariables.request,0);
        if(requestType == IntentSwitchVariables.REQUEST_VINE_ACCESS_TOKEN)
        {
            getAccessToken(intent);
        }

        else if(requestType == IntentSwitchVariables.REQUEST_VINE_USER_SEARCH)
        {
            getUserSearchResult(intent);
        }

        else if (requestType == IntentSwitchVariables.REQUEST_VINE_USER_DETAIL)
        {
            getUserInformation(intent);
        }
    }
}
