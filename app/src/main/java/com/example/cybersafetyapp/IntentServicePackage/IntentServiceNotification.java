package com.example.cybersafetyapp.IntentServicePackage;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.cybersafetyapp.HelperClassesPackage.APIWorks;
import com.example.cybersafetyapp.ActivityPackage.Notifications;
import com.example.cybersafetyapp.ClassifierPackage.Classifier;
import com.example.cybersafetyapp.HelperClassesPackage.Comment;
import com.example.cybersafetyapp.HelperClassesPackage.CommentFeedback;
import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.HelperClassesPackage.Post;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;


public class IntentServiceNotification extends IntentService {
    private static DatabaseHelper databaseHelper;
    String email;
    //static boolean  instagramNewCommentsFound = false;
    //static boolean  vineNewCommentsFound = false;
    Classifier classifier;
    private int instagramNumberOfBullying = 0;
    ArrayList<CommentFeedback>feedbackList;


    public IntentServiceNotification() {
        super("IntentServiceNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle messages = intent.getExtras();
        //Log.i(UtilityVariables.tag,"Inside "+getClass().getSimpleName()+" email is: "+messages.getString(IntentSwitchVariables.email));
        this.email = messages.getString(IntentSwitchVariables.EMAIL);
        try {
            this.classifier = Classifier.getInstance(this);

        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,e.toString());
        }
        instagramNotificationCheck();
        //createNotification(this, "Possible Bullying!!", "On Instagram!!"+this.instagramNumberOfBullying+" instances", "Possible Bullying!");

        if(this.instagramNumberOfBullying > 0)
        {
            createNotification(this, "Possible Bullying!!", "On Instagram!!"+this.instagramNumberOfBullying+" instances", "Possible Bullying!");
            this.instagramNumberOfBullying = 0;
        }



    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert)
    {
        Intent intent = new Intent(context,Notifications.class);
        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
        intent.putExtra(IntentSwitchVariables.FEEDBACK_COMMENT_LIST, this.feedbackList);

        PendingIntent notificationIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msg)
                        .setContentText(msgAlert).setContentText(msgText);

        mBuilder.setContentIntent(notificationIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,mBuilder.build());
    }

    private void instagramNotificationCheck()
    {
        this.feedbackList = new ArrayList<>();
        IntentServiceNotification.databaseHelper = new DatabaseHelper(this);

        // first for instagram

        // getting the instagram userids that are being monitored
        // then getting any new posts that have been shared and insert the postids in the database
        ArrayList<String> useridList = getUsersFromDB(DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_USER_TABLE);
        ArrayList<String> monitoringPostList = IntentServiceNotification.databaseHelper.getMonitoringPostIDsByEmail(this.email,DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE);
        String accessToken = IntentServiceNotification.databaseHelper.getAccessTokenForGuardian(this.email,DatabaseHelper.NAME_COL_INSTAGRAM_TOKEN);
        APIWorks apiworks = APIWorks.getInstance();
        ArrayList<Post> posts = null;
        for(String userid:useridList) {
            posts = apiworks.instagramGettingUserPosts(userid, accessToken);
        }
        if(posts != null)
        {
            for(Post post:posts)
            {
                if(!monitoringPostList.contains(post.postid))
                {
                    IntentServiceNotification.databaseHelper.insertMonitoringPostTable(post,DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE,this.email);
                }
            }
        }
        Hashtable<String,String> monitoringPosts = IntentServiceNotification.databaseHelper.getMonitoringPostIDLastTimeCheckedByEmail(this.email,DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE);
        Set<String> postids = monitoringPosts.keySet();
        for(String postid:postids)
        {
            Long lastCheckedTime = Long.parseLong(monitoringPosts.get(postid));
            ArrayList<Comment> comments = apiworks.instagramGettingPostComments(postid,accessToken);
            ArrayList<String> newComments = new ArrayList<>();
            Long temp = lastCheckedTime;
            for(Comment comment :comments)
            {
                if((Long.parseLong(comment.createdtime)) > lastCheckedTime)
                {
                    //instagramNewCommentsFound = true;
                    newComments.add(comment.commentText);
                    temp = Long.parseLong(comment.createdtime);
                }

            }
            if(!lastCheckedTime.equals(temp)) {
                IntentServiceNotification.databaseHelper.updateLastTimeCheckedForPost(DatabaseHelper.NAME_TABLE_INSTAGRAM_MONITORING_POST_TABLE, postid, temp.toString());
            }
            if(newComments.size()>0)
            {
                CommentFeedback commentFeedback = new CommentFeedback();

                double[]featureValues = this.classifier.getFeatureValues(newComments);
                double prediction = this.classifier.predict(featureValues);

                commentFeedback.comments.addAll(newComments);
                commentFeedback.featureValues = featureValues;
                commentFeedback.predictedValue = prediction;
                this.feedbackList.add(commentFeedback);
                Log.i(UtilityVariables.tag,"Feature values in "+this.getClass().getSimpleName()+" : "+ Arrays.toString(featureValues));
                Log.i(UtilityVariables.tag,"Prediction value in "+this.getClass().getSimpleName()+" : "+ prediction);

                this.instagramNumberOfBullying++;
                if (Math.round(prediction) == 1)
                {

                    commentFeedback.classifierResult= "Bullying";

                }
                else
                {
                    commentFeedback.classifierResult= "Not Bullying";
                }
            }
        }
    }


    private ArrayList<String> getUsersFromDB(String tableName)
    {
        ArrayList<String> useridList = new ArrayList<>();
        Hashtable<String,String> users = IntentServiceNotification.databaseHelper.getMonitoringInformationDetailByGuardianEmail(tableName,this.email);
        Set<String> keys = users.keySet();
        for(String key: keys){
            useridList.add(key);
        }

        return useridList;
    }


}
