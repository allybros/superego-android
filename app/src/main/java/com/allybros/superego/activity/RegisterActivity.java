package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.RegisterTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.widget.SegoEditText;
import com.google.android.recaptcha.RecaptchaAction;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class RegisterActivity extends BaseSignOnActivity {

    private SegoEditText etRegisterUsername;
    private SegoEditText etRegisterMail;
    private SegoEditText etRegisterPassword;
    private Button btnRegister;
    private ConstraintLayout btSignInGoogle;
    private ConstraintLayout btSignInTwitter;
    private CheckBox checkBoxAgreement;
    private LinearLayout cardFormRegister;
    private MaterialProgressBar progressView;
    private BroadcastReceiver registerReceiver;
    private TextView tvAgreementRegister;
    private String usernameInput;
    private String emailInput;
    private String passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeComponents();
        setupUi();
    }

    /**
     * Binds view resources to their property
     */
    private void initializeComponents(){
        btnRegister = findViewById(R.id.btSignUp);
        btSignInGoogle = findViewById(R.id.btSignInGoogle);
        btSignInTwitter = findViewById(R.id.btSignInTwitter);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterMail = findViewById(R.id.etRegisterMail);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        checkBoxAgreement = findViewById(R.id.checkboxAgreement);
        cardFormRegister = findViewById(R.id.cardFormRegister);
        progressView = findViewById(R.id.progressViewRegister);
        tvAgreementRegister = findViewById(R.id.tvAgreementRegister);
    }


    /**
     * Executes register operation within the recaptcha context
     * @param username Username
     * @param email Email
     * @param password Password
     */
    private void executeRegisterTask(String username, String email, String password) {
        if (!this.isRecaptchaReady()) {
            Log.e("Recaptcha", "Recaptcha client is not initiated");
            showCaptchaFailureDialog();
            return;
        }
        this.getRecaptchaTaskClient()
            .executeTask(RecaptchaAction.LOGIN)
            .addOnSuccessListener(this, token ->
                RegisterTask.registerTask(this, username, email, password, true, token))
            .addOnFailureListener(this, e -> {
                Log.e("Recaptcha", "Token generation failure");
                showCaptchaFailureDialog();
            });
    }


    @Override
    protected void setUpReceivers(){
        super.setUpReceivers();
        /* Catches broadcasts of api/RegisterTask class */
        registerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("Register receiver", "Got message: " + status);
                setProgress(false);
                switch (status) {
                    case ErrorCodes.SUCCESS:
                        // Session is started, go back to splash activity
                        Intent i =new Intent(getApplicationContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    case ErrorCodes.USERNAME_NOT_LEGAL:
                        etRegisterUsername.setError(getString(R.string.error_username_not_legal));
                        break;
                    case ErrorCodes.USERNAME_ALREADY_EXIST:
                        etRegisterUsername.setError(getString(R.string.error_username_taken));
                        break;
                    case ErrorCodes.EMAIL_ALREADY_EXIST:
                        etRegisterMail.setError(getString(R.string.error_email_already_exist));
                        break;
                    case ErrorCodes.EMAIL_NOT_LEGAL:
                        etRegisterMail.setError(getString(R.string.error_email_not_legal));
                        break;
                    case ErrorCodes.PASSWORD_NOT_LEGAL:
                        etRegisterPassword.setError(getString(R.string.error_password_not_legal));
                        break;
                    default:
                        // An error occurred
                        showErrorDialog(getString(R.string.error_no_connection));
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(registerReceiver, new IntentFilter(ConstantValues.ACTION_REGISTER));
    }

    private void setupUi(){

        btnRegister.setOnClickListener(v -> {
            etRegisterUsername.clearError();
            etRegisterMail.clearError();
            etRegisterPassword.clearError();

            checkBoxAgreement.setBackground(getDrawable(R.drawable.selector_check_box));

            usernameInput = etRegisterUsername.getText();
            emailInput = etRegisterMail.getText();
            passwordInput = etRegisterPassword.getText();
            boolean conditions = checkBoxAgreement.isChecked();

            // Validate fields
            if (usernameInput.isEmpty()){
                etRegisterUsername.setError(getResources().getString(R.string.error_username_empty));
            }
            if (emailInput.isEmpty()){
                etRegisterMail.setError(getResources().getString(R.string.error_email_empty));
            }
            if (passwordInput.isEmpty()){
                etRegisterPassword.setError(getResources().getString(R.string.error_password_empty));
            }
            if (!conditions){
                checkBoxAgreement.setBackground(getDrawable(R.drawable.checkbox_error));
            }
            if (!usernameInput.isEmpty()
                    && !emailInput.isEmpty()
                    && !passwordInput.isEmpty()
                    && conditions) {
                Log.i("Register Activity","Register request send");
                setProgress(true);
                this.executeRegisterTask(usernameInput, emailInput, passwordInput);
            }
        });

        tvAgreementRegister.setOnClickListener(v -> {
            String url = getResources().getString(R.string.conditions_link);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        etRegisterPassword.addTextChangedListener(new TextWatcher() {
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
                    etRegisterPassword.setError(getString(R.string.error_password_empty));
                } else {
                    etRegisterPassword.clearError();
                }
            }
        });

        etRegisterMail.addTextChangedListener(new TextWatcher() {
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
                //this method is empty
                if(s.toString().isEmpty()){
                    etRegisterMail.setError(getResources().getString(R.string.error_email_empty));
                } else {
                    etRegisterMail.clearError();
                }
            }
        });

        etRegisterUsername.addTextChangedListener(new TextWatcher() {
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
                //this method is empty
                if(s.toString().isEmpty()){
                    etRegisterUsername.setError(getResources().getString(R.string.error_username_empty));
                } else {
                    etRegisterUsername.clearError();
                }
            }
        });

        checkBoxAgreement.setOnClickListener(v -> checkBoxAgreement.setBackground(getDrawable(R.drawable.selector_check_box)));

        // Set Google Sign In
        btSignInGoogle.setOnClickListener(v -> onGoogleSignInClicked());
        btSignInTwitter.setOnClickListener(v -> onTwitterSignInClicked());
    }

    @Override
    protected void setProgress(boolean visible){
        if (visible) {
            // Disable form elements
            etRegisterUsername.setEnabled(false);
            etRegisterMail.setEnabled(false);
            etRegisterPassword.setEnabled(false);
            checkBoxAgreement.setEnabled(false);
            btnRegister.setEnabled(false);

            cardFormRegister.setAlpha(0.8f);
            progressView.setVisibility(View.VISIBLE);

        } else {
            //Enable form elements
            etRegisterUsername.setEnabled(true);
            etRegisterMail.setEnabled(true);
            etRegisterPassword.setEnabled(true);
            checkBoxAgreement.setEnabled(true);
            btnRegister.setEnabled(true);

            cardFormRegister.setAlpha(1f);
            progressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignInButtonClicked(View view) {
        Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(registerReceiver);
        super.onDestroy();
    }
}