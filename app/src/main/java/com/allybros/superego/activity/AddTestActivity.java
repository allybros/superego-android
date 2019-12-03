package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddTestActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    WebView addTestWebview;
    public static String addPageHTML;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);
        addTestWebview = (WebView) findViewById(R.id.add_test_webview);
        WebSettings webSettings = addTestWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String url = "https://demo.allybros.com/superego/create.php";
        String postData = null;
        try {
            postData = "session-token=" + URLEncoder.encode(SplashActivity.session_token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        };
        addTestWebview.postUrl(url,postData.getBytes());
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
