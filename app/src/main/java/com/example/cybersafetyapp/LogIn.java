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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity implements JsonResultReceiver.Receiver,View.OnClickListener{
    private Intent intent;
    String email,password;
    DatabaseHelper databaseHelper;
    private JsonResultReceiver mReceiver;
    private String validMessage;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        this.databaseHelper = new DatabaseHelper(this);
    }

    private String checkValidLogin()
    {
        if (this.email.length()<ErrorMessageVariables.validEmailLength)
            return ErrorMessageVariables.notValidEmail;
        if (this.password.length() < ErrorMessageVariables.validPasswordLength)
            return ErrorMessageVariables.notValidPassword;
        return ErrorMessageVariables.valid;
    }

    public void onClick(View v)
    {
        onClickLoginButton(v);
    }



    public void onClickLoginButton(View v)
    {
        try {
            this.email = ((EditText)findViewById(R.id.editext_login_emailid)).getText().toString();
            this.password = ((EditText)findViewById(R.id.edittext_login_password)).getText().toString();

            this.validMessage = this.checkValidLogin();
            if (validMessage.equals(ErrorMessageVariables.valid)) {
                this.progressDialog = new ProgressDialog(this);
                this.progressDialog.show();

                mReceiver = new JsonResultReceiver(new Handler());
                mReceiver.setReceiver(this);
                Intent intentServiceServer = new Intent(this, IntentServiceServer.class);
                intentServiceServer.putExtra(IntentSwitchVariables.email,this.email);
                intentServiceServer.putExtra(IntentSwitchVariables.password,this.password);
                intentServiceServer.putExtra(IntentSwitchVariables.receiver, mReceiver);
                intentServiceServer.putExtra(IntentSwitchVariables.request, IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_LOGIN);
                //Log.i(UtilityVariables.tag,"LogIn class now switching to intentServiceServer service");
                startService(intentServiceServer);
                /*boolean queryResult = this.databaseHelper.checkLoginGuardian(this.email.toString(), this.password.toString());
                if (queryResult == true) {
                    //testServerWorks();
                    intent = new Intent(IntentSwitchVariables.Dashboard);
                    intent.putExtra(IntentSwitchVariables.email,this.email.toString());
                    intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(LogIn.this, ErrorMessageVariables.logInCredentialFail, Toast.LENGTH_LONG).show();
                    intent = new Intent(IntentSwitchVariables.LogIn);
                    startActivity(intent);
                }*/

            } else {
                this.progressDialog.dismiss();
                Toast.makeText(LogIn.this, validMessage, Toast.LENGTH_LONG).show();
                intent = new Intent(IntentSwitchVariables.LogIn);
                startActivity(intent);


            }
        }catch (Exception e)
        {
            this.progressDialog.dismiss();
            Toast.makeText(LogIn.this, ErrorMessageVariables.logInUnexpectedError, Toast.LENGTH_LONG).show();
            intent = new Intent(IntentSwitchVariables.LogIn);
            startActivity(intent);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(this, WelcomeToCybersafetyApp.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode) {
            case IntentServiceGetJson.STATUS_RUNNING:
                this.progressDialog.dismiss();
                break;
            case IntentServiceGetJson.STATUS_FINISHED:
                this.progressDialog.dismiss();
                String success = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_SUCCESS);
                String message = resultData.getString(IntentSwitchVariables.SERVER_RESPONSE_MESSAGE);
                if(success.equals("success"))
                {
                    intent = new Intent(IntentSwitchVariables.Dashboard);
                    intent.putExtra(IntentSwitchVariables.email,this.email.toString());
                    intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName().toString());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(LogIn.this, message, Toast.LENGTH_LONG).show();
                    intent = new Intent(IntentSwitchVariables.LogIn);
                    startActivity(intent);
                }
                break;
            case IntentServiceGetJson.STATUS_ERROR:
                this.progressDialog.dismiss();
                Toast.makeText(LogIn.this, ErrorMessageVariables.logInUnexpectedError, Toast.LENGTH_LONG).show();
                intent = new Intent(IntentSwitchVariables.LogIn);
                startActivity(intent);
                break;
        }

    }
}
