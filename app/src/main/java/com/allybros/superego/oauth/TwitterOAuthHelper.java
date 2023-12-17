package com.allybros.superego.oauth;

import android.util.Log;

import java.util.UUID;

import okhttp3.HttpUrl;

public class TwitterOAuthHelper {

    private final String clientId;
    private final String challenge;
    private final String state;
    private static final String ANDROID_TWITTER_REDIRECT_URL = "twitter://superego/login";
    private static final String TWITTER_API_SCOPE = "users.read tweet.read offline.access";

    /**
     * Twitter Oauth Helper for generating required parameters for twitter oauth request
     * @param clientId Twitter client id
     */
    public TwitterOAuthHelper(String clientId) {
        this.clientId = clientId;
        this.challenge = UUID.randomUUID().toString();
        this.state = UUID.randomUUID().toString();
        Log.i(this.getClass().getName(), "Created twitter oauth helper with challenge: " +
                challenge + " state: " + state);
    }

    /**
     * Make request to Twitter and create a Login URL
     * @return Login URL for twitter
     */
    public String getTwitterLoginUrl() {
        Log.i(this.getClass().getName(), "Creating twitter login url");
        String loginUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("twitter.com")
                .addPathSegments("i/oauth2/authorize")
                .addQueryParameter("response_type", "code")
                .addQueryParameter("client_id", this.clientId)
                .addQueryParameter("redirect_uri", ANDROID_TWITTER_REDIRECT_URL)
                .addQueryParameter("scope", TWITTER_API_SCOPE)
                .addQueryParameter("state", this.state)
                .addQueryParameter("code_challenge", this.challenge)
                .addQueryParameter("code_challenge_method", "plain")
                .build().toString();
        Log.i(this.getClass().getName(), "Created login url " + loginUrl);
        return loginUrl;
    }

    public String getChallenge() {
        return challenge;
    }

    public String getState() {
        return state;
    }
}
