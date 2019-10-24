package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.RegisterTask;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegisterUsername,etRegisterMail,etRegisterPassword;
    Button btSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        btSignUp=(Button) findViewById(R.id.btSignUp);
        etRegisterPassword=(EditText)findViewById(R.id.etRegisterPassword);
        etRegisterMail=(EditText)findViewById(R.id.etRegisterMail);
        etRegisterUsername=(EditText)findViewById(R.id.etRegisterUsername);


        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO:Aggrement value must define by a view component
            public void onClick(View v) {
                RegisterTask.registerTask(getApplicationContext(),etRegisterUsername.getText().toString(),etRegisterMail.getText().toString(),etRegisterPassword.getText().toString(),true);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
}