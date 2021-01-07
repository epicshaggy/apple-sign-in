package com.getcapacitor.community.applesignin

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.getcapacitor.community.applesignin.applesignin.R

class LoginActivity : AppCompatActivity() {
    var appleDialog: Dialog? = null
    var authUrl: String? = null
    var redirectURI: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authUrl = intent.getStringExtra("authUrl")
        redirectURI = intent.getStringExtra("redirectURI")
        Log.i("url", authUrl)
        setupAppleWebViewDialog()
    }

    private fun setupAppleWebViewDialog() {
        appleDialog = Dialog(this)
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.evaluateJavascript(assets.open("requestInterceptor.js").reader().readText(), null)
        webView.addJavascriptInterface(RequestInterceptor(), "requestInterceptor")
        webView.webViewClient = AppleWebViewClient()
        webView.loadUrl(authUrl)
        appleDialog!!.setContentView(webView)
        appleDialog!!.show()
    }

    private inner class AppleWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            // retrieve display dimensions
            val displayRectangle = Rect()
            val window = this@LoginActivity.window
            window.decorView.getWindowVisibleDisplayFrame(displayRectangle)

            // Set height of the Dialog to 90% of the screen
            val layoutParams = view.layoutParams
            layoutParams.width = Math.round(displayRectangle.width() * 0.9f)
            layoutParams.height = Math.round(displayRectangle.height() * 0.9f)
            view.layoutParams = layoutParams
        }
    }
}