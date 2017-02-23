package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cybersafetyapp.ClassifierPackage.Classifier;
import com.example.cybersafetyapp.HelperClassesPackage.CommentFeedback;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ToastMessagesVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

import java.util.ArrayList;
import java.util.Arrays;

public class Notifications extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<CommentFeedback> feedbackCommentList;
    private String email;
    private TableLayout tablenotifications;
    private String classname = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupToolbarMenu();
        setupNavigationDrawerMenu();

        Intent intent = getIntent();
        this.feedbackCommentList = null;
        this.feedbackCommentList = (ArrayList<CommentFeedback>) intent.getSerializableExtra(IntentSwitchVariables.FEEDBACK_COMMENT_LIST);
        this.email = intent.getStringExtra(IntentSwitchVariables.EMAIL);


        showNotifications();


    }

    private void setupNavigationDrawerMenu()
    {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        if(UtilityVariables.isLoggedIn) {
            navigationView.inflateMenu(R.menu.menu_loggedin);
            //TextView emailTextView = (TextView)findViewById(R.id.txvEmail);
            //emailTextView.setText(this.email);
        }
        else
        {
            navigationView.inflateMenu(R.menu.menu_loggedout);
        }

        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_notifications);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupToolbarMenu()
    {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("Menu");
    }

    public void onClickButtonGoBackToDashboard(View v)
    {
        Log.i(UtilityVariables.tag,this.classname+": Go back to dashboard button has been clicked.");
        try {
            Classifier classifier = Classifier.getInstance(this);
            classifier.updateClassifier(this.feedbackCommentList);
        }catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.classname+" onClickButtonGoBackToDashboard function something bad happened while updating the classifier: "+e.toString());
        }


        Intent intent = new Intent(this,Dashboard.class);
        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().toString());
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
            sb.append(comments.get(j)).append("\n");
        }
        textViewComments.setText(sb.toString());
        textViewComments.setTextColor(Color.WHITE);
        textViewComments.setPadding(0,2,0,1);
        tr_head.addView(textViewComments);

        TextView predictedText = new TextView(this);
        predictedText.setText(this.feedbackCommentList.get(i).classifierResult);
        predictedText.setPadding(2,2,2,1);
        tr_head.addView(predictedText);

        Button buttonCorrect = new Button(this);
        buttonCorrect.setText(R.string.Correct);
        buttonCorrect.setPadding(2,2,5,1);
        buttonCorrect.setOnClickListener(this);
        tr_head.addView(buttonCorrect);

        Button buttonWrong = new Button(this);
        buttonWrong.setText(R.string.Wrong);
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
        if(buttonClicked.getText().toString().equals("Wrong"))
        {
            if(predictedText.equals("Bullying"))
                this.feedbackCommentList.get(id).feedbackValue = 0;
            else
                this.feedbackCommentList.get(id).feedbackValue = 1;
        }
        else
        {
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
        if (childCount > 0) {
            table.removeViews(1, childCount - 1);
        }
    }


    private void showNotifications()
    {
        if (this.feedbackCommentList != null)
        {
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else {
            int childCount = this.tablenotifications.getChildCount();
            if (childCount > 0 && keyCode == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(getApplicationContext(), "Please provide feedbacks and then use the menus to navigate.", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    /*@Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else {
            Toast.makeText(this," Use the navigate menu to navigate please ", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        Intent intent;

        switch (menuItem.getItemId())
        {
            case R.id.MenuAboutUs:
                intent = new Intent(this, Aboutus.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuLogIn:
                intent = new Intent(this, LogIn.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuRegister:
                intent = new Intent(this, Register.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;

            case R.id.MenuLogout:
                UtilityVariables.isLoggedIn=false;
                intent = new Intent(this, WelcomeToCybersafetyApp.class);
                startActivity(intent);
                break;
            case R.id.MenuAddUser:
                break;
            case R.id.MenuDashboard:
                intent = new Intent(this, Dashboard.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
                break;
            case R.id.MenuEditProfile:
                break;
            case R.id.MenuSettings:
                break;
            case R.id.MenuCheckMonitoringProfiles:
                intent = new Intent(this, MonitoringProfileList.class);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                startActivity(intent);
                break;
            case R.id.MenuTurnAlarmOn:
                break;

        }
        return false;
    }
}
