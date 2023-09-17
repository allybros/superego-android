package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.api.SocialMediaSignInTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.widget.SegoEditText;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton btLogin;
    private SegoEditText etPassword, etUid;
    private TextView tvRegister;
    private ConstraintLayout btSignInFacebook, btSignInGoogle;
    private MaterialProgressBar progressView;

    static LoginButton btHiddenFacebook;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 0;    // It require to come back from Google Sign in intent
    private BroadcastReceiver loginSocialMediaReceiver, loginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponents();
        setupReceivers(this);
        setupUi(this);
    }

    private void initializeComponents(){
        btLogin = findViewById(R.id.btLogin);
        btHiddenFacebook = findViewById(R.id.btHiddenFacebook);
        btSignInGoogle = findViewById(R.id.btSignInGoogle);
        btSignInFacebook = findViewById(R.id.btSignInFacebook);
        tvRegister = findViewById(R.id.tvRegister);
        etUid = findViewById(R.id.etLoginUid);
        etPassword = findViewById(R.id.etPassword);
        progressView = findViewById(R.id.progressViewLogin);
    }

    private void setupReceivers(final LoginActivity activity){
        /* Catches broadcasts of api/LoginTask class */
        loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("Login receiver", "Status: " + status);
                setProgress(false);
                switch (status){

                    case ErrorCodes.SYSFAIL:
                        //TODO: We should write something
                        break;
                    case ErrorCodes.CAPTCHA_REQUIRED:
                        etUid.setError(getString(R.string.error_login_failed));
                        etPassword.setError(getString(R.string.error_login_failed));
                        break;

                    case ErrorCodes.SUSPEND_SESSION:
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog);
                        builder.setTitle("insightof.me");
                        builder.setMessage(getString(R.string.error_desc_session_suspended));
                        builder.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;

                    case ErrorCodes.SUCCESS:
                        //Login User
                        Intent i =new Intent(getApplicationContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;

                    case ErrorCodes.CONNECTION_ERROR:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog);
                        builder1.setTitle("insightof.me");
                        builder1.setMessage(getString(R.string.error_check_connection));
                        builder1.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               dialog.dismiss();
                            }
                        });
                        builder1.show();
                        break;
                }
            }
        };

        /* Listens broadcasts of api/SocialMediaSignInTask class */
        loginSocialMediaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("OAuth Receiver", "Status: " + status);
                setProgress(false);
                switch (status){
                    case ErrorCodes.EMAIL_EMPTY:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_email_empty)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;
                    case ErrorCodes.EMAIL_NOT_LEGAL:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_email_not_legal)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;
                    case ErrorCodes.USERNAME_NOT_LEGAL:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_username_not_legal)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;

                    case ErrorCodes.SUCCESS:
                        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;

                    case ErrorCodes.SYSFAIL:
                    case ErrorCodes.CONNECTION_ERROR:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                                .setTitle("insightof.me")
                                .setMessage(R.string.error_no_connection)
                                .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                }).show();
                        break;
                }
            }
        };

        //Registers Receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, new IntentFilter(ConstantValues.ACTION_LOGIN));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginSocialMediaReceiver, new IntentFilter(ConstantValues.ACTION_SOCIAL_MEDIA_LOGIN));
    }

    private void setupUi(final LoginActivity activity){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            tvRegister.setText(Html.fromHtml(getResources().getString(R.string.desc_call_register)), TextView.BufferType.SPANNABLE);
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            etPassword.clearError();

                etUid.clearError();

            if(etUid.getText().isEmpty()){
                etUid.setError(getString(R.string.error_username_empty));
            }
            if(etPassword.getText().isEmpty()){
                etPassword.setError(getString(R.string.error_password_empty));
            }
            if(!etPassword.getText().isEmpty() && !etUid.getText().toString().isEmpty()){
                setProgress(true);
                LoginTask.loginTask(getApplicationContext(), etUid.getText().toString(),etPassword.getText().toString());
            }
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
                //this method is empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //this method is empty
                if(s.toString().isEmpty()) {
                    etUid.setError(getString(R.string.error_username_empty));
                } else {
                    etUid.clearError();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        //Set Facebook Sign In
        btSignInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btHiddenFacebook.callOnClick();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        btHiddenFacebook.setPermissions(Arrays.asList("email","public_profile"));

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        Log.d("Facebook","B"+accessToken.getToken());
                        SocialMediaSignInTask.loginTask(getApplicationContext(),accessToken.getToken(),"facebook");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
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
        callbackManager.onActivityResult(requestCode,resultCode,data);
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
            new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
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

    /**
     * Shows progress view upon login form and disables form items,
     * Indicates a process is going on
     * @param visible Set true when progress view needs to be shown.
     */
    private void setProgress(boolean visible) {
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginSocialMediaReceiver);
        super.onDestroy();
    }

    public void onRegisterButtonClicked(View view) {
        Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
