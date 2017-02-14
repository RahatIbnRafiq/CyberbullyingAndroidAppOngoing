package com.example.cybersafetyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity implements View.OnClickListener,JsonResultReceiver.Receiver {
    DatabaseHelper databaseHelper;
    String email,password,phone,email_confirmation,password_confirmation;
    private ProgressDialog progressdialog;
    private JsonResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseHelper = new DatabaseHelper(this);
    }

    public void onClick(View v)
    {
        buttonOnClickRegister(v);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(this, WelcomeToCybersafetyApp.class);
        startActivity(intent);
        return true;
    }

    private String checkValidRegistration(View v)
    {
        email = ((EditText)findViewById(R.id.edittext_register_emailid)).getText().toString();
        email_confirmation = ((EditText)findViewById(R.id.edittext_register_emailid)).getText().toString();
        if (!email.equals(email_confirmation))
            return ErrorMessageVariables.EmailsDoNotMatch;

        password = ((EditText)findViewById(R.id.edittext_register_password)).getText().toString();
        password_confirmation = ((EditText)findViewById(R.id.edittext_register_password_confirmation)).getText().toString();
        if (!password.equals(password_confirmation))
            return ErrorMessageVariables.PasswordsDoNotMatch;
        else if (password.length() < ErrorMessageVariables.validPasswordLength)
            return ErrorMessageVariables.PasswordLengthError;

        phone = ((EditText)findViewById(R.id.edittext_register_phone)).getText().toString();
        if (phone.length() < ErrorMessageVariables.validPhoneLength)
            return ErrorMessageVariables.notValidPhoneNumber;

        return ErrorMessageVariables.valid;
    }

    public void buttonOnClickRegister(View v)
    {
        String message = checkValidRegistration(v);
        if (message.equals(ErrorMessageVariables.valid))
        {
            this.progressdialog = new ProgressDialog(this);
            this.progressdialog.show();

            mReceiver = new JsonResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            Intent intentServiceServer = new Intent(this, IntentServiceServer.class);
            intentServiceServer.putExtra(IntentSwitchVariables.email,this.email);
            intentServiceServer.putExtra(IntentSwitchVariables.password,this.password);
            intentServiceServer.putExtra(IntentSwitchVariables.phone_number,this.phone);
            intentServiceServer.putExtra(IntentSwitchVariables.receiver, mReceiver);
            intentServiceServer.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_REGISTER);
            Log.i(UtilityVariables.tag,"Register class now switching to intentServiceServer service");
            startService(intentServiceServer);
            /*boolean insertion = databaseHelper.insertRegistrationData(email,password,phone);
            if (insertion) {
                Toast.makeText(Register.this,ToastMessagesVariables.RegistrationSuccess,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IntentSwitchVariables.LogIn);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(Register.this,message,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(IntentSwitchVariables.Register);
                startActivity(intent);
            }*/
        }
        else
        {
            Toast.makeText(Register.this,message,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(IntentSwitchVariables.Register);
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
                if(success.equals("success"))
                {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IntentSwitchVariables.LogIn);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IntentSwitchVariables.Register);
                    startActivity(intent);
                }
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                this.progressdialog.dismiss();
                Toast.makeText(this, "Sorry. Something Unexpected has happened. Please try again.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IntentSwitchVariables.Register);
                startActivity(intent);
                break;
        }

    }
}
