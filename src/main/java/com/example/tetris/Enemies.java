    package com.example.tetris;

    import java.util.Random;
    import javafx.scene.canvas.GraphicsContext;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.ArcType;
    import java.util.Random;
    import javafx.scene.canvas.GraphicsContext;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.ArcType;
    import java.util.HashSet;
    import java.util.Random;
    import java.util.Set;
    import javafx.scene.canvas.GraphicsContext;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.ArcType;

    public class Enemies {
        record Enemy(int x, int y) {}

        private final GraphicsContext gc;
        private final int playerSize;
        private final int enemySize;
        private int enemySpeed;
        private final Set<Enemy> enemies = new HashSet<>();
        private Game game;
        private long lastUpdateTime = 0; // Последнее время обновления врагов
        private static final long ENEMY_MOVE_INTERVAL = 1; // Интервал движения врагов в наносекундах (100 мс)

        public Enemies(GraphicsContext gc, int playerSize, int enemySize, int initialEnemySpeed) {
            this.gc = gc;
            this.playerSize = playerSize;
            this.enemySize = enemySize;
            this.enemySpeed = initialEnemySpeed; // Начальная скорость врагов
        }

        public void initializeEnemies(Walls walls, Game game) {
            Random random = new Random();  // Создаем объект для генерации случайных чисел
            int enemiesAdded = 0;
            int maxEnemies = 4;

            while (enemiesAdded < maxEnemies) {
                // Генерация случайных координат для врага
                int i = random.nextInt(walls.getMaze().length - 2) + 1;
                int j = random.nextInt(walls.getMaze()[i].length - 2) + 1;

                // Проверка, что в этом месте нет стены и враг не стоит здесь
                if (!walls.getMaze()[i][j] && !isEnemyCollision(i * playerSize, j * playerSize)) {
                    enemies.add(new Enemy(i * playerSize, j * playerSize));  // Добавляем врага на карту
                    enemiesAdded++;  // Увеличиваем количество добавленных врагов
                }
            }
        }

        public void update(Pacman pacman, Walls walls, Game game) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastUpdateTime;

            // Если прошло меньше времени, чем интервал движения врагов, не обновляем их
            if (elapsedTime < ENEMY_MOVE_INTERVAL) {
                return;
            }

            lastUpdateTime = currentTime;

            Set<Enemy> updatedEnemies = new HashSet<>();  // Множество для обновленных врагов

            for (var enemy : enemies) {
                double diffX = pacman.getPlayerX() - enemy.x();
                double diffY = pacman.getPlayerY() - enemy.y();

                int newX = enemy.x();
                int newY = enemy.y();

                boolean moved = false;

                // Движение врага по ближайшей оси (X или Y)
                if (Math.abs(diffX) <= Math.abs(diffY)) {
                    double nextX = enemy.x() + Math.signum(diffX) * enemySpeed;
                    if (!isCollision(nextX, enemy.y(), walls) && !isEnemyCollision((int) nextX, newY)) {
                        newX = (int) nextX;  // Обновляем X
                        moved = true;
                    }
                } else {
                    double nextY = enemy.y() + Math.signum(diffY) * enemySpeed;
                    if (!isCollision(enemy.x(), nextY, walls) && !isEnemyCollision(newX, (int) nextY)) {
                        newY = (int) nextY;  // Обновляем Y
                        moved = true;
                    }
                }

                // Если враг не мог двигаться, пытаемся двигаться в другую сторону
                if (!moved || isEnemyCollision(newX, newY)) {
                    if (Math.abs(diffX) <= Math.abs(diffY)) {
                        newX = enemy.x() - (int) Math.signum(diffX) * enemySpeed;  // Пробуем двигаться в обратную сторону по X
                    } else {
                        newY = enemy.y() - (int) Math.signum(diffY) * enemySpeed;  // Пробуем двигаться в обратную сторону по Y
                    }
                }

                updatedEnemies.add(new Enemy(newX, newY));  // Добавляем обновленного врага
            }

            // Проверка столкновения с Pacman
            if (isCollisionWithPacman(pacman) && !game.isGameover()) {
                pacman.stop();
                game.endGame();  // Если столкновение с Pacman, завершаем игру
            }

            enemies.clear();  // Очищаем старых врагов
            enemies.addAll(updatedEnemies);  // Добавляем обновленных врагов
        }

        public boolean isCollisionWithPacman(Pacman pacman) {
            for (Enemy enemy : enemies) {
                double pacmanX = pacman.getPlayerX();
                double pacmanY = pacman.getPlayerY();
                double enemyX = enemy.x();
                double enemyY = enemy.y();

                double distance = Math.sqrt((pacmanX - enemyX) * (pacmanX - enemyX) + (pacmanY - enemyY) * (pacmanY - enemyY));

                if (distance < playerSize) {
                    return true;
                }
            }
            return false;
        }

        private boolean isEnemyCollision(int x, int y) {
            for (Enemy other : enemies) {
                if (other.x() == x && other.y() == y) {
                    return true;  // Если враг находится в той же клетке, возвращаем true
                }
            }
            return false;
        }

        private boolean isCollision(double x, double y, Walls walls) {
            boolean[][] maze = walls.getMaze();
            int gridXStart = (int) (x / playerSize);
            int gridYStart = (int) (y / playerSize);
            int gridXEnd = (int) ((x + enemySize - 1) / playerSize);
            int gridYEnd = (int) ((y + enemySize - 1) / playerSize);

            // Проверяем, не выходит ли враг за пределы карты
            if (gridXStart < 0 || gridXEnd >= maze.length
                    || gridYStart < 0 || gridYEnd >= maze[0].length) {
                return true;
            }

            // Проверка на столкновение с стенами
            return maze[gridXStart][gridYStart]
                    || maze[gridXEnd][gridYStart]
                    || maze[gridXStart][gridYEnd]
                    || maze[gridXEnd][gridYEnd];
        }

        public void draw() {
            gc.setFill(Color.RED);
            for (var enemy : enemies) {
                gc.fillArc(enemy.x(), enemy.y(), enemySize, enemySize, 0, 180, ArcType.ROUND);
                gc.fillRect(enemy.x(), enemy.y() + enemySize / 2, enemySize, enemySize / 2);
                gc.setFill(Color.WHITE);
                gc.fillOval(enemy.x() + enemySize * 0.2,
                        enemy.y() + enemySize * 0.2, enemySize * 0.2, enemySize * 0.2);
                gc.fillOval(enemy.x() + enemySize * 0.6,
                        enemy.y() + enemySize * 0.2, enemySize * 0.2, enemySize * 0.2);
                gc.setFill(Color.RED);
            }
        }

        // Метод для увеличения скорости врагов со временем
        public void increaseSpeed(int increment) {
            enemySpeed += increment;
        }
    }
