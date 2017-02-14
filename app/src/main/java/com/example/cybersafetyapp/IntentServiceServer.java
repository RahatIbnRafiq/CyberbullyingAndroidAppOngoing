package com.example.cybersafetyapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IntentServiceServer extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;


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
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }


    private void registerGuardian(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.receiver);
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
            data.put("email",intent.getStringExtra(IntentSwitchVariables.email));
            data.put("password",intent.getStringExtra(IntentSwitchVariables.password));
            data.put("phone_number",intent.getStringExtra(IntentSwitchVariables.phone_number));
            writer.write(data.toString());
            writer.flush();
            String response = streamToString(urlConnection.getInputStream());
            Log.i(UtilityVariables.tag,"Response from register request: "+response);
            JSONObject resultjson = new JSONObject(response);
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS,resultjson.optString("success"));
            bundle.putString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE,resultjson.optString("message"));
            receiver.send(STATUS_FINISHED, bundle);


        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in registerGuardian function in IntentServiceServer : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }
    private void loginGuardian(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.receiver);
        Bundle bundle = new Bundle();
        try {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            String urlString = UtilityVariables.LOGIN_GUARDIAN+"?email="+intent.getStringExtra(IntentSwitchVariables.email)+
                    "&password="+intent.getStringExtra(IntentSwitchVariables.password);
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


    @Override
    protected void onHandleIntent(Intent intent) {
        int requestType = intent.getIntExtra(IntentSwitchVariables.request,0);
        Log.i(UtilityVariables.tag,"Inside intentServiceServer class now.request type: "+requestType);
        if(requestType == IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_LOGIN)
        {
            //Log.i(UtilityVariables.tag,"login intentservice server the request is  login request.");
            loginGuardian(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_REGISTER)
        {
            registerGuardian(intent);
        }


    }

}
