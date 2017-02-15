package com.example.cybersafetyapp.ActivityPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;

public class Aboutus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        Bundle messages = getIntent().getExtras();
        String sourceClass = messages.getString(IntentSwitchVariables.SOURCE_CLASS_NAME);
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
        button.setText("Log In");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),LogIn.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
            }
        });
        relativeLayout.addView(button, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));

    }

    private void AboutUsAfterLogIn()
    {
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.activity_aboutus);
        Button button = new Button(this);
        button.setText("Dashboard");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),Dashboard.class);
                intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                startActivity(intent);
            }
        });
        relativeLayout.addView(button, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT));


    }
}
