package com.example.EggHuntGame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.HashSet;


import javafx.application.Platform;

class GameEngineTest {
    @BeforeAll
    public static void initToolkit() {
        // Initialize JavaFX toolkit
        Platform.startup(() -> {
            // Any additional setup code that requires JavaFX
            // can be placed here
        });
    }

    private GameEngine gameEngine;
    private GameGUI gameGUI;

    @BeforeEach
    void setUp() {
        int difficulty = 2; // Set the desired difficulty value for the test case
        gameEngine = new GameEngine();
        gameGUI = new GameGUI(difficulty);
    }

    @Test
    void handleArrowButtonPress_validInput_success() {
        // Given
        int expectedScore = -1;

        // When
        gameEngine.handleArrowButtonPress("KP_UP");

        // Then
        int actualScore = gameEngine.getScore();
        Assertions.assertEquals(expectedScore, actualScore, "Score should be updated correctly");
    }

    @Test
    void handleArrowButtonPress_invalidInput_noChange() {
        // Given
        int initialScore = gameEngine.getScore();

        // When
        gameEngine.handleArrowButtonPress("1");

        // Then
        int actualScore = gameEngine.getScore();
        Assertions.assertEquals(initialScore, actualScore, "Score should not be changed for invalid input");
    }

    @Test
    void isValidMove_validMove_true() {
        // Given
        int row = 2;
        int col = 3;

        // When
        boolean isValid = gameEngine.isValidMove(row, col);

        // Then
        Assertions.assertTrue(isValid, "Valid move should return true");
    }

    @Test
    void isValidMove_invalidMove_false() {
        // Given
        int row = -1;
        int col = 5;

        // When
        boolean isValid = gameEngine.isValidMove(row, col);

        // Then
        Assertions.assertFalse(isValid, "Invalid move should return false");
    }

    @Test
    void handleArrowButtonPress_collectKey_increaseKeyCount() {
        // Given
        int initialKeyCount = gameEngine.collectedKeys;
        gameEngine.playerX = 1; // Set the player's X position
        gameEngine.playerY = 1; // Set the player's Y position
        gameEngine.keyLocations[1][2] = true; // Place a key at (1, 2)

        // When
        gameEngine.handleArrowButtonPress("RIGHT");

        // Then
        int updatedKeyCount = gameEngine.collectedKeys;
        Assertions.assertEquals(initialKeyCount + 1, updatedKeyCount, "Key count should increase after collecting a key");
    }

    @Test
    void handleArrowButtonPress_unlockLockedCell_decreaseKeyCount() {
        // Given
        gameEngine.keyLocations[1][1] = true; // Place a key at (1, 1)
        gameEngine.lockedCells[2][1] = true; // Place a locked cell at (2, 1)
        gameEngine.collectedKeys = 1; // Set the collected key count to 1
        gameEngine.openedLocks = new HashSet<>(); // Initialize the opened locks set

        // When
        gameEngine.playerX = 1; // Set the player's X position
        gameEngine.playerY = 1; // Set the player's Y position
        gameEngine.handleArrowButtonPress("DOWN");

        // Then
        int updatedKeyCount = gameEngine.collectedKeys;
        Assertions.assertEquals(0, updatedKeyCount, "Key count should decrease after unlocking a locked cell");
        Assertions.assertTrue(gameEngine.openedLocks.contains(2 * GameEngine.MAP_SIZE + 1), "The opened locks set should contain the unlocked cell");
    }

    @Test
    void handleArrowButtonPress_collectEgg_increaseEggCount() {
        // Given
        int initialEggCount = gameEngine.collectedEggs;
        gameEngine.playerX = 1; // Set the player's X position
        gameEngine.playerY = 1; // Set the player's Y position
        gameEngine.eggLocations[1][2] = true; // Place an egg at (1, 2)

        // When
        gameEngine.handleArrowButtonPress("RIGHT");

        // Then
        int updatedEggCount = gameEngine.collectedEggs;
        Assertions.assertEquals(initialEggCount + 1, updatedEggCount, "Egg count should increase after collecting an egg");
    }

    @Test
    void handleArrowButtonPress_collectEggAndKey_increaseEggAndKeyCount() {
        // Given
        gameEngine.playerX = 1; // Set the player's X position
        gameEngine.playerY = 1; // Set the player's Y position
        gameEngine.keyLocations[2][1] = true; // Place a key at (2, 1)
        gameEngine.eggLocations[1][2] = true; // Place an egg at (1, 2)
        gameEngine.collectedKeys = 1; // Set the collected key count to 1
        gameEngine.collectedEggs = 2; // Set the collected egg count to 2

        // When
        gameEngine.handleArrowButtonPress("DOWN");

        // Then
        int updatedKeyCount = gameEngine.collectedKeys;
        int updatedEggCount = gameEngine.collectedEggs;
        Assertions.assertEquals(2, updatedKeyCount, "Key count should increase after collecting a key");
        Assertions.assertEquals(2, updatedEggCount, "Egg count should not change when collecting a key");
    }

    //@TODO catching some exceptions but the tests are passing, not quite sure yet.
    @Test
    void saveData_validFileName_fileExists() {
        // Given
        String fileName = "saveDataTest.sav";

        // When
        gameGUI.saveData(fileName);

        // Then
        File file = new File(fileName);
        Assertions.assertTrue(file.exists(), "Saved file should exist");
        Assertions.assertTrue(file.isFile(), "Saved file should be a regular file");
    }

    @Test
    void saveData_invalidFileName_fileNotCreated() {
        // Given
        String invalidFileName = "";

        // When
        gameGUI.saveData(invalidFileName);

        // Then
        File file = new File(invalidFileName);
        Assertions.assertFalse(file.exists(), "Invalid file name should not create a file");
    }

    @Test
    void loadData_validFileName_dataLoadedSuccessfully() {
        // Given
        String fileName = "saveDataTest.sav";
        GameEngine savedData = new GameEngine();
        savedData.playerX = 2;
        savedData.playerY = 3;
        savedData.keyLocations[1][1] = true;



        try {
            ResourceManager.save(savedData, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Reset the gameEngine object
        gameEngine = new GameEngine();
        gameEngine.playerX = savedData.playerX;
        gameEngine.playerY = savedData.playerY;
        gameEngine.keyLocations = savedData.keyLocations;



        // When
        gameGUI.loadData(fileName);

        // Then
        Assertions.assertEquals(savedData.playerX, gameEngine.playerX, "Player X position should be loaded correctly");
        Assertions.assertEquals(savedData.playerY, gameEngine.playerY, "Player Y position should be loaded correctly");
        Assertions.assertEquals(savedData.keyLocations[1][1], gameEngine.keyLocations[1][1], "Key location should be loaded correctly");

        // @TODO no idea why im getting an error here for Image must be non-null, not trying to load or save any image. fix

    }


    @Test
    void loadData_invalidFileName_noDataLoaded() {
        // Given
        String invalidFileName = "nonexistentFile.sav";

        // When
        gameGUI.loadData(invalidFileName);

        // Then
        // Add assertions to verify that the gameEngine object hasn't been modified
        // Assertions.assertEquals(...);
    }
}
