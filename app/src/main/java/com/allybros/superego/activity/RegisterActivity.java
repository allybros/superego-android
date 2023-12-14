package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.api.RegisterTask;
import com.allybros.superego.api.SocialMediaSignInTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.widget.SegoEditText;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class RegisterActivity extends AppCompatActivity {

    private SegoEditText etRegisterUsername,etRegisterMail,etRegisterPassword;
    private Button btnRegister;
    private ConstraintLayout btSignInFacebook, btSignInGoogle;
    private CheckBox checkBoxAgreement;
    private LinearLayout cardFormRegister;
    private MaterialProgressBar progressView;
    private BroadcastReceiver registerReceiver;
    private BroadcastReceiver autoLoginReceiver;
    private TextView tvAgreementRegister, tvSignIn;
    private String usernameInput;
    private String emailInput;
    private String passwordInput;

    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 0;    // It require to come back from Google Sign in intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeComponents();
        setupReceivers(this);
        setupUi(this);
    }

    private void initializeComponents(){
        btnRegister = findViewById(R.id.btSignUp);
        btSignInGoogle = findViewById(R.id.btSignInGoogle);
        btSignInFacebook = findViewById(R.id.btSignInFacebook);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterMail = findViewById(R.id.etRegisterMail);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        checkBoxAgreement = findViewById(R.id.checkboxAgreement);
        cardFormRegister = findViewById(R.id.cardFormRegister);
        progressView = findViewById(R.id.progressViewRegister);
        tvAgreementRegister = findViewById(R.id.tvAgreementRegister);
        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void setupReceivers(final RegisterActivity activity){
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
                        LoginTask.loginTask(getApplicationContext(), activity.usernameInput, activity.passwordInput);
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

    private void setupUi(final RegisterActivity activity){

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etRegisterUsername.clearError();
                etRegisterMail.clearError();
                etRegisterPassword.clearError();

                checkBoxAgreement.setBackground(getDrawable(R.drawable.selector_check_box));

                usernameInput = etRegisterUsername.getText();
                emailInput = etRegisterMail.getText();
                passwordInput = etRegisterPassword.getText();
                boolean conditions = checkBoxAgreement.isChecked();


                //Validate fields
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
                    Log.d("Register request send","Register request send");
                    setProgress(true);
                    RegisterTask.registerTask(getApplicationContext(), etRegisterUsername.getText(), etRegisterMail.getText(), etRegisterPassword.getText(), true);
                    etRegisterUsername.setText("");
                    etRegisterMail.setText("");
                    etRegisterPassword.setText("");
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            tvSignIn.setText(Html.fromHtml(getResources().getString(R.string.action_login)), TextView.BufferType.SPANNABLE);
        }

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

        checkBoxAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxAgreement.setBackground(getDrawable(R.drawable.selector_check_box));
            }
        });

        //Set Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.GOOGLE_CLIENT_ID))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        btSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
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

    /**
     * Provides that cacth the results that come back from Google an Facebook sign in
     * @param data is a variable that comes back from the intent. It includes data that is needed.
     * @param requestCode is a variable that defines what intent come back.
     * @param resultCode  is a variable that defines intent result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            setProgress(true);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * Sends request to Ally Bros Api for signing in with Google
     * @param completedTask that has provided from GoogleSignInApi. It has result of google sign in task.
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.w("GoogleSignInSuccess", account.getDisplayName()+account.getEmail()+account.getPhotoUrl()+account.getIdToken());
            SocialMediaSignInTask.loginTask(getApplicationContext(),account.getIdToken(),"google");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleSignInError", "signInResult:failed code=" + e.getStatusCode());
            setProgress(false);
            // Show error dialog
            new AlertDialog.Builder(RegisterActivity.this, R.style.SegoAlertDialog)
                    .setTitle("insightof.me")
                    .setMessage(R.string.error_google_signin)
                    .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(registerReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(autoLoginReceiver);
        super.onDestroy();
    }
}