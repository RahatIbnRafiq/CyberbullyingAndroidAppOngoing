package com.example.cybersafetyapp.ActivityPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cybersafetyapp.IntentServicePackage.IntentServiceGetJson;
import com.example.cybersafetyapp.IntentServicePackage.IntentServiceServer;
import com.example.cybersafetyapp.HelperClassesPackage.JsonResultReceiver;
import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.ErrorMessageVariables;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class Register extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver {
    private String email;
    private String password;
    private String phone;
    private ProgressDialog progressdialog;
    private String classname = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onClick(View v)
    {
        buttonOnClickRegister(v);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(this,WelcomeToCybersafetyApp.class);
        startActivity(intent);
        return true;
    }

    private String checkValidRegistration()
    {
        this.email = ((EditText)findViewById(R.id.edittext_register_emailid)).getText().toString();
        String email_confirmation = ((EditText) findViewById(R.id.edittext_register_emailid)).getText().toString();
        if (!email.equals(email_confirmation))
            return ErrorMessageVariables.EMAILS_DO_NOT_MATCH;

        this.password = ((EditText)findViewById(R.id.edittext_register_password)).getText().toString();
        String password_confirmation = ((EditText) findViewById(R.id.edittext_register_password_confirmation)).getText().toString();
        if (!this.password.equals(password_confirmation))
            return ErrorMessageVariables.PASSWORDS_DO_NOT_MATCH;
        else if (this.password.length() < UtilityVariables.VALID_PASSWORD_LENGTH)
            return ErrorMessageVariables.PASSWORD_LENGTH_ERROR;

        this.phone = ((EditText)findViewById(R.id.edittext_register_phone)).getText().toString();
        if (this.phone.length() < UtilityVariables.VALID_PHONE_LENGTH)
            return ErrorMessageVariables.NOT_VALID_PHONE_NUMBER;

        return UtilityVariables.VALID;
    }

    public void buttonOnClickRegister(View v)
    {
        String message = checkValidRegistration();
        if (message.equals(UtilityVariables.VALID))
        {
            this.progressdialog = new ProgressDialog(this);
            this.progressdialog.show();

            JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent intentServiceServer = new Intent(this, IntentServiceServer.class);
            intentServiceServer.putExtra(IntentSwitchVariables.EMAIL,this.email);
            intentServiceServer.putExtra(IntentSwitchVariables.PASSWORD,this.password);
            intentServiceServer.putExtra(IntentSwitchVariables.PHONE_NUMBER,this.phone);
            intentServiceServer.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
            intentServiceServer.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_REGISTER);
            Log.i(UtilityVariables.tag,this.classname+": Register class now switching to intentServiceServer service");
            startService(intentServiceServer);
        }
        else
        {
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,Register.class);
            startActivity(intent);
        }



    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                this.progressdialog.dismiss();
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                this.progressdialog.dismiss();
                String success = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS);
                String message = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE);
                if(success!=null && message != null)
                {
                    if(success.equals("success"))
                    {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,LogIn.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this,Register.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    Log.i(UtilityVariables.tag,this.classname+" null pointer found while getting success and messsage from server.");
                }

                break;
            case IntentServiceGetJson.STATUS_ERROR:
                this.progressdialog.dismiss();
                Toast.makeText(this, "Sorry. Something Unexpected has happened. Please try again.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,Register.class);
                startActivity(intent);
                break;
        }

    }
}
