package com.example.proc4;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public class MainActivity extends AppCompatActivity {
    private Button[][] buttons = new Button[3][3];
    private boolean playerTurn = true; // true = X, false = O
    private boolean gameActive = true;

    // Переменные для статистики
    private int xWins = 0;
    private int oWins = 0;
    private int draws = 0;

    private TextView xWinsText, oWinsText, drawsText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("game_prefs", MODE_PRIVATE);

        // Привязываем текстовые поля для отображения статистики
        xWinsText = findViewById(R.id.xWinsText);
        oWinsText = findViewById(R.id.oWinsText);
        drawsText = findViewById(R.id.drawsText);

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        Button resetButton = findViewById(R.id.resetButton);
        Button themeButton = findViewById(R.id.themeButton);

        // Привязываем кнопки к игровому полю
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(v -> onButtonClick((Button) v));
            }
        }

        // Устанавливаем слушатели для кнопок
        resetButton.setOnClickListener(v -> resetGame());
        themeButton.setOnClickListener(v -> switchTheme());

        // Загружаем тему и статистику
        loadTheme();
        loadStatistics();
        setupGame();
    }

    private void onButtonClick(Button button) {
        if (!gameActive || !button.getText().toString().isEmpty()) return;

        // Устанавливаем символ игрока
        button.setText(playerTurn ? "X" : "O");

        // Проверяем победителя
        if (checkWinner()) {
            gameActive = false;
            updateStatistics(button.getText().toString());
            Toast.makeText(this, "Победил " + button.getText(), Toast.LENGTH_SHORT).show();
        } else if (isBoardFull()) {
            gameActive = false;
            updateStatistics("draw");
            Toast.makeText(this, "Ничья!", Toast.LENGTH_SHORT).show();
        } else {
            playerTurn = !playerTurn; // Сменить игрока
        }
    }

    private boolean checkWinner() {
        String[][] field = new String[3][3];
        // Заполняем массив символами из кнопок
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        // Проверяем строки и столбцы
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].isEmpty())
                return true;
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].isEmpty())
                return true;
        }
        // Проверяем диагонали
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].isEmpty())
            return true;
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].isEmpty())
            return true;
        return false;
    }

    private boolean isBoardFull() {
        for (Button[] row : buttons) {
            for (Button button : row) {
                if (button.getText().toString().isEmpty()) return false;
            }
        }
        return true; // Если пустых кнопок нет, то доска полная
    }

    private void updateStatistics(String winner) {
        // Обновляем статистику в зависимости от победителя
        if (winner.equals("X")) {
            xWins++;
        } else if (winner.equals("O")) {
            oWins++;
        } else {
            draws++;
        }
        saveStatistics(); // Сохраняем статистику
        displayStatistics(); // Отображаем обновлённую статистику
    }

    private void resetGame() {
        setupGame(); // Сбросить игру
    }

    private void switchTheme() {
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        preferences.edit().putBoolean("dark_mode", !isDarkMode).apply();
    }

    private void loadTheme() {
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void loadStatistics() {
        // Загружаем статистику из SharedPreferences
        xWins = preferences.getInt("x_wins", 0);
        oWins = preferences.getInt("o_wins", 0);
        draws = preferences.getInt("draws", 0);
        displayStatistics(); // Отображаем загруженную статистику
    }

    private void saveStatistics() {
        // Сохраняем статистику в SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("x_wins", xWins);
        editor.putInt("o_wins", oWins);
        editor.putInt("draws", draws);
        editor.apply();
    }

    private void displayStatistics() {
        // Обновляем текстовые поля статистики
        xWinsText.setText("Победы X: " + xWins);
        oWinsText.setText("Победы O: " + oWins);
        drawsText.setText("Ничьи: " + draws);
    }

    private void setupGame() {
        // Сбрасываем игровое поле
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        gameActive = true; // Игра активна
        playerTurn = true; // Начинает игрок X
    }
}