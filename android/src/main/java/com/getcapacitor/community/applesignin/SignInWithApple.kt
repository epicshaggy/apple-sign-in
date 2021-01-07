package com.getcapacitor.community.applesignin

import android.content.Intent
import android.net.Uri
import com.getcapacitor.NativePlugin
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.community.applesignin.SignInWithApple

@NativePlugin(requestCodes = [SignInWithApple.AUTH_CODE])
class SignInWithApple : Plugin() {
    var authUrl = "https://appleid.apple.com/auth/authorize"

    @PluginMethod
    fun authorize(call: PluginCall) {
        val clientId = call.getString("clientId", null)
        val redirectURI = call.getString("redirectURI", null)
        if (clientId == null || redirectURI == null) {
            call.reject("No options were provided.")
            return
        }
        var authenticationUri = Uri
                .parse(authUrl)
                .buildUpon()
                .appendQueryParameter("client_id", clientId)
                .appendQueryParameter("redirect_uri", redirectURI)
                .appendQueryParameter("response_type", "code id_token")
                .build()
        if (call.hasOption("state")) {
            authenticationUri = authenticationUri
                    .buildUpon()
                    .appendQueryParameter("state", call.getString("state"))
                    .build()
        }
        if (call.hasOption("nonce")) {
            authenticationUri = authenticationUri
                    .buildUpon()
                    .appendQueryParameter("nonce", call.getString("nonce"))
                    .build()
        }
        val scopes = call.getString("scopes", "name email")
        authenticationUri = authenticationUri
                .buildUpon()
                .appendQueryParameter("scope", scopes)
                .appendQueryParameter("response_mode", "form_post")
                .build()
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("authUrl", authenticationUri.toString())
        intent.putExtra("redirectURI", redirectURI)
        startActivityForResult(call, intent, AUTH_CODE)
    }

    companion object {
        const val AUTH_CODE = 67
    }
}