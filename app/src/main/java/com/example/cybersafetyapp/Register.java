package com.example.cybersafetyapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private Intent intent;
    DatabaseHelper databaseHelper;
    String email,password,phone,email_confirmation,password_confirmation,phone_conformation;
    String registrationSuccess = "Congrats.Registration is successful!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    private String checkValidRegistration(View v)
    {
        System.out.println("validating registration fields");
        email = ((EditText)findViewById(R.id.edittext_register_emailid)).getText().toString();
        email_confirmation = ((EditText)findViewById(R.id.edittext_register_emailid)).getText().toString();
        if ((email.equals(email_confirmation) == false))
            return "emails do not match";

        password = ((EditText)findViewById(R.id.edittext_register_password)).getText().toString();
        password_confirmation = ((EditText)findViewById(R.id.edittext_register_password_confirmation)).getText().toString();
        if (password.equals(password_confirmation) == false)
            return "passwords do not match";
        else if (password.length() < 3)
            return "password length must be at least 3";

        phone = ((EditText)findViewById(R.id.edittext_register_phone)).getText().toString();
        if (phone.length() < 10)
            return "not a valid phone number";

        return "valid";
    }

    public void buttonOnClickRegister(View v)
    {
        System.out.println("database being created");
        this.databaseHelper = new DatabaseHelper(this);
        System.out.println("database finished being created");
        /*String message = checkValidRegistration(v);
        if (message.equals("valid"))
        {
            System.out.println("valid data");
            boolean insertion = databaseHelper.insertRegistrationData(email,password,phone);
            if (insertion == true) {
                Toast.makeText(Register.this,registrationSuccess,Toast.LENGTH_SHORT).show();

                Cursor res = databaseHelper.showAllData();
                if (res.getCount() == 0)
                {
                    System.out.println("no data in the table");
                }
                else
                {
                    while (res.moveToNext())
                    {
                        System.out.println(res.getString(0)+" "+res.getString(1)+" "+res.getString(2)+"\n");
                    }


                }
                intent = new Intent("com.example.cybersafetyapp.LogIn");
                startActivity(intent);
            }
            else
            {
                Toast.makeText(Register.this,message,Toast.LENGTH_LONG).show();
                intent = new Intent("com.example.cybersafetyapp.Register");
                startActivity(intent);
            }
        }
        else
        {
            Toast.makeText(Register.this,message,Toast.LENGTH_LONG).show();
            intent = new Intent("com.example.cybersafetyapp.Register");
            startActivity(intent);
        }*/



    }
}
