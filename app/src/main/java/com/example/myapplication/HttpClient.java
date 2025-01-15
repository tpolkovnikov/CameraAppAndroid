package com.example.myapplication;

import android.content.Context;
import okhttp3.OkHttpClient;

public class HttpClient {

    public static OkHttpClient getClient(Context context) {
        // Создаём OkHttpClient без настроек SSL для HTTP
        return new OkHttpClient.Builder()
                .build();
    }
}
