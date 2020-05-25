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

public class LoginActivity extends AppCompatActivity {
    private MaterialButton btLogin;

    private MaterialButton btRegister;
    private TextInputEditText etMail;
    private TextInputEditText etPassword;
    public TextInputLayout passwordTextInput, usernameTextInput;
    static LoginButton signInFacebook;
    private Button btSignInFacebook, btSignInGoogle;
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
        usernameTextInput = findViewById(R.id.username_text_input);
        btLogin = findViewById(R.id.btLogin);
        signInFacebook = findViewById(R.id.sign_in_facebook);
        btSignInGoogle = findViewById(R.id.btSignInGoogle);
        btSignInFacebook = findViewById(R.id.btSignInFacebook);
        btRegister = findViewById(R.id.btRegister);
        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        passwordTextInput = findViewById(R.id.password_text_input);
    }

    private void setupReceivers(){
        /**
         * Catches broadcasts of api/LoginTask class
         */
        loginReceiver = new BroadcastReceiver() {
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
                        builder.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
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

                    case ErrorCodes.SUCCESS:


                        Intent intent1=new Intent(getApplicationContext(), SplashActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        break;

                    case ErrorCodes.CONNECTION_ERROR:
                        passwordTextInput.setError(getString(R.string.checkConnection));
                        usernameTextInput.setError(getString(R.string.checkConnection));
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setTitle("insightof.me");
                        builder1.setMessage(getString(R.string.checkConnection));
                        builder1.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
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
        /**
         * Catches broadcasts of api/SocialMediaSignInTask class
         */
        loginSocialMediaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("receiver", "Got message: " + status);
                switch (status){

                    case ErrorCodes.SYSFAIL:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                        builder2.setTitle("insightof.me");
                        builder2.setMessage(R.string.sysfail_login_social_media);
                        builder2.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder2.show();
                        break;

                    case ErrorCodes.EMAIL_EMPTY:
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(LoginActivity.this);
                        builder3.setTitle("insightof.me");
                        builder3.setMessage(R.string.social_media_email_empty);
                        builder3.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder3.show();
                        break;
                    case ErrorCodes.EMAIL_NOT_LEGAL:
                        AlertDialog.Builder builder4 = new AlertDialog.Builder(LoginActivity.this);
                        builder4.setTitle("insightof.me");
                        builder4.setMessage(R.string.social_media_email_not_legal);
                        builder4.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder4.show();
                        break;
                    case ErrorCodes.USERNAME_NOT_LEGAL:
                        AlertDialog.Builder builder5 = new AlertDialog.Builder(LoginActivity.this);
                        builder5.setTitle("insightof.me");
                        builder5.setMessage(R.string.social_media_username_not_legal);
                        builder5.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder5.show();
                        break;

                    case ErrorCodes.SUCCESS:
                        Intent intent1=new Intent(getApplicationContext(), SplashActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        break;

                    case ErrorCodes.CONNECTION_ERROR:
                        passwordTextInput.setError(getString(R.string.checkConnection));
                        usernameTextInput.setError(getString(R.string.checkConnection));
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setTitle("insightof.me");
                        builder1.setMessage(getString(R.string.checkConnection));
                        builder1.setPositiveButton( getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                        builder1.show();
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
                signInFacebook.callOnClick();
            }
        });
        callbackManager = CallbackManager.Factory.create();
        signInFacebook.setPermissions(Arrays.asList("email","public_profile"));

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

    //Provides that cacth the results that come back from Google an Facebook sign in
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

    //Sends request to Ally Bros Api for signing in with Google
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //Delete receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginSocialMediaReceiver);
        super.onDestroy();
    }

}
