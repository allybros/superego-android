package com.allybros.superego.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.Result;

public class WebViewActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    private WebView webView;
    private WebViewClient webViewClient;
    private ImageView imageViewLogo;
    private String url;
    private String title;

    public static final String WEB_ACTION_CREATE_TEST = "create_test";
    public static final String WEB_ACTION_RATE = "rate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get intent extras, url and title
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        Boolean unlockSlidr = intent.getBooleanExtra("slidr", true);
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
        //Set webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.setSoundEffectsEnabled(true);
        //Set webview client
        webViewClient = new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Uri responseUri = Uri.parse(url);
                if (Objects.equals(responseUri.getScheme(), "intent")){
                    // Valid intent scheme
                    int startIndex = url.indexOf("://");
                    String actionName = url.substring(startIndex+3).split("\\?")[0];
                    String status = responseUri.getQueryParameter("status");
                    Log.d("WebView Result", actionName + ", "+ status);
                    //TODO: Decouple result handling from this class
                    if (actionName.equals(WEB_ACTION_CREATE_TEST) && Objects.equals(""+status, ""+ErrorCodes.SUCCESS))
                        SessionManager.getInstance().touchSession(); //User info modified.
                    // Activity result
                    finish();
                }
                super.onPageStarted(view, url, favicon);
            }
        };
        webView.setWebViewClient(webViewClient);

        //Loading animation
        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(50)
                .playOn(findViewById(R.id.imageViewLogo));

        String postData=null;
        try {
            postData = "session-token=" + URLEncoder.encode(SessionManager.getInstance().getSessionToken(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Load content
        webView.postUrl(url, postData.getBytes());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewLogo.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            }
        }, 2000);

        if (unlockSlidr){
            slidr= Slidr.attach(this);
            slidr.unlock();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
