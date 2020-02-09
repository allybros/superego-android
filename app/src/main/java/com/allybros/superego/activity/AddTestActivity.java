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
import com.allybros.superego.api.ProfileRefreshTask;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AddTestActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    WebView addTestWebview;
//    private BernoullisBowProgressView progressBar;
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

//        progressBar=(BernoullisBowProgressView)findViewById(R.id.progress_circular);
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
//                progressBar.setVisibility(View.INVISIBLE);
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
        ProfileRefreshTask.profileRefreshTask(getApplicationContext(),SplashActivity.session_token);
        super.onDestroy();
    }
}
