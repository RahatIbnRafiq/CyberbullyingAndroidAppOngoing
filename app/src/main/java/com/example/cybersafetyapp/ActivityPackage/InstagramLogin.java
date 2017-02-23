package com.example.cybersafetyapp.ActivityPackage;

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

import com.example.cybersafetyapp.R;
import com.example.cybersafetyapp.UtilityPackage.IntentSwitchVariables;
import com.example.cybersafetyapp.UtilityPackage.UtilityVariables;

public class InstagramLogin extends AppCompatActivity {

    private WebView webView;
    private String email;
    private String usernameToBeSearched;
    private String useridGetDetail;
    private int requestType;
    private String classname = this.getClass().getSimpleName();


    private boolean checkIfValidIntent(Intent intent)
    {
        try {
            Bundle messages = intent.getExtras();
            this.email = messages.getString(IntentSwitchVariables.EMAIL);
            this.requestType = messages.getInt(IntentSwitchVariables.REQUEST);
            if(requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                this.usernameToBeSearched = messages.getString(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED);
            else if(requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_ACCESS_TOKEN)
                this.useridGetDetail  = messages.getString(IntentSwitchVariables.USERID_GET_DETAIL);
            return true;
        }
        catch (Exception e)
        {
            Log.i(UtilityVariables.tag,this.classname+": Exception in checkIfValidIntent function: "+e.toString());
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);
        if(!this.checkIfValidIntent(getIntent()))
        {
            Intent intent = new Intent(this,Dashboard.class);
            intent.putExtra(IntentSwitchVariables.EMAIL,this.email);
            startActivity(intent);
        }
        this.webView = (WebView) findViewById(R.id.webView1);
        this.webView.getSettings().setJavaScriptEnabled(true);
        clearCookies(this);
        this.webView.setWebViewClient(new WebViewClient() {
            private int running = 0;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if(url.startsWith(UtilityVariables.INSTAGRAM_CALLBACK_URL))
                {
                    Intent intent = new Intent(getApplicationContext(),WaitForToken.class);
                    intent.putExtra(IntentSwitchVariables.OSN_NAME,IntentSwitchVariables.INSTAGRAM);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_LOGIN_CODE,url);
                    intent.putExtra(IntentSwitchVariables.EMAIL,email);
                    if(requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_ACCESS_TOKEN)
                    {
                        intent.putExtra(IntentSwitchVariables.USERID_GET_DETAIL,useridGetDetail);
                    }
                    else if(requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                    {
                        intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,usernameToBeSearched);
                    }

                    intent.putExtra(IntentSwitchVariables.REQUEST,requestType);
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
                    Intent intent = new Intent(getApplicationContext(),WaitForToken.class);
                    intent.putExtra(IntentSwitchVariables.OSN_NAME,IntentSwitchVariables.INSTAGRAM);
                    intent.putExtra(IntentSwitchVariables.INSTAGRAM_LOGIN_CODE,url);
                    intent.putExtra(IntentSwitchVariables.EMAIL,email);
                    if(requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_ACCESS_TOKEN)
                    {
                        intent.putExtra(IntentSwitchVariables.USERID_GET_DETAIL,useridGetDetail);
                    }
                    else if(requestType == IntentSwitchVariables.REQUEST_INSTAGRAM_USER_SEARCH)
                    {
                        intent.putExtra(IntentSwitchVariables.USERNAME_TO_BE_SEARCHED,usernameToBeSearched);
                    }
                    intent.putExtra(IntentSwitchVariables.REQUEST,requestType);
                    startActivity(intent);
                }
                running = Math.max(running, 1);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(--running == 0) {
                    //(UtilityVariables.tag,"InstagramLogin: Finished loading page: "+url);
                }
            }
        });


        String mAuthUrl = UtilityVariables.INSTAGRAM_AUTH_URL + "?client_id=" + UtilityVariables.INSTAGRAM_CLIENT_ID + "&redirect_uri="
                + UtilityVariables.INSTAGRAM_CALLBACK_URL + "&response_type=code&display=touch&scope=likes+comments+relationships+public_content";
        this.webView.loadUrl(mAuthUrl);
    }


    @SuppressWarnings("deprecation")
    private static void clearCookies(Context context)
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
