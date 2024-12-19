package com.example.tetris;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Game {
    private boolean isGameover = false;
    private GraphicsContext gc;
    private PacmanGame pacmanGame;

    public Game(GraphicsContext gc) {
        this.gc = gc;
    }

    public void endGame() {
        System.out.println("Game over");
        if (!isGameover) { // Проверяем, что игра не завершена
            isGameover = true; // Устанавливаем флаг завершения игры
            System.out.println("Game over"); // Для отладки
            drawGameOver(); // Рисуем сообщение "Game Over"
        }
    }

    public boolean isGameover() {
        return isGameover;
    }

    public void drawGameOver() {
        double canvasWidth = 1000; // Ширина окна (замените на фактическую, если она отличается)
        double canvasHeight = 600; // Высота окна (замените на фактическую, если она отличается)

        gc.setFill(Color.BLACK); // Заливка фона
        gc.fillRect(0, 0, canvasWidth, canvasHeight); // Полностью очищаем холст

        String gameOverText = "Game Over";
        gc.setFill(Color.RED); // Цвет текста
        gc.setFont(javafx.scene.text.Font.font("Arial", 40)); // Устанавливаем шрифт

        // Примерная ширина текста (можно уточнить, используя FontMetrics)
        double textWidth = gameOverText.length() * 20; // Оценка ширины текста (~20 пикселей на символ для Arial 40)
        double textHeight = 40; // Высота шрифта совпадает с его размером

        // Центрирование текста
        double centerX = (canvasWidth - textWidth) / 2;
        double centerY = (canvasHeight - textHeight) / 2;

        gc.fillText(gameOverText, centerX, centerY);





    }


}
