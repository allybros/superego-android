package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.ErrorCodes;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton btLogin;
    private MaterialButton btRegister;
    private TextInputEditText etMail;
    private TextInputEditText etPassword;
    public TextInputLayout passwordTextInput, usernameTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameTextInput =(TextInputLayout) findViewById(R.id.username_text_input);
        btLogin=(MaterialButton) findViewById(R.id.btLogin);
        btRegister=(MaterialButton) findViewById(R.id.btRegister);
        etMail=(TextInputEditText)findViewById(R.id.etMail);
        etPassword=(TextInputEditText)findViewById(R.id.etPassword);
        passwordTextInput=(TextInputLayout)findViewById(R.id.password_text_input);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("login-status-share"));

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTextInput.setErrorEnabled(false);
                usernameTextInput.setErrorEnabled(false);

                if(etMail.getText().toString().isEmpty()){
                    usernameTextInput.setError(getString(R.string.usernameEmpty));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.password_text_input));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.username_text_input));

                }
                if(etPassword.getText().toString().isEmpty()){
                    passwordTextInput.setError(getString(R.string.passwordEmpty));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.password_text_input));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.username_text_input));
                }
                if(!etPassword.getText().toString().isEmpty() && !etMail.getText().toString().isEmpty()){
                    LoginTask.loginTask(getApplicationContext(),etMail.getText().toString(),etPassword.getText().toString());
                }




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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);
//TODO:PAROLA HATALI durumu eksik
            switch (status){
                case ErrorCodes.SYSFAIL:
                    passwordTextInput.setError(getString(R.string.loginFailed));
                    usernameTextInput.setError(getString(R.string.loginFailed));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.password_text_input));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.username_text_input));
                    break;
                case ErrorCodes.SUSPEND_SESSION:
                    passwordTextInput.setError(getString(R.string.session_suspend));
                    usernameTextInput.setError(getString(R.string.session_suspend));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(5)
                            .playOn(findViewById(R.id.password_text_input));
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(5)
                            .playOn(findViewById(R.id.username_text_input));
                    break;


            }

        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

}
