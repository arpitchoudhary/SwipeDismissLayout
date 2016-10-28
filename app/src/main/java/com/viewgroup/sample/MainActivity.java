package com.viewgroup.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.viewgroup.SwipeDismissLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SwipeDismissLayout layout = (SwipeDismissLayout) findViewById(R.id.container);
//        layout.setSwipeEnabled(true);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.createappfaster.com/");
    }
}
