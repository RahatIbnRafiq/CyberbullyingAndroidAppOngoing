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
        databaseHelper = new DatabaseHelper(this);
    }

    private String checkValidRegistration(View v)
    {
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
        /*boolean insertion = this.databaseHelper.insertRegistrationData("test1@test.com","123456","1231231234");
        System.out.println("insertion result:");
        System.out.println(insertion);
        System.out.println("now trying to print the data from the table");

        Cursor res = this.databaseHelper.getAllData();
        if(res.getCount() == 0)
        {
            System.out.println("Nothing found in the table");
        }
        else
        {
            while (res.moveToNext())
            {
                System.out.println("email :"+ res.getString(0)+"\n");
                System.out.println("password :"+ res.getString(1)+"\n");
                System.out.println("phone :"+ res.getString(2)+"\n");
            }

            System.out.println("printing data is done");
        }*/



        String message = checkValidRegistration(v);
        if (message.equals("valid"))
        {
            boolean insertion = databaseHelper.insertRegistrationData(email,password,phone);
            if (insertion == true) {
                Toast.makeText(Register.this,registrationSuccess,Toast.LENGTH_SHORT).show();
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
        }



    }
}
