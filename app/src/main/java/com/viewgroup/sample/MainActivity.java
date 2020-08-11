package com.viewgroup.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import static com.viewgroup.sample.Helper.PREFERENCES_NAME_LANG_CODE;
import static com.viewgroup.sample.Helper.PREFERENCES_PARENT_LANG;

public class MainActivity extends BaseActivity {

    private int previousLanguageSelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button_top).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromTopActivity.class)));
        findViewById(R.id.button_start).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromStartActivity.class)));
        findViewById(R.id.button_bottom).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromBottomActivity.class)));
        findViewById(R.id.button_end).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DismissFromEndActivity.class)));


        ArrayList<String> layoutDirections = new ArrayList<>();
        layoutDirections.add("LTR");
        layoutDirections.add("RTL");

        Spinner spinner =((Spinner)findViewById(R.id.spinner_layout_direction));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, layoutDirections);
        spinner.setAdapter(spinnerAdapter);
        int selection = Helper.loadPreference(this, PREFERENCES_PARENT_LANG, PREFERENCES_NAME_LANG_CODE, "en").equals("fa")?1:0;
        previousLanguageSelection = selection;
        spinner.setSelection(selection);


        ((Spinner)findViewById(R.id.spinner_layout_direction)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Helper.savePreference(MainActivity.this, PREFERENCES_PARENT_LANG, PREFERENCES_NAME_LANG_CODE, position==1?"fa":"en");
                if (position != previousLanguageSelection) {
                    MainActivity.this.recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
