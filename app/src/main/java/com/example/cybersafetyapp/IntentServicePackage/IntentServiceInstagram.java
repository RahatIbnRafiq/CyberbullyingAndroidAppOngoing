package com.example.cybersafetyapp.IntentServicePackage;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class IntentServiceInstagram extends IntentService {

    public static int GET_TOKEN = 1;
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    //private static final String API_URL = "https://api.instagram.com/v1";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    //private DatabaseHelper databaseHelper;
    private String classname = this.getClass().getSimpleName();


    public IntentServiceInstagram() {
        super("IntentServiceInstagram");
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
                try {
                    is.close();
                }catch (IOException ex)
                {
                    Log.i(UtilityVariables.tag,this.classname+" IO Exception streamToString function");
                }
            }

            str = sb.toString();
        }

        return str;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }


    private void instagramUserSearch(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {
            String accessToken = intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN);
            String username = intent.getStringExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            InputStream inputStream ;
            HttpURLConnection urlConnection;

            URL url = new URL("https://api.instagram.com/v1/users/search/?q="+username+"&access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            //Log.i(UtilityVariables.tag,this.classname+"instagramUserSearch function: status code "+statusCode);
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                bundle.putString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED, intent.getStringExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED));
                bundle.putInt(IntentSwitchVariables.REQUEST,IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_USER_SEARCH_RESULT_JSON,response);
                receiver.send(STATUS_FINISHED, bundle);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(UtilityVariables.tag,"error in instagramUserSearch function "+this.classname+"  :"+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }

    private void getUserInformation(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {

            String accessToken = intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN);
            String userid = intent.getStringExtra(IntentSwitchVariables.USERID);


            InputStream inputStream;
            HttpURLConnection urlConnection;

            URL url = new URL("https://api.instagram.com/v1/users/"+userid+"/?access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            //Log.i(UtilityVariables.tag,this.classname+" getUserInformation function: status code "+statusCode);
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                //Log.i(UtilityVariables.tag,this.classname+" getUserInformation function: User information found for instagram: "+response);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                bundle.putString(IntentSwitchVariables.USERID, userid);
                bundle.putInt(IntentSwitchVariables.REQUEST,IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_USER_DETAIL_RESULT_JSON,response);
                receiver.send(STATUS_FINISHED, bundle);
            }
            else {
                receiver.send(STATUS_ERROR, bundle);

            }
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+": getUserInformation function: Exception in getUserInformation function in IntentServiceInstagram : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }

    private void getAccessToken(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        String codeUrl = intent.getStringExtra(IntentSwitchVariables.URL);
        //String email = intent.getStringExtra(IntentSwitchVariables.EMAIL);
        String code = codeUrl.split("=")[1];
        Bundle bundle = new Bundle();

        try {
            URL url = new URL(TOKEN_URL);
            Log.i(UtilityVariables.tag, this.classname+": getAccessToken function: Opening Token URL " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write("client_id="+UtilityVariables.INSTAGRAM_CLIENT_ID+
                    "&grant_type=authorization_code" +
                    "&redirect_uri="+UtilityVariables.INSTAGRAM_CALLBACK_URL+
                    "&code=" + code);
            writer.flush();
            String response = streamToString(urlConnection.getInputStream());
            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

            String mAccessToken = jsonObj.getString("access_token");
            Log.i(UtilityVariables.tag, this.classname+":getAccessToken function: Got access token: " + mAccessToken);
            bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, mAccessToken);
            receiver.send(STATUS_FINISHED, bundle);

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(UtilityVariables.tag,this.classname+":getAccessToken Function: error in getAccessToken function "+IntentServiceInstagram.class+"  :"+ex.toString());
            bundle.putString(Intent.EXTRA_TEXT, ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }

    private void getUserPostInformation(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {

            String accessToken = intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN);
            String userid = intent.getStringExtra(IntentSwitchVariables.USERID);


            InputStream inputStream ;
            HttpURLConnection urlConnection ;

            URL url = new URL("https://api.instagram.com/v1/users/"+userid+"/media/recent/?access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            Log.i(UtilityVariables.tag,this.classname+": getUserPostInformation function: status code "+statusCode);
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                //Log.i(UtilityVariables.tag,"User's post information found for instagram: "+response);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                bundle.putString(IntentSwitchVariables.USERID, userid);
                bundle.putInt(IntentSwitchVariables.REQUEST,IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_POST_INFORMATION_RESULT_JSON,response);
                receiver.send(STATUS_FINISHED, bundle);
            }
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+":getUserPostInformation function: Exception in getUserPostInformation function in IntentServiceInstagram : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }


    private void getUserPostDetail(Intent intent)
    {
        ResultReceiver receiver = intent.getParcelableExtra(IntentSwitchVariables.RECEIVER);
        Bundle bundle = new Bundle();
        try {

            String accessToken = intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN);
            String postid = intent.getStringExtra(IntentSwitchVariables.POSTID);


            InputStream inputStream ;
            HttpURLConnection urlConnection;

            URL url = new URL("https://api.instagram.com/v1/media/"+postid+"/comments/?access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            Log.i(UtilityVariables.tag,this.classname+":getUserPostDetail function: status code "+statusCode);
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                //Log.i(UtilityVariables.tag,"User's post comments found for instagram: "+response);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN, intent.getStringExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN));
                bundle.putString(IntentSwitchVariables.POSTID, postid);
                bundle.putInt(IntentSwitchVariables.REQUEST,IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS);
                bundle.putString(IntentSwitchVariables.INSTAGRAM_POST_DETAILS_RESULT_JSON,response);
                receiver.send(STATUS_FINISHED, bundle);
            }
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,this.classname+":getUserPostDetail Function: Exception in getUserPostDetail function in IntentServiceInstagram : "+ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int requestType = intent.getIntExtra(IntentSwitchVariables.REQUEST,0);
        if(requestType == GET_TOKEN)
        {
            getAccessToken(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
        {
            instagramUserSearch(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_DETAIL)
        {
            getUserInformation(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_INFORMATION)
        {
            getUserPostInformation(intent);
        }
        else if (requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_POST_DETAILS)
        {
            getUserPostDetail(intent);
        }




    }

}
