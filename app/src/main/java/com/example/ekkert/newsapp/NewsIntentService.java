package com.example.ekkert.newsapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.NewsLoader;
import ru.mail.weather.lib.Storage;

public class NewsIntentService extends IntentService {

    public NewsIntentService() {
        super("NewsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                String topic = Storage.getInstance(getApplicationContext()).loadCurrentTopic();
                News news = new NewsLoader().loadNews(topic);
                if (news != null) {
                    Storage.getInstance(getApplicationContext()).saveNews(news);
                    Intent resultIntent = new Intent(MainActivity.ACTION_NEW_NEWS);
                    sendBroadcast(resultIntent);
                } else {
                    Intent resultIntent = new Intent(MainActivity.ACTION_ERROR);
                    sendBroadcast(resultIntent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Intent resultIntent = new Intent(MainActivity.ACTION_ERROR);
                sendBroadcast(resultIntent);
            }
        }
    }
}