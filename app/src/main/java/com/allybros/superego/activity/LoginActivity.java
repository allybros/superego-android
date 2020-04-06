package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.ErrorCodes;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton btLogin;
    private MaterialButton btRegister;
    private TextInputEditText etMail;
    private TextInputEditText etPassword;
    public TextInputLayout passwordTextInput, usernameTextInput;
    private MaterialCardView loginCard;

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
        loginCard= (MaterialCardView) findViewById(R.id.loginCard);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("login-status-share"));

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.FadeOutUp)
                        .duration(700)
                        .repeat(0)
                        .playOn(findViewById(R.id.loginCard));
                btLogin.setEnabled(true);

                passwordTextInput.setErrorEnabled(false);
                usernameTextInput.setErrorEnabled(false);

                if(etMail.getText().toString().isEmpty()){
                    usernameTextInput.setError(getString(R.string.usernameEmpty));
                    YoYo.with(Techniques.FadeInDown)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.loginCard));
                    btLogin.setEnabled(true);
                }
                if(etPassword.getText().toString().isEmpty()){
                    passwordTextInput.setError(getString(R.string.passwordEmpty));
                    YoYo.with(Techniques.FadeInDown)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.loginCard));
                    btLogin.setEnabled(true);
                }
                if(!etPassword.getText().toString().isEmpty() && !etMail.getText().toString().isEmpty()){
                    YoYo.with(Techniques.FadeOutUp)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.loginCard));
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",0);
            Log.d("receiver", "Got message: " + status);
            switch (status){

                case ErrorCodes.SYSFAIL:
                    passwordTextInput.setError(getString(R.string.loginFailed));
                    usernameTextInput.setError(getString(R.string.loginFailed));
                    YoYo.with(Techniques.FadeInDown)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.loginCard));
                    btLogin.setEnabled(true);
                    break;

                case ErrorCodes.CAPTCHA_REQUIRED:
                    passwordTextInput.setError(getString(R.string.loginFailed));
                    usernameTextInput.setError(getString(R.string.loginFailed));
                    YoYo.with(Techniques.FadeInDown)
                            .duration(700)
                            .repeat(0)
                            .playOn(findViewById(R.id.loginCard));
                    btLogin.setEnabled(true);
                    break;

                case ErrorCodes.SUSPEND_SESSION:
                    passwordTextInput.setError(getString(R.string.session_suspend));
                    usernameTextInput.setError(getString(R.string.session_suspend));
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("insightof.me");
                    builder.setMessage(getString(R.string.session_suspend));
                    builder.setPositiveButton( getString(R.string.okey), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            btLogin.setEnabled(true);
                            YoYo.with(Techniques.FadeInDown)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(findViewById(R.id.loginCard));
                        }
                    });
                    builder.show();
                    break;

                case ErrorCodes.CONNECTION_ERROR:
                    passwordTextInput.setError(getString(R.string.checkConnection));
                    usernameTextInput.setError(getString(R.string.checkConnection));
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setTitle("insightof.me");
                    builder1.setMessage(getString(R.string.checkConnection));
                    builder1.setPositiveButton( getString(R.string.okey), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            btLogin.setEnabled(true);
                            YoYo.with(Techniques.FadeInDown)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(findViewById(R.id.loginCard));

                        }
                    });
                    builder1.show();
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
