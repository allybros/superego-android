package com.allybros.superego.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    private WebView webView;
    private ImageView imageViewLogo;
    public static final int RESULT_WEB_VIEW = 2024;
    public static final String RESPONSE_URI_EXTRA = "response-uri";
    private static final String WEB_ACTION_CREATE_TEST = "new-test";
    private static final String WEB_ACTION_TWITTER_LOGIN = "twitter";
    private static final String WEB_ACTION_INTENT = "intent";
    private static final String COMPLETED = "completed";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get intent extras, url, title and action type
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String action = intent.getStringExtra("action");
        if (action == null && url != null) {
            final int startIndex = url.indexOf("://");
            action = url.substring(startIndex+3).split("\\?")[0];
        }

        boolean unlockSlidr = intent.getBooleanExtra("slidr", true);

        // Init layout
        setContentView(R.layout.activity_add_test);
        webView = findViewById(R.id.webView);
        imageViewLogo = findViewById(R.id.imageViewLogo);

        //Set title
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set web-view
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setSoundEffectsEnabled(true);
        //Set web-view client
        webView.setWebViewClient(new WebViewActivityClient(action));

        // Loading animation
        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(50)
                .playOn(findViewById(R.id.imageViewLogo));


        // Load URL
        if (action.equals(WEB_ACTION_CREATE_TEST)) {
            Log.i("WebView", "Load for create test action");
            // Post session-token
            String postData = null;
            try {
                postData = "session-token=" + URLEncoder.encode(SessionManager.getInstance().getSessionToken(), "UTF-8");
                Log.d("WebView", "Post Data: " + postData);
            } catch (UnsupportedEncodingException e) {
                Log.e("WebView", "Can't read the post data");
                e.printStackTrace();
            }

            //Load content
            webView.postUrl(url, postData.getBytes());
        } else {
            Log.i("WebView", "Load for default action");
            webView.loadUrl(url);
        }


        if (unlockSlidr){
            slidr= Slidr.attach(this);
            slidr.unlock();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class WebViewActivityClient extends WebViewClient {

        private final String webViewAction;

        public WebViewActivityClient(String webViewAction) {
            this.webViewAction = webViewAction;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Uri responseUri = Uri.parse(url);
            if (Objects.equals(responseUri.getScheme(), WEB_ACTION_INTENT)){
                // Valid intent scheme
                String status = responseUri.getQueryParameter("status");
                Log.d("WebView Result", this.webViewAction + ", "+ status);
                if (this.webViewAction.equals(WEB_ACTION_CREATE_TEST) && Objects.equals(status, COMPLETED))
                    SessionManager.getInstance().touchSession(); //User info modified.
                // Activity result
                finish();
            }
            else if (Objects.equals(responseUri.getScheme(), WEB_ACTION_TWITTER_LOGIN)) {
                // Twitter Login callback
                Log.i("Twitter Result", "Twitter login callback");
                // Activity result
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESPONSE_URI_EXTRA, responseUri.toString());
                setResult(RESULT_WEB_VIEW, resultIntent);
                finish();
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            imageViewLogo.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn).duration(400).playOn(webView);
            super.onPageFinished(view, url);
        }
    }

    public static class WebViewActivityContract extends ActivityResultContract<Intent, String> {

        @NotNull
        @Override
        public Intent createIntent(@NotNull Context context, Intent intent) {
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == WebViewActivity.RESULT_WEB_VIEW && intent != null) {
                // WebView result
                Log.d("WebViewActivityContract", "WebView activity result");
                return intent.getStringExtra(WebViewActivity.RESPONSE_URI_EXTRA);
            } else {
                return null;
            }
        }

    }

}
