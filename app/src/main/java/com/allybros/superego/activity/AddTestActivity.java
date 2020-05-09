package com.allybros.superego.activity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.util.SessionManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddTestActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    WebView addTestWebview;
    private ActionBar toolbar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String postData=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);
        addTestWebview = (WebView) findViewById(R.id.add_test_webview);
        imageView= (ImageView) findViewById(R.id.logo);
        addTestWebview.getSettings().setJavaScriptEnabled(true);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Test Oluştur");

        YoYo.with(Techniques.Bounce)
                .duration(1000)
                .repeat(50)
                .playOn(findViewById(R.id.logo));

        String url = "https://demo.allybros.com/superego/create.php";

        try {
            postData = "session-token=" + URLEncoder.encode(SessionManager.getInstance().getSessionToken(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        addTestWebview.postUrl(url,postData.getBytes());


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.INVISIBLE);
                addTestWebview.setVisibility(View.VISIBLE);
            }
        }, 2000);

        slidr= Slidr.attach(this);
        slidr.unlock();


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AddTestActivity.this,UserPageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d("AddTest","Destroy");
        LoadProfileTask.loadProfileTask(getApplicationContext(),SessionManager.getInstance().getSessionToken(), ConstantValues.getActionRefreshProfile());
        super.onDestroy();
    }
}
