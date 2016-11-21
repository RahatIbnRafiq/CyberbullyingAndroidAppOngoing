package com.example.cybersafetyapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    String email,password,phone,email_confirmation,password_confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseHelper = new DatabaseHelper(this);
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
            boolean insertion = databaseHelper.insertRegistrationData(email,password,phone);
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
            }
        }
        else
        {
            Toast.makeText(Register.this,message,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(IntentSwitchVariables.Register);
            startActivity(intent);
        }



    }
}
