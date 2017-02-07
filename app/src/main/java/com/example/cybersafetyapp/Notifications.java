package com.example.cybersafetyapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class Notifications extends AppCompatActivity implements View.OnClickListener{

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
        //Log.i(UtilityVariables.tag,this.getClass().getSimpleName());

        showNotifications();


    }

    public void onClickButtonGoBackToDashboard(View v)
    {
        Log.i(UtilityVariables.tag,"Go back to dashboard button has been clicked.");
        try {
            Classifier classifier = Classifier.getInstance(this);
            classifier.updateClassifier(this.feedbackCommentList);
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.getClass().getSimpleName()+" onClickButtonGoBackToDashboard function something bad happened while updating the classifier: "+e.toString());
        }


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
        //Log.i(UtilityVariables.tag,this.getClass().getSimpleName()+": comments: "+sb.toString());
        textViewComments.setText(sb.toString());
        textViewComments.setTextColor(Color.WHITE);
        textViewComments.setPadding(0,2,0,1);
        tr_head.addView(textViewComments);

        TextView predictedText = new TextView(this);
        predictedText.setText(this.feedbackCommentList.get(i).classifierResult);
        predictedText.setPadding(2,2,2,1);
        tr_head.addView(predictedText);

        Button buttonCorrect = new Button(this);
        buttonCorrect.setText("Correct");
        buttonCorrect.setPadding(2,2,5,1);
        buttonCorrect.setOnClickListener(this);
        tr_head.addView(buttonCorrect);

        Button buttonWrong = new Button(this);
        buttonWrong.setText("Wrong");
        buttonWrong.setPadding(2,2,5,1);
        buttonWrong.setOnClickListener(this);
        tr_head.addView(buttonWrong);

        TextView predictedValue = new TextView(this);
        predictedValue.setText(this.feedbackCommentList.get(i).predictedValue+"");
        predictedValue.setVisibility(View.INVISIBLE);
        predictedValue.setPadding(0,2,0,1);
        tr_head.addView(predictedValue);

        TextView featureValuesText = new TextView(this);
        featureValuesText.setText(Arrays.toString(this.feedbackCommentList.get(i).featureValues));
        featureValuesText.setVisibility(View.INVISIBLE);
        featureValuesText.setPadding(0,2,0,1);
        tr_head.addView(featureValuesText);


        this.tablenotifications.addView(tr_head, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }

    public void onClick(View v)
    {
        Button buttonClicked = (Button)v;
        TableRow row = (TableRow) buttonClicked.getParent();
        String predictedText  = ((TextView) row.getChildAt(1)).getText().toString();
        int id = row.getId();
        //Log.i(UtilityVariables.tag,"The row of the clicked button is: "+id);
        if(buttonClicked.getText().toString().equals("Wrong"))
        {
            //Log.i(UtilityVariables.tag,"This classification was wrong.");
            if(predictedText.equals("Bullying"))
                this.feedbackCommentList.get(id).feedbackValue = 0;
            else
                this.feedbackCommentList.get(id).feedbackValue = 1;
        }
        else
        {
            //Log.i(UtilityVariables.tag,"This classification was correct.");
            if(predictedText.equals("Bullying"))
                this.feedbackCommentList.get(id).feedbackValue = 1;
            else
                this.feedbackCommentList.get(id).feedbackValue = 0;
        }

        Toast.makeText(this, ToastMessagesVariables.THANK_YOU_FEEDBACK, Toast.LENGTH_SHORT).show();
        this.tablenotifications.removeView(row);


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
            //Log.i(UtilityVariables.tag,"feedbacklist size got in notification page: "+this.feedbackCommentList.size());
            this.tablenotifications = (TableLayout) findViewById(R.id.TableNotifications);
            cleanTable(this.tablenotifications);
            for(int i=0;i<this.feedbackCommentList.size();i++)
            {
                addDataToRow(i);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int childCount = this.tablenotifications.getChildCount();
        if (childCount > 0 && keyCode == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(getApplicationContext(), "Please provide feedbacks and then click go back to dashboard button.", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
