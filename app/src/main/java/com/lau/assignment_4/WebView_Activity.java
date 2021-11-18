package com.lau.assignment_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebView_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();

//        TextView textView = findViewById(R.id.textView);
        WebView webView = findViewById(R.id.webView);


        String title = intent.getStringExtra("title");
//        textView.setText(title);

        String label = title.substring(0, (title.length() > 20 ? 20 : title.length()));
        this.setTitle("Article: " + label + "...");

        String url = intent.getStringExtra("url");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}