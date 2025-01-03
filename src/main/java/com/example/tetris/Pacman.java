package com.example.tetris;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.Set;

public class Pacman {
    private static final int PLAYER_SIZE = 40;
    private final GraphicsContext gc;
    private double playerX;
    private double playerY;
    private final double playerSpeed ;
    private double mouthAngle = 0;
    private boolean mouthOpening = true;
    private double mouthDirection = 90;
    private boolean ismoving=true;

    public Pacman(GraphicsContext gc,double playerX, double playerY,int playerSpeed) {
        this.gc = gc;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerSpeed = playerSpeed;
    }
    public void draw() {
        gc.setFill(Color.YELLOW);
        gc.fillArc(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE,
                mouthDirection + mouthAngle,
                360 - 2 * mouthAngle,
                ArcType.ROUND);
    }

    public void update(Set<KeyCode> pressedKeys, Walls walls) {
        if(ismoving){
            if (pressedKeys.contains(KeyCode.LEFT)) {
                if (!isCollision(playerX - playerSpeed, playerY, walls)) {
                    playerX -= playerSpeed;
                    mouthDirection = 180;
                }
            }
            if (pressedKeys.contains(KeyCode.RIGHT)) {
                if (!isCollision(playerX + playerSpeed, playerY, walls)) {
                    playerX += playerSpeed;
                    mouthDirection = 0;
                }
            }
            if (pressedKeys.contains(KeyCode.UP)) {
                if (!isCollision(playerX, playerY - playerSpeed, walls)) {
                    playerY -= playerSpeed;
                    mouthDirection = 90;
                }
            }
            if (pressedKeys.contains(KeyCode.DOWN)) {
                if (!isCollision(playerX, playerY + playerSpeed, walls)) {
                    playerY += playerSpeed;
                    mouthDirection = 270;
                }
            }

            if (mouthOpening) {
                mouthAngle += 2;
                if (mouthAngle >= 45) {
                    mouthOpening = false;
                }
            } else {
                mouthAngle -= 2;
                if (mouthAngle <= 10) {
                    mouthOpening = true;
                }
            }
        }

    }

    private boolean isCollision(double x, double y, Walls walls) {
        boolean[][] maze = walls.getMaze();
        int gridXStart = (int) (x / PLAYER_SIZE);
        int gridYStart = (int) (y / PLAYER_SIZE);
        int gridXEnd = (int) ((x + PLAYER_SIZE - 1) / PLAYER_SIZE);
        int gridYEnd = (int) ((y + PLAYER_SIZE - 1) / PLAYER_SIZE);

        if (gridXStart < 0 || gridXEnd >= maze.length
                || gridYStart < 0 || gridYEnd >= maze[0].length) {
            return true;
        }
        return maze[gridXStart][gridYStart] || maze[gridXEnd][gridYStart]
                || maze[gridXStart][gridYEnd] || maze[gridXEnd][gridYEnd];
    }

    public double getPlayerX() {
        return playerX;
    }

    public double getPlayerY() {
        return playerY;
    }
    public void stop(){
        ismoving=false;
    }
}

