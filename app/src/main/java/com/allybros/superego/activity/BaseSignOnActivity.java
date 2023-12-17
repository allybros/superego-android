package com.allybros.superego.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.oauth.TwitterCallbackTask;
import com.allybros.superego.oauth.TwitterOAuthHelper;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.google.android.recaptcha.Recaptcha;
import com.google.android.recaptcha.RecaptchaTasksClient;

public class BaseSignOnActivity extends ComponentActivity {

    protected RecaptchaTasksClient recaptchaTasksClient = null;

    protected BroadcastReceiver oauthLoginReceiver;
    private ActivityResultLauncher<Intent> webViewActivityResultLauncher;

    private TwitterOAuthHelper twitterOAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeRecaptchaClient();
        setUpReceivers();
        registerWebViewActivityResultLauncher();

        // Initialize twitter oauth helper
        this.twitterOAuthHelper = new TwitterOAuthHelper(getString(R.string.twitter_client_id));
    }

    /**
     * Initializes recaptcha client for securing the login call
     */
    protected void initializeRecaptchaClient() {
        final String recaptchaClientKey = this.getString(R.string.recaptcha_client_key);
        Recaptcha.getTasksClient(this.getApplication(), recaptchaClientKey, 5000)
            .addOnSuccessListener(this, client -> {
                Log.i("BaseSignOn", "Created recaptcha client with client key " + recaptchaClientKey);
                this.recaptchaTasksClient = client;
            })
            .addOnFailureListener(this, e ->
                    Log.e("BaseSignOn", "Error occurred on recaptcha client " + e.getMessage())
            );
    }

    /**
     * @return Returns true if recaptcha client is ready
     */
    protected boolean isRecaptchaReady() {
        return this.recaptchaTasksClient != null;
    }

    /**
     * @return Returns recaptcha task client
     */
    protected RecaptchaTasksClient getRecaptchaTaskClient() {
        return this.recaptchaTasksClient;
    }

    /**
     * SetUp API task receivers and register them
     */
    protected void setUpReceivers() {
        // Receive responses from oauth Login
        oauthLoginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                Log.d("OAuthReceiver", "Status: " + status);
                setProgress(false);
                switch (status){
                    case ErrorCodes.EMAIL_EMPTY:
                        showErrorDialog(getString(R.string.error_email_empty));
                        break;
                    case ErrorCodes.EMAIL_NOT_LEGAL:
                        showErrorDialog(getString(R.string.error_email_not_legal));
                        break;
                    case ErrorCodes.USERNAME_NOT_LEGAL:
                        showErrorDialog(getString(R.string.error_username_not_legal));
                        break;
                    case ErrorCodes.SUCCESS:
                        // Start splash activity, so the profile page
                        Log.i("OAuthReceiver", "Successful response, redirect to splash");
                        Intent i = new Intent(getBaseContext(), SplashActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        break;
                    default:
                        showErrorDialog("Failed social media sign in");
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(oauthLoginReceiver, new IntentFilter(ConstantValues.ACTION_SOCIAL_MEDIA_LOGIN));
    }

    protected void onTwitterSignInClicked() {
        // Get twitter login url
        String loginUrl = this.twitterOAuthHelper.getTwitterLoginUrl();
        // Open WebView
        startWebViewActivity(this.getString(R.string.action_login_via_twitter), loginUrl);
    }

    protected void onGoogleSignInClicked() {
        showErrorDialog("To be implemented");
    }

    /**
     * Starts a web view activity with given title and url
     * @param title Title of the string
     * @param url Url to be displayed
     */
    protected void startWebViewActivity(String title, String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        webViewActivityResultLauncher.launch(intent);
    }

    /**
     * Registers web view activity result launcher
     */
    private void registerWebViewActivityResultLauncher() {
        this.webViewActivityResultLauncher = registerForActivityResult(new WebViewActivity.WebViewActivityContract(),
            resultUrl -> {
                final String WEB_VIEW_ACTIVITY_RESULT = "WebViewActivityResult";
                Log.i(WEB_VIEW_ACTIVITY_RESULT, "WebView activity resulted with " + resultUrl);
                if (resultUrl == null) {
                    Log.i(WEB_VIEW_ACTIVITY_RESULT, "User cancelled the operation");
                    return;
                }
                Uri resultUri = Uri.parse(resultUrl);
                String error = resultUri.getQueryParameter("error");
                if (error != null && error.equals("access_denied")) {
                    Log.i(WEB_VIEW_ACTIVITY_RESULT, "User denied the operation");
                    return;
                }
                String code = resultUri.getQueryParameter("code");
                String challenge = this.twitterOAuthHelper.getChallenge();
                if (code == null || challenge == null) {
                    Log.e(WEB_VIEW_ACTIVITY_RESULT, "Can't parse callback url");
                    showErrorDialog(getString(R.string.error_twitter_signin));
                }
                setProgress(true);
                TwitterCallbackTask.handleTwitterCallback(this, code, challenge);
        });
    }

    /**
     * Shows a captcha failure dialog to inform user that request was not verified
     */
    protected void showCaptchaFailureDialog() {
        showErrorDialog(getString(R.string.error_captcha_validation));
    }

    /**
     * Shows a basic alert dialog
     * @param message Message text to be shown
     */
    protected void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SegoAlertDialog);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(message);
        builder.setPositiveButton( getString(R.string.action_ok), (dialog, id) -> dialog.dismiss());
        builder.show();
    }

    protected void setProgress(boolean isProgress) {
        // Can be implemented on child class
    }


}
