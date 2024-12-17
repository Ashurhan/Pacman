package com.example.tetris;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Game {
    private boolean isGameover = false;
    private GraphicsContext gc;

    public Game(GraphicsContext gc) {
        this.gc = gc;
    }

    public void endGame() {
        if (!isGameover) {
            isGameover = true;
            System.out.println("Game over");
            drawGameOver();
        }
    }

    public boolean isGameover() {
        return isGameover;
    }

    // Метод для рисования сообщения "Game over" на экране
    public void drawGameOver() {
        gc.setFill(Color.RED);
        gc.setFont(javafx.scene.text.Font.font(40));  // Устанавливаем размер шрифта
        gc.fillText("Game Over", 200, 200);  // Рисуем текст "Game Over" в позиции (200, 200)
    }
}
