package com.allybros.superego.activity.webview

import androidx.appcompat.app.AppCompatActivity
import com.r0adkll.slidr.model.SlidrInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.annotation.SuppressLint
import android.os.Bundle
import com.allybros.superego.R
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.allybros.superego.databinding.ActivityWebviewBinding
import com.allybros.superego.unit.ErrorCodes
import com.allybros.superego.util.SessionManager
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.r0adkll.slidr.Slidr
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class WebViewActivity : AppCompatActivity() {
    private var slidr: SlidrInterface? = null
    private var webViewClient: WebViewClient? = null
    private var url: String? = null
    private var title: String? = null

    lateinit var binding: ActivityWebviewBinding
    val viewModel: WebViewVM by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Get intent extras, url and title
        val intent = intent
        url = intent.getStringExtra("url")
        title = intent.getStringExtra("title")
        val unlockSlidr = intent.getBooleanExtra("slidr", true)

        // Init layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)

        //Set title
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setTitle(title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //Set webview
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.settings.setAppCacheEnabled(false)
        binding.webView.isSoundEffectsEnabled = true
        //Set webview client
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                val responseUri = Uri.parse(url)
                if (responseUri.scheme == "intent") {
                    // Valid intent scheme
                    val startIndex = url.indexOf("://")
                    val actionName = url.substring(startIndex + 3).split("\\?").toTypedArray()[0]
                    val status = responseUri.getQueryParameter("status")
                    Log.d("WebView Result", "$actionName, $status")
                    //TODO: Decouple result handling from this class
                    if (actionName == WEB_ACTION_CREATE_TEST && "" + status == "" + ErrorCodes.SUCCESS) SessionManager.getInstance()
                        .touchSession() //User info modified.
                    // Activity result
                    finish()
                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.imageViewLogo.setVisibility(View.INVISIBLE)
                binding.webView.setVisibility(View.VISIBLE)
                YoYo.with(Techniques.FadeIn).duration(400).playOn(binding.webView)
                super.onPageFinished(view, url)
            }
        }
        binding.webView.setWebViewClient(webViewClient)

        //Loading animation
        YoYo.with(Techniques.Bounce)
            .duration(1000)
            .repeat(50)
            .playOn(findViewById(R.id.imageViewLogo))

        // Post session-token
        var postData: String? = null
        try {
            postData = "session-token=" + URLEncoder.encode(
                SessionManager.getInstance().sessionToken,
                "UTF-8"
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        //Load content
        binding.webView.postUrl(url, postData!!.toByteArray())
        if (unlockSlidr) {
            slidr = Slidr.attach(this)
            slidr!!.unlock()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        private const val WEB_ACTION_CREATE_TEST = "create_test"
        private const val WEB_ACTION_RATE = "rate"
    }
}