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

import org.json.JSONException;


public class MainActivity extends AppCompatActivity {
    private Button btLogin;
    private Button btRegister;
    private EditText etMail;
    private EditText etPassword;
    private Button gecisbt;

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
        gecisbt=(Button)findViewById(R.id.gecisbt);


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
                        Intent intent1=new Intent(MainActivity.this, UserPageActivity.class);
                        intent1.putExtra("user_id",intent.getIntExtra("user_id",0));
                        intent1.putExtra("user_type", intent.getIntExtra("user_type",0));
                        intent1.putExtra("username", intent.getStringExtra("username"));
                        intent1.putExtra("user_bio",intent.getStringExtra("user_bio"));
                        intent1.putExtra("email",intent.getStringExtra("email"));
                        intent1.putExtra("userTraitsArray",intent.getBundleExtra("userTraitsArray"));
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

//        gecisbt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this, UserPageActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });


        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, new IntentFilter("loginTaskResult"));

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginTask tmpLoginTask =new LoginTask(etMail.getText().toString(),etPassword.getText().toString(),MainActivity.this);

                try {
                    tmpLoginTask.loginRequest();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.communicationError),Toast.LENGTH_SHORT).show();
                }
//                LOGİNTASKCAGIRL
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

