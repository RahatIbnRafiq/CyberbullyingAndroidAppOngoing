package com.example.cybersafetyapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {

    private ArrayList<CommentFeedback> feedbackCommentList;
    private String email = null;
    private TableLayout tablenotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Intent intent = getIntent();
        this.feedbackCommentList = null;
        this.feedbackCommentList = (ArrayList<CommentFeedback>) intent.getSerializableExtra(IntentSwitchVariables.FEEDBACK_COMMENT_LIST);
        this.email = intent.getStringExtra(IntentSwitchVariables.email);
        Log.i(UtilityVariables.tag,this.getClass().getSimpleName());

        showNotifications();


    }

    public void onClickButtonGoBackToDashboard(View v)
    {
        Log.i(UtilityVariables.tag,"Go back to dashboard button has been clicked.");
        Intent intent = new Intent(this,Dashboard.class);
        intent.putExtra(IntentSwitchVariables.email,this.email);
        intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().toString());
        intent.removeExtra(IntentSwitchVariables.FEEDBACK_COMMENT_LIST);
        startActivity(intent);
    }


    private void addDataToRow(int i)
    {
        TableRow tr_head = new TableRow(this);
        tr_head.setId(i);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));


        TextView textViewComments = new TextView(this);
        ArrayList<String>comments = this.feedbackCommentList.get(i).comments;
        StringBuilder sb = new StringBuilder();
        for(int j=0;j<comments.size();j++) {
            sb.append(comments.get(j).toString() + "\n");
        }
        Log.i(UtilityVariables.tag,this.getClass().getSimpleName()+": comments: "+sb.toString());
        textViewComments.setText(sb.toString());
        textViewComments.setTextColor(Color.WHITE);
        textViewComments.setPadding(0,2,0,1);
        tr_head.addView(textViewComments);

        this.tablenotifications.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }

    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 0) {
            table.removeViews(1, childCount - 1);
        }
    }


    private void showNotifications()
    {
        if (this.feedbackCommentList != null)
        {
            Log.i(UtilityVariables.tag,"feedbacklist size got in notification page: "+this.feedbackCommentList.size());
            this.tablenotifications = (TableLayout) findViewById(R.id.TableNotifications);
            cleanTable(this.tablenotifications);
            for(int i=0;i<this.feedbackCommentList.size();i++)
            {
                addDataToRow(i);
            }
        }
    }
}
