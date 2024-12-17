    package com.example.tetris;

    import java.util.Random;
    import javafx.scene.canvas.GraphicsContext;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.ArcType;

    import java.util.HashSet;
    import java.util.Set;
    public class Enemies {
        record Enemy(int x, int y) {}

        private final GraphicsContext gc;
        private final int playerSize;
        private final int enemySize;
        private final int enemySpeed;
        private final Set<Enemy> enemies = new HashSet<>();

        public Enemies(GraphicsContext gc, int playerSize, int enemySize, int enemySpeed) {
            this.gc = gc;
            this.playerSize = playerSize;
            this.enemySize = enemySize;
            this.enemySpeed = enemySpeed;
        }

        public void initializeEnemies(Walls walls) {
            Random random = new Random();  // Создаем объект для генерации случайных чисел
            int enemiesAdded = 0;
            int maxEnemies = 4;

            while (enemiesAdded < maxEnemies) {
                // Генерация случайных координат для врага
                int i = random.nextInt(walls.getMaze().length - 2) + 1;  // Генерация случайного индекса для строки от 1 до walls.getMaze().length - 2
                int j = random.nextInt(walls.getMaze()[i].length - 2) + 1; // Генерация случайного индекса для столбца от 1 до walls.getMaze()[i].length - 2

                // Проверка, что в этом месте нет стены и враг не стоит здесь
                if (!walls.getMaze()[i][j] && !isEnemyCollision(i * playerSize, j * playerSize)) {
                    enemies.add(new Enemy(i * playerSize, j * playerSize));  // Добавляем врага на карту
                    enemiesAdded++;  // Увеличиваем количество добавленных врагов
                }
            }
        }

        public void update(Pacman pacman, Walls walls,Game game) {
            Set<Enemy> updatedEnemies = new HashSet<>();

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
                        newX = (int) nextX;
                        moved = true;
                    }
                } else {
                    double nextY = enemy.y() + Math.signum(diffY) * enemySpeed;
                    if (!isCollision(enemy.x(), nextY, walls) && !isEnemyCollision(newX, (int) nextY)) {
                        newY = (int) nextY;
                        moved = true;
                    }
                }

                if (!moved || isEnemyCollision(newX, newY)) {
                    if (Math.abs(diffX) <= Math.abs(diffY)) {
                        newX = enemy.x() - (int) Math.signum(diffX) * enemySpeed;
                    } else {
                        newY = enemy.y() - (int) Math.signum(diffY) * enemySpeed;
                    }
                }

                updatedEnemies.add(new Enemy(newX, newY));
            }
            if(isCollisionWithPacman(pacman)){
                System.out.println("Pacman collided with enemies");
                pacman.stop();
                game.endGame();
            }

            enemies.clear();
            enemies.addAll(updatedEnemies);
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
                    return true;
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
            if (gridXStart < 0 || gridXEnd >= maze.length
                    || gridYStart < 0 || gridYEnd >= maze[0].length) {
                return true;
            }
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
    }