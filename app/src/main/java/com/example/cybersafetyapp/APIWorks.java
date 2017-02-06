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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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



    public ArrayList<Comment> instagramGettingPostComments(String postid,String accessToken)
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
                    Log.i(UtilityVariables.tag,commentid+","+commentText+","+createdTime);
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

    public ArrayList<Post> instagramGettingUserPosts(String userid,String accessToken)
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



    public ArrayList<Post> vineGettingUserPosts(String userid, String accessToken)
    {
        try {
            ArrayList<Post>posts = new ArrayList<>();
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            String urlString = UtilityVariables.VINE_URL_USER_TIMELINE+ userid+ "?vine-session-id="+accessToken;
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);


            JSONObject resultjson = new JSONObject(response);
            JSONArray resultarray = resultjson.getJSONObject("data").getJSONArray("records");
            for(int i=0;i<resultarray.length();i++)
            {
                JSONObject postData = resultarray.getJSONObject(i);
                String postid = postData.optString("postId");
                String link = postData.optString("shareUrl");
                String created = postData.optString("created");
                created = created.substring(0,created.indexOf("."));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Long createdTime = format.parse(created).getTime();
                posts.add(new Post(postid,createdTime.toString(),"0",link));

            }
            return posts;
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in vineGettingUserPosts function in APIWorks : "+ex.toString());
            return null;
        }
    }

    public ArrayList<Comment> vineGettingPostComments(String postid,String accessToken)
    {
        try {
            ArrayList<Comment>comments = new ArrayList<>();
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            String urlString = UtilityVariables.VINE_URL_POST_COMMENTS+ postid+ "/comments?vine-session-id="+accessToken;
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);

            JSONObject resultjson = new JSONObject(response);
            JSONArray resultarray = resultjson.getJSONObject("data").getJSONArray("records");

            for(int i=0;i<resultarray.length();i++)
            {
                String commentid = resultarray.getJSONObject(i).getString("commentId");
                String created = resultarray.getJSONObject(i).getString("created");
                String commentText = resultarray.getJSONObject(i).getString("comment");
                String fromUserName = resultarray.getJSONObject(i).getString("username");


                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Long createdTime = format.parse(created).getTime();

                comments.add(new Comment(commentid,createdTime.toString(),commentText,fromUserName));
            }

            return comments;
        }catch (Exception ex)
        {
            Log.i(UtilityVariables.tag,"Exception in vineGettingPostComments function in APIWorks : "+ex.toString());
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
