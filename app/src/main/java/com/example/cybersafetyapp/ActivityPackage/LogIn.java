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

public class LogIn extends AppCompatActivity implements JsonResultReceiver.Receiver,View.OnClickListener{
    private String email,password;
    private ProgressDialog progressDialog;
    private String classname = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    private String checkValidLogin()
    {
        if (this.email.length()< UtilityVariables.VALID_EMAIL_LENGTH)
            return ErrorMessageVariables.NOT_VALID_EMAIL;
        if (this.password.length() < UtilityVariables.VALID_PASSWORD_LENGTH)
            return ErrorMessageVariables.NOT_VALID_PASSWORD;
        return UtilityVariables.VALID;
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
            String validMessage = this.checkValidLogin();
            if (validMessage.equals(UtilityVariables.VALID)) {
                this.progressDialog = new ProgressDialog(this);
                this.progressDialog.show();

                JsonResultReceiver mReceiver = new JsonResultReceiver(new Handler());
                mReceiver.setReceiver(this);
                Intent intentServiceServer = new Intent(this, IntentServiceServer.class);
                intentServiceServer.putExtra(IntentSwitchVariables.EMAIL,this.email);
                intentServiceServer.putExtra(IntentSwitchVariables.PASSWORD,this.password);
                intentServiceServer.putExtra(IntentSwitchVariables.RECEIVER, mReceiver);
                intentServiceServer.putExtra(IntentSwitchVariables.REQUEST, IntentSwitchVariables.REQUEST_SERVER_GUARDIAN_LOGIN);
                startService(intentServiceServer);

            } else {
                this.progressDialog.dismiss();
                Toast.makeText(this, validMessage, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,LogIn.class);
                startActivity(intent);


            }
        }catch (Exception e)
        {
            this.progressDialog.dismiss();
            Toast.makeText(LogIn.this, ErrorMessageVariables.LOGIN_UNEXPECTED_ERROR, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,LogIn.class);
            startActivity(intent);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(this,WelcomeToCybersafetyApp.class);
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
                if (success!=null && message != null)
                {
                    if(success.equals("success"))
                    {
                        Intent intent = new Intent(this,Dashboard.class);
                        intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
                        intent.putExtra(IntentSwitchVariables.SOURCE_CLASS_NAME,this.getClass().getName());
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this,LogIn.class);
                        startActivity(intent);
                    }
                }
                else
                {
                    Log.i(UtilityVariables.tag,this.classname+" success and message came back as null");
                }

                break;
            case IntentServiceGetJson.STATUS_ERROR:
                this.progressDialog.dismiss();
                Toast.makeText(LogIn.this, ErrorMessageVariables.LOGIN_UNEXPECTED_ERROR, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,LogIn.class);
                startActivity(intent);
                break;
        }

    }
}
