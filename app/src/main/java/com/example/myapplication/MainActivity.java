package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// git test
public class MainActivity extends AppCompatActivity {

    private Button btnRecord;
    private Button btnLive;
    private boolean isRecording = false;

    private boolean isLive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // если трансляции нет - то нельзя включать запись
        if (!isLive)
            return;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим кнопку
        btnRecord = findViewById(R.id.btnRecord);
        // Находим кнопку
        btnLive = findViewById(R.id.btnLive);

        // Устанавливаем обработчик нажатия на кнопку записи
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Переключаем состояние записи
                isRecording = !isRecording;

                // Изменяем текст на кнопке
                btnRecord.setText(isRecording ? "Остановить запись" : "Начать запись");

                // Выводим уведомление
                Toast.makeText(MainActivity.this,
                        isRecording ? "Запись начата" : "Запись остановлена",
                        Toast.LENGTH_SHORT).show();

                // Здесь можно вызвать метод для начала или остановки записи
                handleRecording(isRecording);
            }
        });

        // Устанавливаем обработчик нажатия на кнопку трансляции
        btnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Переключаем состояние записи
                isLive = !isLive;

                // Изменяем текст на кнопке
                btnLive.setText(isLive ? "Остановить трансляцию" : "Начать трансляцию");

                // Выводим уведомление
                Toast.makeText(MainActivity.this,
                        isLive ? "Трансляция начата" : "Трансляция остановлена",
                        Toast.LENGTH_SHORT).show();

                // Здесь можно вызвать метод для начала или остановки трансляции
                handleLive(isLive);
            }
        });
    }

    private void handleRecording(boolean startRecording) {
        // Логика начала/остановки записи
        if (startRecording) {
            // Код для начала записи
        } else {
            // Код для остановки записи
        }
    }

    private void handleLive(boolean startLive) {
        // Логика начала/остановки трансляции
        if (startLive) {
            // Код для начала трансляции
        } else {
            // Код для остановки трансляции
        }
    }
}