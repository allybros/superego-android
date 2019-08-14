package com.allybros.superego.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.LoginTask;


public class MainActivity extends AppCompatActivity {
    private Button btLogin;
    private Button btRegister;
    private EditText etMail;
    private EditText etPassword;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    private String session_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref= getSharedPreferences(USER_INFORMATION_PREF,MODE_PRIVATE);
        session_token=pref.getString("session_token","");
        if(!session_token.isEmpty()){
            LoadProfileTask.loadProfileTask(getBaseContext(),session_token);
        }

        btLogin=(Button) findViewById(R.id.btLogin);
        btRegister=(Button) findViewById(R.id.btRegister);
        etMail=(EditText )findViewById(R.id.etMail);
        etPassword=(EditText )findViewById(R.id.etPassword);


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    LoginTask.loginTask(getApplicationContext(),etMail.getText().toString(),etPassword.getText().toString());

            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

