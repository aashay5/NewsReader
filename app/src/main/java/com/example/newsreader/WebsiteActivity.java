package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebsiteActivity extends AppCompatActivity {
    private WebView webActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);


        Intent intent=getIntent();
        if (null!=intent) {
            String url=intent.getStringExtra("url");
            if (null!=url) {
                webActivity=findViewById(R.id.webActivity);
                webActivity.setWebViewClient(new WebViewClient());
                webActivity.getSettings().setJavaScriptEnabled(true);
                webActivity.loadUrl(url);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webActivity.canGoBack()){
            getObbDir();
        }else{
            super.onBackPressed();
        }
    }
}