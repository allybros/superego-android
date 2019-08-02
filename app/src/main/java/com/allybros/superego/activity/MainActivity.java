package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;


public class MainActivity extends AppCompatActivity {
    private Button btLogin;
    private Button btRegister;
    private EditText etMail;
    private EditText etPassword;

    public static final int LOGIN_FAILED=0;
    public static final int LOGIN_SUCCESS=10;
    public static final int USERNAME_EMPTY=20;
    public static final int PASSWORD_EMPTY=26;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btLogin=(Button) findViewById(R.id.btLogin);
        btRegister=(Button) findViewById(R.id.btRegister);
        etMail=(EditText )findViewById(R.id.etMail);
        etPassword=(EditText )findViewById(R.id.etPassword);


        final Toast[] toast = new Toast[1];
        final String loginFailed= (String) getString(R.string.loginFailed);
        final String loginSuccess= (String) getString(R.string.loginSuccess);
        final String usernameEmpty= (String) getString(R.string.usernameEmpty);
        final String passwordEmpty= (String) getString(R.string.passwordEmpty);

        BroadcastReceiver statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status=intent.getIntExtra("status",0);
                switch (status){
                    case LOGIN_FAILED:
                        toast[0] = Toast.makeText(context, loginFailed, Toast.LENGTH_SHORT);
                        toast[0].show();
                        break;
                    case LOGIN_SUCCESS:
                        toast[0] = Toast.makeText(context, loginSuccess, Toast.LENGTH_SHORT);
                        toast[0].show();
                        Intent intent1=new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case USERNAME_EMPTY:
                        toast[0] = Toast.makeText(context, usernameEmpty, Toast.LENGTH_SHORT);
                        toast[0].show();
                        break;
                    case PASSWORD_EMPTY:
                        toast[0] = Toast.makeText(context, passwordEmpty, Toast.LENGTH_SHORT);
                        toast[0].show();
                        break;
                }

            }
        };




        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, new IntentFilter("status"));

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginTask(MainActivity.this,etMail.getText().toString(),etPassword.getText().toString()).execute();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}

