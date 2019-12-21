package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OneShotPreDrawListener;

import com.allybros.superego.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddTestActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    WebView addTestWebview;
    private ProgressBar progressBar;
    public static String addPageHTML;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String postData=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);
        addTestWebview = (WebView) findViewById(R.id.add_test_webview);
        addTestWebview.getSettings().setJavaScriptEnabled(true);


        progressBar=(ProgressBar)findViewById(R.id.progress_circular);
        String url = "https://demo.allybros.com/superego/create.php";
//        Map<String,String> postData = new HashMap<>();
//        postData.put("session-token",SplashActivity.session_token);
        try {
            postData = "session-token=" + URLEncoder.encode(SplashActivity.session_token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        addTestWebview.postUrl(url,postData.getBytes());


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                addTestWebview.setVisibility(View.VISIBLE);
            }
        }, 1000);




        slidr= Slidr.attach(this);
        slidr.unlock();

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddTestActivity.this,UserPageActivity.class);
        startActivity(intent);
        finish();
    }
}
