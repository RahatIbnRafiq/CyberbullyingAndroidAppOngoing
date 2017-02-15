package com.example.cybersafetyapp.HelperClassesPackage;
import android.util.Log;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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



    public ArrayList<Comment> instagramGettingPostComments(String postid, String accessToken)
    {

        try {
            ArrayList<Comment>comments = new ArrayList<>();
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            URL url = new URL("https://api.instagram.com/v1/media/"+postid+"/comments?access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                JSONObject resultjson = new JSONObject(response);
                JSONArray data = resultjson.optJSONArray("data");
                for(int i=0;i<data.length();i++)
                {
                    JSONObject record = data.optJSONObject(i);
                    String commentid = record.optString("id");
                    String createdTime = record.optString("created_time");
                    String commentText = record.optString("text");
                    String fromUserName = record.getJSONObject("from").getString("username");
                    //Log.i(UtilityVariables.tag,commentid+","+commentText+","+createdTime);
                    comments.add(new Comment(commentid,createdTime,commentText,fromUserName));

                }
            }
            return comments;
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in instagramGettingPostComments function in APIWorks class : "+ex.toString());
            return null;

        }

    }

    public ArrayList<Post> instagramGettingUserPosts(String userid, String accessToken)
    {
        try {
            ArrayList<Post>posts = new ArrayList<>();
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            URL url = new URL("https://api.instagram.com/v1/users/"+userid+"/media/recent/?access_token="+accessToken);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            if(statusCode == 200)
            {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                JSONObject resultjson = new JSONObject(response);
                JSONArray userdata = resultjson.optJSONArray("data");
                for(int i=0;i<userdata.length();i++)
                {
                    JSONObject record = userdata.optJSONObject(i);
                    String postid = record.optString("id");
                    String link = record.optString("link");
                    String createdTime = record.optString("created_time");
                    //Log.i(UtilityVariables.tag,postid+","+link+","+createdTime);
                    posts.add(new Post(postid,createdTime,"0",link));

                }
            }
            return posts;
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in instagramUserPostInformation function in APIWorks class : "+ex.toString());
            return null;

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
