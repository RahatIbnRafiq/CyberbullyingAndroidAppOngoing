package com.example.cybersafetyapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InstagramLogin extends AppCompatActivity {

    private WebView webView;
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    public String email;
    public String usernameToBeSearched;
    public int requestType;


    private boolean checkIfValidIntent(Intent intent)
    {
        try {
            Bundle messages = intent.getExtras();
            this.email = messages.getString(IntentSwitchVariables.email);
            this.usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            this.requestType = messages.getInt(IntentSwitchVariables.request);
            return true;
        }
        catch (Exception e)
        {
            Log.i(UtilityVariables.tag,"Exception in checkIfValidIntent in class: "+this.getClass().getName()+" : "+e.toString());
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);
        if(!this.checkIfValidIntent(getIntent()))
        {
            Intent intent = new Intent(Dashboard.class.getName());
            intent.putExtra(IntentSwitchVariables.email,this.email);
            startActivity(intent);
        }
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        clearCookies(this);
        webView.setWebViewClient(new WebViewClient() {
            private int running = 0;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if(url.startsWith(UtilityVariables.INSTAGRAM_CALLBACK_URL))
                {
                    Intent intent = new Intent(WaitForToken.class.getName());
                    intent.putExtra(IntentSwitchVariables.OSNName,IntentSwitchVariables.INSTAGRAM);
                    intent.putExtra(IntentSwitchVariables.InstagramLoginCode,url);
                    intent.putExtra(IntentSwitchVariables.email,email);
                    intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,usernameToBeSearched);
                    intent.putExtra(IntentSwitchVariables.request,requestType);
                    startActivity(intent);
                }
                running++;
                webView.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.startsWith(UtilityVariables.INSTAGRAM_CALLBACK_URL))
                {
                    Intent intent = new Intent(WaitForToken.class.getName());
                    intent.putExtra(IntentSwitchVariables.OSNName,IntentSwitchVariables.INSTAGRAM);
                    intent.putExtra(IntentSwitchVariables.InstagramLoginCode,url);
                    intent.putExtra(IntentSwitchVariables.email,email);
                    intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,usernameToBeSearched);
                    intent.putExtra(IntentSwitchVariables.request,requestType);
                    startActivity(intent);
                }
                running = Math.max(running, 1);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(--running == 0) {
                    Log.i(UtilityVariables.tag,"finished loading page: "+url);
                }
            }
        });


        String mAuthUrl = AUTH_URL + "?client_id=" + UtilityVariables.INSTAGRAM_CLIENT_ID + "&redirect_uri="
                + UtilityVariables.INSTAGRAM_CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships+public_content";
        webView.loadUrl(mAuthUrl);
    }


    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
