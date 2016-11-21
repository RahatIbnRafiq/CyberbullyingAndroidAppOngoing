package com.example.cybersafetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Aboutus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        Bundle messages = getIntent().getExtras();
        String sourceClass = messages.getString(IntentSwitchVariables.sourceClassName);
        if(sourceClass != null && sourceClass.equals(WelcomeToCybersafetyApp.class.getName()))
        {
            AboutUsBeforeLogIn();

        }
        else
        {
            AboutUsAfterLogIn();
        }
    }


    private void AboutUsBeforeLogIn()
    {
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_aboutus);
        Button button = new Button(this);
        button.setText(UtilityVariables.LogIn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentSwitchVariables.LogIn);
                intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
                startActivity(intent);
            }
        });
        relativeLayout.addView(button, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));

    }

    private void AboutUsAfterLogIn()
    {
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_aboutus);
        Button button = new Button(this);
        button.setText(UtilityVariables.Dashboard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntentSwitchVariables.Dashboard);
                intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName());
                startActivity(intent);
            }
        });
        relativeLayout.addView(button, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));


    }
}
