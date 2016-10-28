package com.viewgroup.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.viewgroup.SwipeDismissLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeDismissLayout layout = (SwipeDismissLayout) findViewById(R.id.container);
        layout.setSwipeEnabled(true);
    }
}
