package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.cybersafetyapp.HelperClassesPackage.DatabaseHelper;
import com.example.cybersafetyapp.HelperClassesPackage.DatabaseWorks;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;


public class SearchByUserName extends AppCompatActivity{
    private String email;
    private String classname = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_user_name);
        Bundle messages = getIntent().getExtras();
        if(messages == null ||
                messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME) == null ||
                (!messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME).equals(AddNewProfile.class.getName())) ||
                (!messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME).equals(SearchResultProfiles.class.getName()))
                )
        {
            Intent intent = new Intent(this,Dashboard.class);

            try{
                assert messages != null;
                email = messages.getString(IntentSwitchVariables.EMAIL);
                intent.putExtra(IntentSwitchVariables.EMAIL,email);

            }catch(Exception e)
            {
                Log.i(UtilityVariables.tag,this.classname+" oncreate function search by user name did not get email.");
            }

            intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
            startActivity(intent);
        }
        email = messages.getString(IntentSwitchVariables.EMAIL);
    }


    public void buttonOnclickSearchByUserName(View v)
    {
        RadioButton radioInstagram = (RadioButton)findViewById(R.id.radio_searchbyusername_instagram);
        String userToSearch = ((EditText)findViewById(R.id.edittext_searchbyusername_username)).getText().toString().trim();

        if (radioInstagram.isChecked())
        {
            try {
                DatabaseWorks databaseWorks = DatabaseWorks.getInstance(this);
                String token = databaseWorks.getAccessTokenForGuardian(this.email,DatabaseWorks.NAME_COL_INSTAGRAM_TOKEN);
                databaseWorks.printAllDataFromTable(DatabaseWorks.NAME_TABLE_GUARDIAN_INFORMATION);
                Intent intent;
                if(token == null || token.length() < 1)
                {
                    intent = new Intent(this,InstagramLogin.class);
                }
                else
                {
                    intent = new Intent(this,WaitForResults.class);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_ACCESS_TOKEN,token);
                }
                intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,userToSearch);
                intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intent.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH);
                startActivity(intent);

            }catch (Exception ex)
            {
                Log.i(UtilityVariables.tag,ex.toString());
            }

        }
    }

}
