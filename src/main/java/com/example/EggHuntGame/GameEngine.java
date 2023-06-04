package com.example.EggHuntGame;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
public class GameEngine implements java.io.Serializable {

    // Game Variables
    public transient Image playerImage; // Declare the field as private and specify its type
    @Serial
    private static final long serialVersionUID = 1L;
    protected static final int TILE_SIZE = 40;
    protected static final int MAP_SIZE = 10;
    protected static final int NUM_KEYS = 5;
    protected static final int NUM_EGGS = 5;
    protected static final int MAX_STEPS = 100;
    protected int playerX;
    protected int playerY;
    protected boolean[][] keyLocations;
    protected boolean[][] lockedCells;
    protected boolean[][] eggLocations;
    protected boolean gameWon;
    protected Set<Integer> openedLocks;
    protected int collectedKeys;
    protected int collectedEggs;
    protected int movementCount = 0;
    protected Text movementCounterText;
    protected Text keyCounterText;
    protected Text eggCounterText;
    protected Text scoreText;
    protected SerializableRectangle[][] grid;
//    protected Image playerImage;
    protected int newX;
    protected int newY;
    protected String movementCounterTextData;
    protected String scoreTextData;




    // method that controls all user movement inside the 10x10 grid, takes input from arrow keys or arrow buttons in the GUI
    protected void handleArrowButtonPress(String direction) {
        if (gameWon || movementCount >= MAX_STEPS) {
            return;
        }

        int dx = 0;
        int dy = 0;

        switch (direction) {
            case "UP", "KP_UP", "up", "w" -> dy = -1;
            case "DOWN", "KP_DOWN", "down", "s" -> dy = 1;
            case "LEFT", "KP_LEFT", "left", "a" -> dx = -1;
            case "RIGHT", "KP_RIGHT", "right", "d" -> dx = 1;
            default -> {
                // Ignore other key events
                return;
            }
        }

        newX = playerX + dx;
        newY = playerY + dy;
        // move validation
        if (isValidMove(newX, newY)) {
            //collect key and remove specific key from grid
            if (keyLocations[newY][newX]) {
                keyLocations[newY][newX] = false;
                collectedKeys++;
                grid[newY][newX].setFill(Color.WHITE);
                keyCounterText.setText("Key Count: " + collectedKeys);
            }

            if (lockedCells[newY][newX] && !openedLocks.contains(newY * MAP_SIZE + newX)) {
                //collect lock and remove specific lock from grid
                if (collectedKeys > 0) {
                    collectedKeys--;
                    openedLocks.add(newY * MAP_SIZE + newX);
                    grid[newY][newX].setFill(Color.WHITE);
                    keyCounterText.setText("Key Count: " + collectedKeys);
                } else {
                    // Player doesn't have a key to unlock the locked cell
                    return;
                }
            }

            if
            //collect egg and remove specific egg from grid
            (eggLocations[newY][newX]) {
                eggLocations[newY][newX] = false;
                collectedEggs++;
                grid[newY][newX].setFill(Color.WHITE);
                eggCounterText.setText("Egg Count: " + collectedEggs);
            }
            //replace previous tile with blank white space
            grid[playerY][playerX].setFill(Color.WHITE);
            playerX = newX;
            playerY = newY;

            // Update the player's position on the grid with the player image
            grid[playerY][playerX].setFill(new ImagePattern(playerImage));

            //track user movement, game over at 100 steps, score displayed at the end. 100 - user steps = score
            movementCount++;
            System.out.println(movementCount);
            movementCounterText.setText("Steps Taken: " + movementCount + "/" + MAX_STEPS);


            // end of game conditions
            if (playerX == MAP_SIZE - 1 && playerY == 0 && collectedEggs == NUM_EGGS) {
                gameWon = true;
                grid[playerY][playerX].setFill(Color.GREEN);
                int score = MAX_STEPS - movementCount;
                scoreText.setText("Well done! your Score is: " + score);
            } else if (movementCount >= MAX_STEPS) {
                scoreText.setText("Game Over, score is -1");
            }
        }
    }

    public int getScore() {
        if (gameWon) {
            return MAX_STEPS - movementCount;
        } else {
            return -1; // Return -1 if the game is not won
        }
    }


    protected boolean isValidMove(int x, int y) {
        return x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE;
    }
    private void loadPlayerImage() {
        try {
            playerImage = new Image("player_icon.png");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception or log an error message
        }
    }
    public GameEngine() {
        // Initialize arrays
        keyLocations = new boolean[MAP_SIZE][MAP_SIZE];
        lockedCells = new boolean[MAP_SIZE][MAP_SIZE];
        eggLocations = new boolean[MAP_SIZE][MAP_SIZE];
        movementCounterText = new SerializableText();
        eggCounterText = new SerializableText();
        openedLocks = new HashSet<>();
        keyCounterText = new SerializableText();
        scoreText = new SerializableText();
        // Initialize the grid
        grid = new SerializableRectangle[MAP_SIZE][MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                grid[i][j] = new SerializableRectangle(TILE_SIZE, TILE_SIZE);
            }
        }
        // Load the player image
        loadPlayerImage();
    }


} // end of class
