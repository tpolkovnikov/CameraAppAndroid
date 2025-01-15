package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://192.168.1.103:5000"; // Ваш сервер
    private OkHttpClient httpClient;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private String authToken = null; // JWT токен для запросов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = new OkHttpClient();
        playerView = findViewById(R.id.playerView); // Получаем ссылку на PlayerView из разметки
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Button startBroadcastButton = findViewById(R.id.start_broadcast_button);
        Button stopBroadcastButton = findViewById(R.id.stop_broadcast_button);
        Button startRecordingButton = findViewById(R.id.start_recording_button);
        Button stopRecordingButton = findViewById(R.id.stop_recording_button);

        startBroadcastButton.setOnClickListener(v -> sendPostRequest("/start_broadcast"));
        stopBroadcastButton.setOnClickListener(v -> sendPostRequest("/stop_broadcast"));
        startRecordingButton.setOnClickListener(v -> sendPostRequest("/start_recording"));
        stopRecordingButton.setOnClickListener(v -> sendPostRequest("/stop_recording"));

        // Запуск потока видео с сервера
        startVideoStream();

        // Авторизация
        loginUser("your_username", "your_password");
    }

    private void loginUser(String username, String password) {
        // Отправка POST запроса для получения токена
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(SERVER_URL + "/login")
                .post(RequestBody.create(json.toString(), okhttp3.MediaType.parse("application/json")))
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        authToken = jsonResponse.getString("token");
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void sendPostRequest(String path) {
        // Использование токена в заголовке для авторизации
        Request request = new Request.Builder()
                .url(SERVER_URL + path)
                .addHeader("x-access-token", authToken) // Добавляем токен в заголовок
                .post(okhttp3.RequestBody.create(null, new byte[0])) // Пустое тело запроса
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String message = response.body().string();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("ServerResponse", "Request failed: " + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Request failed: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void startVideoStream() {
        // Сетевой поток для видео
        Uri uri = Uri.parse(SERVER_URL + "/video_feed");
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "user-agent");
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }
}
