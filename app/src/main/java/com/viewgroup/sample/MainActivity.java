package com.viewgroup.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button_top).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromTopActivity.class)));
        findViewById(R.id.button_start).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromStartActivity.class)));
        findViewById(R.id.button_bottom).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromBottomActivity.class)));
        findViewById(R.id.button_end).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromEndActivity.class)));
    }
}
