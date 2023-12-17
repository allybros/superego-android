package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.widget.SegoEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.recaptcha.RecaptchaAction;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class LoginActivity extends BaseSignOnActivity {
    private MaterialButton btLogin;
    private SegoEditText etPassword;
    private SegoEditText etUid;
    private ConstraintLayout btSignInGoogle;
    private ConstraintLayout btSignInTwitter;
    private MaterialProgressBar progressView;
    private BroadcastReceiver loginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponents();
        setupUi();
    }

    /**
     * Binds layout items to the view objects of this class
     */
    private void initializeComponents(){
        btLogin = findViewById(R.id.btSignUp);
        btSignInGoogle = findViewById(R.id.btSignInGoogle);
        btSignInTwitter = findViewById(R.id.btSignInTwitter);
        etUid = findViewById(R.id.etLoginUid);
        etPassword = findViewById(R.id.etPassword);
        progressView = findViewById(R.id.progressViewLogin);
    }

    @Override
    protected void setUpReceivers(){
        super.setUpReceivers();
        // Receiver for Login Task
        loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("Login receiver", "Status: " + status);
                setProgress(false);

                switch (status){
                    case ErrorCodes.SUCCESS:
                        //Login User
                        Intent i =new Intent(getApplicationContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;

                    case ErrorCodes.SYSFAIL:
                    case ErrorCodes.CAPTCHA_REQUIRED:
                        etUid.setError(getString(R.string.error_login_failed));
                        etPassword.setError(getString(R.string.error_login_failed));
                        break;

                    case ErrorCodes.INVALID_CAPTCHA:
                        showCaptchaFailureDialog();
                        break;

                    case ErrorCodes.SUSPEND_SESSION:
                        showErrorDialog(getString(R.string.error_desc_session_suspended));
                        break;

                    default:
                        showErrorDialog(getString(R.string.error_check_connection));
                        break;
                }
            }
        };

        //Registers Receivers
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(loginReceiver, new IntentFilter(ConstantValues.ACTION_LOGIN));
    }

    /**
     * Define UI Logic
     */
    private void setupUi(){
        // Login button
        btLogin.setOnClickListener(v -> {
            etPassword.clearError();
            etUid.clearError();

            if (etUid.getText().isEmpty()) {
                etUid.setError(getString(R.string.error_username_empty));
            }
            if (etPassword.getText().isEmpty()) {
                etPassword.setError(getString(R.string.error_password_empty));
            }
            if (!etPassword.getText().isEmpty() && !etUid.getText().isEmpty()) {
                setProgress(true);
                this.executeLoginTask(etUid.getText(), etPassword.getText());
            }

        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //this method is empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //this method is empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    etPassword.setError(getString(R.string.error_password_empty));
                } else {
                    etPassword.clearError();
                }
            }
        });

        etUid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    etUid.setError(getString(R.string.error_username_empty));
                } else {
                    etUid.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is empty
            }
        });

        btSignInGoogle.setOnClickListener(v -> onGoogleSignInClicked());
        btSignInTwitter.setOnClickListener(v-> onTwitterSignInClicked());
    }

    /**
     * Executes the login operation within Recaptcha action
     * @param uid Username or Email
     * @param password Password
     */
    private void executeLoginTask(String uid, String password) {
        if (!this.isRecaptchaReady()) {
            showCaptchaFailureDialog();
            setProgress(false);
            return;
        }
        this.getRecaptchaTaskClient().executeTask(RecaptchaAction.LOGIN)
            .addOnSuccessListener(this, token -> LoginTask.loginTask(this, uid, password, token))
            .addOnFailureListener(this,
                    e -> {
                        Log.e("Recaptcha", "Token generation failure");
                        showCaptchaFailureDialog();
                    });
    }

    /**
     * Shows progress view upon login form and disables form items,
     * Indicates a process is going on
     * @param visible Set true when progress view needs to be shown.
     */
    @Override
    protected void setProgress(boolean visible) {
        if (visible) {
            etPassword.setEnabled(false);
            etUid.setEnabled(false);
            btLogin.setEnabled(false);
            progressView.setVisibility(View.VISIBLE);
        } else {
            etPassword.setEnabled(true);
            etUid.setEnabled(true);
            btLogin.setEnabled(true);
            progressView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        //Delete receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
        super.onDestroy();
    }

    public void onRegisterButtonClicked(View view) {
        Intent intent=new Intent(view.getContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
