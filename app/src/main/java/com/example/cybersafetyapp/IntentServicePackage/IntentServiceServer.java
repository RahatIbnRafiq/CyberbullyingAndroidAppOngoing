package com.example.cybersafetyapp.IntentServicePackage;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class IntentServiceServer extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private String classname = this.getClass().getSimpleName();


    public IntentServiceServer() {
        super("IntentServiceServer");
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
                try{
                    is.close();
                }catch (IOException ex)
                {
                    Log.i(UtilityVariables.tag,"IO Exception in "+this.classname);
                }

            }

            str = sb.toString();
        }

        return str;
    }

    private void instagramGetAccessToken(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream ;
            HttpURLConnection urlConnection ;
            String urlString = UtilityVariables.INSTAGRAM_GET_ACCESS_TOKEN+"?code="+intent.getStringExtra(IntentSwitchVariables.SERVER_INSTAGRAM_ACCESS_TOKEN_CODE);
            Log.i(UtilityVariables.tag,this.classname+": instagramGetAccessToken function code url to get access token in "+urlString);
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            //Log.i(UtilityVariables.tag,this.classname+":instagramGetAccessToken function: access token request response:"+ response);
            JSONObject resultjson = new JSONObject(response);
            if(resultjson.optString("success") != null && resultjson.optString("success").equals("success")) {
                resultjson = new JSONObject(resultjson.optString("token"));
                bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, resultjson.optString("access_token"));
            }
            else
            {
                bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, null);
            }
            receiver.send(STATUS_FINISHED, bundle);

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+": instagramGetAccessToken function: Exception in instagramGetAccessToken function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }


    private void registerGuardian(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try{
            URL url = new URL(UtilityVariables.REGISTER_GUARDIAN);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            JSONObject data = new JSONObject();
            data.put("email",intent.getStringExtra(IntentSwitchVariables.EMAIL));
            data.put("password",intent.getStringExtra(IntentSwitchVariables.PASSWORD));
            data.put("phone_number",intent.getStringExtra(IntentSwitchVariables.PHONE_NUMBER));
            writer.write(data.toString());
            writer.flush();
            String response = streamToString(urlConnection.getInputStream());
            Log.i(UtilityVariables.tag,this.classname+": registerGuardian function : Response from register request: "+response);
            JSONObject resultjson = new JSONObject(response);
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS,resultjson.optString("success"));
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE,resultjson.optString("message"));
            receiver.send(STATUS_FINISHED, bundle);


        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+ "Exception in registerGuardian function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }
    private void loginGuardian(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream ;
            HttpURLConnection urlConnection ;
            String urlString = UtilityVariables.LOGIN_GUARDIAN+"?email="+intent.getStringExtra(IntentSwitchVariables.EMAIL)+
                    "&password="+intent.getStringExtra(IntentSwitchVariables.PASSWORD);
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            //
            // Log.i(UtilityVariables.tag,"login intentservice server login request response:"+ response);
            JSONObject resultjson = new JSONObject(response);
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS,resultjson.optString("success"));
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE,resultjson.optString("message"));
            receiver.send(STATUS_FINISHED, bundle);

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in loginGuardian function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    private void instagramGetMonitoringCount(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream ;
            HttpURLConnection urlConnection ;
            String urlString = UtilityVariables.INSTAGRAM_GET_MONITORING_COUNT+"?email="+intent.getStringExtra(IntentSwitchVariables.EMAIL);
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            JSONObject resultjson = new JSONObject(response);
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS,resultjson.optString("success"));
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE,resultjson.optString("message"));
            bundle.putString(IntentSwitchVariables.SERVER_INSTAGRAM_MONITORING_COUNT,resultjson.optString("count"));
            bundle.putInt(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_MONITORING_COUNT);
            receiver.send(STATUS_FINISHED, bundle);

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in instagramGetMonitoringCount function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    private void instagramRequestMonitor(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try{
            URL url = new URL(UtilityVariables.INSTAGRAM_MONITOR_USER);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            JSONObject data = new JSONObject();
            data.put("email",intent.getStringExtra(IntentSwitchVariables.EMAIL));
            Log.i(UtilityVariables.tag,"getting count monitoring "+intent.getIntExtra(IntentSwitchVariables.INSTAGRAM_MONITORING_REQUEST_COUNT,0));
            data.put("countRequest",intent.getIntExtra(IntentSwitchVariables.INSTAGRAM_MONITORING_REQUEST_COUNT,0));

            String userids = intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_USERIDS);
            String usernames = intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_USERNAMES);
            String []tokens_userids = userids.split(",");
            String []tokens_usernames = usernames.split(",");
            JSONArray array = new JSONArray();

            for(int i=0; i < tokens_userids.length;i++)
            {
                Log.i(UtilityVariables.tag,tokens_userids[i]+","+tokens_usernames[i]);
                JSONObject temp = new JSONObject();
                temp.put("email",intent.getStringExtra(IntentSwitchVariables.EMAIL));
                temp.put("userid",tokens_userids[i]);
                temp.put("username",tokens_usernames[i]);
                array.put(i,temp);
            }
            data.put("data",array);
            writer.write(data.toString());
            writer.flush();
            String response = streamToString(urlConnection.getInputStream());
            Log.i(UtilityVariables.tag,this.classname+": instagramRequestMonitor function : Response from monitor request: "+response);
            JSONObject resultjson = new JSONObject(response);
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS,resultjson.optString("success"));
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE,resultjson.optString("message"));
            bundle.putInt(IntentSwitchVariables.REQUEST,IntentSwitchVariables.REQUEST_INSTAGRAM_MONITOR_USER);
            receiver.send(STATUS_FINISHED, bundle);


        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+ "Exception in instagramRequestMonitor function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }


    private void instagramGetMonitoringUsers(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream ;
            HttpURLConnection urlConnection ;
            String urlString = UtilityVariables.INSTAGRAM_GET_MONITORING_USERS+"?email="+intent.getStringExtra(IntentSwitchVariables.EMAIL);
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            //Log.i(UtilityVariables.tag,response);
            JSONObject resultjson = new JSONObject(response);
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS,resultjson.optString("success"));
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE,resultjson.optString("message"));
            bundle.putString(IntentSwitchVariables.JSON_RESULT,resultjson.optString("users"));
            bundle.putString(IntentSwitchVariables.OSN_NAME,"Instagram");
            bundle.putInt(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_SERVER_GET_INSTAGRAM_MONITORING_USERS);
            receiver.send(STATUS_FINISHED, bundle);

        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in instagramGetMonitoringUsers function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int requestType = intent.getIntExtra(IntentSwitchVariables.REQUEST,0);
        Log.i(UtilityVariables.tag,"Inside intentServiceServer class now.request type: "+requestType);
        if(requestType == IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_LOGIN)
        {
            loginGuardian(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_REGISTER)
        {
            registerGuardian(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_ACCESS_TOKEN)
        {
            instagramGetAccessToken(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_MONITOR_USER)
        {
            instagramRequestMonitor(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_SERVER_GET_INSTAGRAM_MONITORING_USERS)
        {
            instagramGetMonitoringUsers(intent);
        }


    }

}
