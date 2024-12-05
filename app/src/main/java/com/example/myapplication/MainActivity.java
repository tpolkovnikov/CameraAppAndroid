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
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим кнопку
        btnRecord = findViewById(R.id.btnRecord);

        // Устанавливаем обработчик нажатия
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
    }

    private void handleRecording(boolean startRecording) {
        // Логика начала/остановки записи
        if (startRecording) {
            // Код для начала записи
        } else {
            // Код для остановки записи
        }
    }
}