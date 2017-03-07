package com.example.ekkert.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.Storage;
import ru.mail.weather.lib.Topics;

public class SettingsActivity extends AppCompatActivity {

    private final View.OnClickListener onBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String topic = ((Button)view).getText().toString();
            Storage.getInstance(SettingsActivity.this).saveCurrentTopic(topic);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.button).setOnClickListener(onBtnClick);
        findViewById(R.id.button2).setOnClickListener(onBtnClick);
        findViewById(R.id.button3).setOnClickListener(onBtnClick);
    }
}
