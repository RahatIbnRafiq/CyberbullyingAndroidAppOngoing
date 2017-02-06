package com.example.cybersafetyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ArrayList<CommentFeedback> feedbackCommentList = this.getIntent().getParcelableArrayListExtra(IntentSwitchVariables.FEEDBACK_COMMENT_LIST);
        if(feedbackCommentList != null)
        {
            for(int i=0;i<feedbackCommentList.size();i++)
            {
                Log.i(UtilityVariables.tag,"Notification page:");
                for(int j=0;j<feedbackCommentList.get(i).comments.size();j++)
                {
                    Log.i(UtilityVariables.tag,feedbackCommentList.get(i).comments.get(j).toString());
                }
            }
        }


    }


    public void clickSamplecomments(View v)
    {
        Toast.makeText(Notifications.this,"Sample Comments go here",Toast.LENGTH_SHORT).show();
    }

    public void clickWholeMediaSession(View v)
    {
        Toast.makeText(Notifications.this,"Whole media session goes here",Toast.LENGTH_SHORT).show();
    }
}
