package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;

public class AddTestActivity extends AppCompatActivity {
//TODO:Sayfa yalnızca oluşturuldu. Bir ara yapılır inş
    WebView addTestWebview;
    public static String addPageHTML;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);
        addTestWebview = (WebView) findViewById(R.id.add_test_webview);
        WebSettings webSettings = addTestWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String encodedHtml = Base64.encodeToString(addPageHTML.getBytes(),
                Base64.NO_PADDING);
        addTestWebview.loadData(encodedHtml, "text/html", "base64");
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddTestActivity.this,UserPageActivity.class);
        startActivity(intent);
        finish();
    }
}
