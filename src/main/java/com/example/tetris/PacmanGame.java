package com.example.tetris;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.text.Font;


import java.util.HashSet;
import java.util.Set;

public class PacmanGame extends Application {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 600;
    private static final int PLAYER_SIZE = 40;
    private static final int PELLET_SIZE = 10;
    private static final int ENEMY_SIZE = 40;
    private static final int ENEMY_SPEED = 2;

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private Walls walls;
    private Pellets pellets;
    private Enemies enemies;
    private Pacman pacman;
    private Game game;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pac-Man Game");
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new StackPane(canvas));
        primaryStage.setScene(scene);
        primaryStage.show();

        walls = new Walls(gc, WIDTH, HEIGHT, PLAYER_SIZE);
        walls.initializeMaze();

        pellets = new Pellets(gc, WIDTH, HEIGHT, PLAYER_SIZE, PELLET_SIZE);
        pellets.initializePellets(walls);

        game = new Game(gc); // Инициализируем игру перед врагами
        enemies = new Enemies(gc, PLAYER_SIZE, ENEMY_SIZE, ENEMY_SPEED);
        enemies.initializeEnemies(walls, game);

        pacman = initializePlayerPosition(gc);

        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };
        timer.start();
    }

    private Pacman initializePlayerPosition(GraphicsContext gc) {
        for (int i = 0; i < walls.getMaze().length; i++) {
            for (int j = 0; j < walls.getMaze()[i].length; j++) {
                if (!walls.getMaze()[i][j]) {
                    return new Pacman(gc, i * PLAYER_SIZE, j * PLAYER_SIZE, 5);
                }
            }
        }
        throw new IllegalStateException("Cannot initialize position for Pacman.");
    }

    private void update() {
        if (!game.isGameover()) { // Проверяем, что игра не окончена
            pacman.update(pressedKeys, walls);
            enemies.update(pacman, walls, game);
            pellets.checkCollision(pacman);
        }
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        walls.draw();
        pellets.draw();
        enemies.draw();
        pacman.draw();
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(20));
        gc.fillText("Score: " + pellets.counter(), 10, 20);


        if (game.isGameover()) { // Рисуем "Game Over", если игра завершена
            game.drawGameOver();
        }
    }

    // Добавьте этот метод в ваш класс PacmanGame
    public  void showRetryButton(GraphicsContext gc, Stage primaryStage) {
        // Создаем кнопку
        Button retryButton = new Button("Retry");
        retryButton.setStyle("-fx-font-size: 20px; -fx-background-color: red; -fx-text-fill: white;");

        // Расположение кнопки по центру
        retryButton.setLayoutX(WIDTH / 2 - 50); // Смещение кнопки на половину ширины
        retryButton.setLayoutY(HEIGHT / 2 + 40); // Расположение чуть ниже текста "Game Over"

        // Создаем StackPane для добавления кнопки поверх канвы
        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().add(retryButton);

        // Добавляем обработчик событий на кнопку
        retryButton.setOnAction(event -> {
            // Удаляем кнопку
            root.getChildren().remove(retryButton);

            // Перезапуск игры
            resetGame(gc, primaryStage);
        });
    }


    // Метод для сброса игры
    private void resetGame(GraphicsContext gc, Stage primaryStage) {
        pressedKeys.clear(); // Очищаем текущие нажатия клавиш
        walls.initializeMaze(); // Перегенерируем стены
        pellets.initializePellets(walls); // Перегенерируем пеллет
        enemies.initializeEnemies(walls, game); // Перегенерируем врагов
        pacman = initializePlayerPosition(gc); // Устанавливаем стартовую позицию игрока
        game = new Game(gc); // Сбрасываем состояние игры
    }

}

