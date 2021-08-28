package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.api.RegisterTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.HelperMethods;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etRegisterUsername,etRegisterMail,etRegisterPassword;
    private TextInputLayout inputLayoutUsername, inputLayoutEmail, inputLayoutPassword;
    private Button btnRegister;
    private CheckBox checkBoxAgreement;
    private LinearLayout cardFormRegister;
    private MaterialProgressBar progressView;
    private BroadcastReceiver registerReceiver;
    private BroadcastReceiver autoLoginReceiver;
    private TextView tvAgreementRegister;

    private String usernameInput;
    private String emailInput;
    private String passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeComponents();
        setupReceivers();
        setupUi();
    }

    private void initializeComponents(){
        btnRegister = findViewById(R.id.btSignUp);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterMail = findViewById(R.id.etRegisterMail);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        inputLayoutUsername = findViewById(R.id.inputLayoutRegisterUsername);
        inputLayoutEmail = findViewById(R.id.inputLayoutRegisterEmail);
        inputLayoutPassword = findViewById(R.id.inputLayoutRegisterPassword);
        checkBoxAgreement = findViewById(R.id.checkboxAgreement);
        cardFormRegister = findViewById(R.id.cardFormRegister);
        progressView = findViewById(R.id.progressViewRegister);
        tvAgreementRegister = findViewById(R.id.tvAgreementRegister);

    }

    private void setupReceivers(){
        /* Catches broadcasts of api/RegisterTask class */
        registerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("Register receiver", "Got message: " + status);
                setProgress(false);
                switch (status) {
                    case ErrorCodes.SUCCESS:
                        //Auto Login User
                        LoginTask.loginTask(getApplicationContext(), usernameInput, passwordInput);
                        setProgress(true);
                        break;
                    case ErrorCodes.SYSFAIL:
                        new AlertDialog.Builder(getApplicationContext(), R.style.SegoAlertDialog)
                                .setTitle(R.string.alert_sign_up)
                                .setMessage(R.string.error_no_connection)
                                .setPositiveButton(R.string.action_ok, null)
                                .show();
                        break;

                    case ErrorCodes.USERNAME_NOT_LEGAL:
                        inputLayoutUsername.setError(getString(R.string.error_username_not_legal));
                        break;

                    case ErrorCodes.USERNAME_ALREADY_EXIST:
                        inputLayoutUsername.setError(getString(R.string.error_username_taken));
                        break;

                    case ErrorCodes.EMAIL_ALREADY_EXIST:
                        inputLayoutEmail.setError(getString(R.string.error_email_already_exist));
                        break;

                    case ErrorCodes.EMAIL_NOT_LEGAL:
                        inputLayoutEmail.setError(getString(R.string.error_email_not_legal));
                        break;

                    case ErrorCodes.PASSWORD_NOT_LEGAL:
                        inputLayoutPassword.setError(getString(R.string.error_password_not_legal));
                        break;
                }
            }
        };

        /* Automatically called login task receiver which is called when register operation succeed. */
        autoLoginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status", ErrorCodes.SYSFAIL);
                //User logged in successfully
                if (status == ErrorCodes.SUCCESS) {
                    Log.d("Register Activity", "Automated login succeeded");
                    //Redirect to splash
                    Intent i = new Intent(RegisterActivity.this, SplashActivity.class);
                    startActivity(i);
                    finish();
                    // Finish the parent for performance
                    if (getParent() != null)
                        getParent().finish();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(registerReceiver, new IntentFilter(ConstantValues.ACTION_REGISTER));
        LocalBroadcastManager.getInstance(this).registerReceiver(autoLoginReceiver, new IntentFilter(ConstantValues.ACTION_LOGIN));
    }

    private void setupUi(){

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputLayoutUsername.setErrorEnabled(false);
                inputLayoutEmail.setErrorEnabled(false);
                inputLayoutPassword.setErrorEnabled(false);
                checkBoxAgreement.setError(null);
                HelperMethods.setMargins(tvAgreementRegister,0,0,0,0);


                usernameInput = etRegisterUsername.getText().toString();
                emailInput = etRegisterMail.getText().toString();
                passwordInput = etRegisterPassword.getText().toString();
                boolean conditions = checkBoxAgreement.isChecked();


                //Validate fields
                if (usernameInput.isEmpty()) inputLayoutUsername.setError(getResources().getString(R.string.error_username_empty));

                if (emailInput.isEmpty()) inputLayoutEmail.setError(getResources().getString(R.string.error_email_empty));

                if (passwordInput.isEmpty()) inputLayoutPassword.setError(getResources().getString(R.string.error_password_empty));

                if (!conditions){
                    checkBoxAgreement.setError(getResources().getString(R.string.error_conditions_not_accepted));
                    HelperMethods.setMargins(tvAgreementRegister,20,0,0,0);
                }

                if (!usernameInput.isEmpty()
                        && !emailInput.isEmpty()
                        && !passwordInput.isEmpty()
                        && conditions) {
                    Log.d("Register request send","Register request send");
                    setProgress(true);
                    RegisterTask.registerTask(getApplicationContext(), etRegisterUsername.getText().toString(), etRegisterMail.getText().toString(), etRegisterPassword.getText().toString(), true);
                }

            }
        });

        tvAgreementRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.conditions_link);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    /**
     * Shows progress view upon login form and disables form items,
     * Indicates a process is going on
     * @param visible Set true when progress view needs to be shown.
     */
    private void setProgress(boolean visible){
        if (visible) {
            // Disable form elements
            inputLayoutUsername.setEnabled(false);
            inputLayoutEmail.setEnabled(false);
            inputLayoutPassword.setEnabled(false);
            checkBoxAgreement.setEnabled(false);
            btnRegister.setEnabled(false);

            cardFormRegister.setAlpha(0.8f);
            progressView.setVisibility(View.VISIBLE);

        } else {
            //Enable form elements
            inputLayoutUsername.setEnabled(true);
            inputLayoutEmail.setEnabled(true);
            inputLayoutPassword.setEnabled(true);
            checkBoxAgreement.setEnabled(true);
            btnRegister.setEnabled(true);

            cardFormRegister.setAlpha(1f);
            progressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}