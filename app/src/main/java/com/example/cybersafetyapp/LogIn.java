package com.example.cybersafetyapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    private Intent intent;
    String email,password;
    DatabaseHelper databaseHelper;


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

    public void onClickLoginButton(View v)
    {
        try {
            this.email = ((EditText)findViewById(R.id.editext_login_emailid)).getText().toString();
            this.password = ((EditText)findViewById(R.id.edittext_login_password)).getText().toString();

            String validMessage = this.checkValidLogin();
            if (validMessage.equals(ErrorMessageVariables.valid)) {
                boolean queryResult = this.databaseHelper.checkLoginGuardian(this.email.toString(), this.password.toString());
                if (queryResult == true) {
                    intent = new Intent(IntentSwitchVariables.Dashboard);
                    intent.putExtra(IntentSwitchVariables.email,this.email.toString());
                    intent.putExtra(IntentSwitchVariables.sourceClassName,this.getClass().getName().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(LogIn.this, ErrorMessageVariables.logInCredentialFail, Toast.LENGTH_LONG).show();
                    intent = new Intent(IntentSwitchVariables.LogIn);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(LogIn.this, validMessage, Toast.LENGTH_LONG).show();
                intent = new Intent(IntentSwitchVariables.LogIn);

                startActivity(intent);

            }
        }catch (Exception e)
        {
            Toast.makeText(LogIn.this, ErrorMessageVariables.logInUnexpectedError, Toast.LENGTH_LONG).show();
            intent = new Intent(IntentSwitchVariables.LogIn);
            startActivity(intent);

        }

    }
}
