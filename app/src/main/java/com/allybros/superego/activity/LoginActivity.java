package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.api.SocialMediaSignInTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton btLogin;
    private TextInputEditText etUid;
    private TextInputEditText etPassword;
    private TextView tvRegister;
    public TextInputLayout passwordTextInput, usernameTextInput;
    private Button btSignInFacebook, btSignInGoogle;
    private LinearLayout cardFormLogin;
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
        setupReceivers();
        setupUi();
    }

    private void initializeComponents(){
        usernameTextInput = findViewById(R.id.inputLayoutLoginUid);
        btLogin = findViewById(R.id.btLogin);
        btHiddenFacebook = findViewById(R.id.btHiddenFacebook);
        btSignInGoogle = findViewById(R.id.btSignInGoogle);
        btSignInFacebook = findViewById(R.id.btSignInFacebook);
        tvRegister = findViewById(R.id.tvRegister);
        etUid = findViewById(R.id.etLoginUid);
        etPassword = findViewById(R.id.etLoginPassword);
        passwordTextInput = findViewById(R.id.inputLayoutLoginPassword);
        cardFormLogin = findViewById(R.id.cardFormLogin);
        progressView = findViewById(R.id.progressViewLogin);
    }

    private void setupReceivers(){
        /* Catches broadcasts of api/LoginTask class */
        loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("receiver", "Got message: " + status);
                setProgress(false);
                switch (status){

                    case ErrorCodes.SYSFAIL:
                    case ErrorCodes.CAPTCHA_REQUIRED:
                        usernameTextInput.setError(" ");
                        passwordTextInput.setError(getString(R.string.error_login_failed));
                        YoYo.with(Techniques.Shake)
                                .duration(400)
                                .playOn(findViewById(R.id.cardFormLogin));
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
                        builder1.setMessage(getString(R.string.check_connection));
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
                Log.d("receiver", "Got message: " + status);
                setProgress(false);
                switch (status){

                    case ErrorCodes.SYSFAIL:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.sysfail_login_social_media)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;

                    case ErrorCodes.EMAIL_EMPTY:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.social_media_email_empty)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;
                    case ErrorCodes.EMAIL_NOT_LEGAL:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.social_media_email_not_legal)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;
                    case ErrorCodes.USERNAME_NOT_LEGAL:
                        new AlertDialog.Builder(LoginActivity.this, R.style.SegoAlertDialog)
                            .setTitle("insightof.me")
                            .setMessage(R.string.social_media_username_not_legal)
                            .setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            }).show();
                        break;

                    case ErrorCodes.SUCCESS:
                        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;

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

        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, new IntentFilter(ConstantValues.ACTION_LOGIN));
        LocalBroadcastManager.getInstance(this).registerReceiver(loginSocialMediaReceiver, new IntentFilter(ConstantValues.ACTION_SOCIAL_MEDIA_LOGIN));
    }

    private void setupUi(){
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            passwordTextInput.setErrorEnabled(false);
            usernameTextInput.setErrorEnabled(false);

            if(etUid.getText().toString().isEmpty()){
                usernameTextInput.setError(getString(R.string.error_username_empty));
            }
            if(etPassword.getText().toString().isEmpty()){
                passwordTextInput.setError(getString(R.string.error_password_empty));
            }
            if(!etPassword.getText().toString().isEmpty() && !etUid.getText().toString().isEmpty()){
                setProgress(true);
                LoginTask.loginTask(getApplicationContext(), etUid.getText().toString(),etPassword.getText().toString());
            }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
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

    //Provides that cacth the results that come back from Google an Facebook sign in //TODO: Make javadoc
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //Sends request to Ally Bros Api for signing in with Google TODO: Make javadoc.
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
        }
    }

    /**
     * Shows progress view upon login form and disables form items,
     * Indicates a process is going on
     * @param visible Set true when progress view needs to be shown.
     */
    private void setProgress(boolean visible) {
        if (visible) {
            passwordTextInput.setEnabled(false);
            usernameTextInput.setEnabled(false);
            btLogin.setEnabled(false);
            cardFormLogin.setAlpha(0.8f);
            progressView.setVisibility(View.VISIBLE);
        } else {
            passwordTextInput.setEnabled(true);
            usernameTextInput.setEnabled(true);
            btLogin.setEnabled(true);
            cardFormLogin.setAlpha(1f);
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

}
