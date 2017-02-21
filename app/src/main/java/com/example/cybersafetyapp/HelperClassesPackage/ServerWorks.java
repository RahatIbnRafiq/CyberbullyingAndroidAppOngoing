package com.example.cybersafetyapp.HelperClassesPackage;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by RahatIbnRafiq on 2/20/2017.
 */

public class ServerWorks {
    private static ServerWorks instance = null;


    public static ServerWorks getInstance()
    {
        if(instance == null) {
            instance = new ServerWorks();
        }
        return instance;
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
                    Log.i(UtilityVariables.tag,"IO Exception in "+this.getClass().getSimpleName());
                }

            }

            str = sb.toString();
        }

        return str;
    }


    public ArrayList<String> getMonitoringUsers(String email,String OSNname) throws Exception
    {
        ArrayList<String> userids = new ArrayList<>();
        if(OSNname == IntentSwitchVariables.INSTAGRAM)
        {
            InputStream inputStream ;
            HttpURLConnection urlConnection ;
            String urlString = UtilityVariables.INSTAGRAM_GET_MONITORING_USERS+"?email="+email;
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = streamToString(inputStream);
            //Log.i(UtilityVariables.tag,this.getClass().getSimpleName()+" this is the getMonitoringUsers Response: "+response);
            JSONObject resultjson = new JSONObject(response);
            JSONArray jsonArray = new JSONArray(resultjson.optString("users"));
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                userids.add(jsonObject.optString("userid"));
                //Log.i(UtilityVariables.tag,this.getClass().getSimpleName()+" userid: "+jsonObject.optString("userid"));
            }

        }
        return userids;
    }

}
