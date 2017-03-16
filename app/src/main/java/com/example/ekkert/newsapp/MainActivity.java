package com.example.ekkert.newsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.mail.weather.lib.*;


public class MainActivity extends AppCompatActivity {
    public static final String ACTION_NEW_NEWS = "action.NEW_NEWS";
    public static final String ACTION_ERROR = "action.ERROR";
    private static boolean updateInBg = false;
    private BroadcastReceiver broadcastReceiver = null;
    private final static String TAG = MainActivity.class.getSimpleName();

    private final View.OnClickListener onSettingsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener offBgUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setUpdateInBg(false);
        }
    };

    private final View.OnClickListener onBgUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setUpdateInBg(true);
        }
    };

    private final View.OnClickListener onUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadNews();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_update).setOnClickListener(onUpdateClick);
        findViewById(R.id.btn_on_bg_update).setOnClickListener(onBgUpdateClick);
        findViewById(R.id.btn_off_bg_update).setOnClickListener(offBgUpdateClick);
        findViewById(R.id.btn_settings).setOnClickListener(onSettingsClick);
        if (Storage.getInstance(MainActivity.this).loadCurrentTopic().equals("")) {
            Storage.getInstance(MainActivity.this).saveCurrentTopic(Topics.AUTO);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MainActivity.ACTION_NEW_NEWS)) {
                    printNews();
                }
                if (intent.getAction().equals(MainActivity.ACTION_ERROR)) {
                    printError();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NEW_NEWS);
        intentFilter.addAction(ACTION_ERROR);
        registerReceiver(broadcastReceiver, intentFilter);
        loadNews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        loadNews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    private void printNews() {
        News news = Storage.getInstance(this).getLastSavedNews();
        String newsTitle;
        String newsText;
        Long newsDate;
        if (news == null) {
            newsTitle = "error";
            newsText = "error";
            newsDate = 0L;
        } else {
            newsTitle = news.getTitle();
            newsText = news.getBody();
            newsDate = news.getDate();
        }
        ((TextView) findViewById(R.id.news_title)).setText(newsTitle);
        ((TextView) findViewById(R.id.news_text)).setText(newsText);
        ((TextView) findViewById(R.id.news_date)).setText(getDate(newsDate));

    }

     private void printError() {
        ((TextView) findViewById(R.id.news_title)).setText("error");
        ((TextView) findViewById(R.id.news_text)).setText("error");
        ((TextView) findViewById(R.id.news_date)).setText(getDate(0L));
        }

    private void setUpdateInBg(boolean isUpdateInBgOn) {
        if (MainActivity.updateInBg != isUpdateInBgOn) {
            MainActivity.updateInBg = isUpdateInBgOn;
            Scheduler scheduler = Scheduler.getInstance();
            Intent intent = new Intent(MainActivity.this, NewsIntentService.class);
            if (updateInBg) {
                scheduler.schedule(this, intent, 60000);
            } else {
                scheduler.unschedule(this, intent);
            }
        }
    }

    private void loadNews() {
        Intent intent = new Intent(MainActivity.this, NewsIntentService.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (broadcastReceiver!= null) {
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "error";
        }
    }
}
