package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class AddNewProfile extends AppCompatActivity {
    private String email;
    private String classname = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_profile);
        Bundle messages = getIntent().getExtras();
        if(messages == null ||
                messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME) == null ||
                !messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME).equals(Dashboard.class.getName()))
        {
            Intent intent = new Intent(this,Dashboard.class);

            try{
                this.email = messages.getString(IntentSwitchVariables.EMAIL);
                intent.putExtra(IntentSwitchVariables.EMAIL,email);

            }catch(Exception e)
            {
                Log.i(UtilityVariables.tag,this.classname+": add new profile did not get email.");
            }

            intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
            startActivity(intent);
        }
        this.email = messages.getString(IntentSwitchVariables.EMAIL);
    }

    public void buttonOnClickSearchByUserName(View v)
    {
        Intent intent = new Intent(this,SearchByUserName.class);
        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
        intent.putExtra(IntentSwitchVariables.EMAIL,email);
        startActivity(intent);
    }
}
