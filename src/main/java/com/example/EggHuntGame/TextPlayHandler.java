package com.example.EggHuntGame;

import javafx.scene.text.Text;
import java.io.Serializable;
import java.util.*;

public class TextPlayHandler extends GameEngine {

    private SerializableRectangle[][] grid; // Update the type of grid

    private final int d;
    private int collectedEggs;
    private int collectedKeys;

    public TextPlayHandler(int d) {
        super();
        this.d = d;
        grid = new SerializableRectangle[MAP_SIZE][MAP_SIZE];
    }

    public void loadTextForPlay() {
        initializeGrid();
        randomlyPlaceItems();
        drawGrid();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your move (w/a/s/d): ");

            String input = scanner.nextLine().toLowerCase();
            System.out.println("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n ");
            if (input.isEmpty()) {
                continue;
            }
            String move = input.substring(0, Math.min(5, input.length()));
            if (grid == null) {
                initializeGrid();
            }

            TextInputHandle(move);

            drawGrid();

            if (gameWon) {
                int score = 100 - movementCount;
                System.out.println("Congratulations! You won the game! Your score is: " + score);
                break;
            } else if (movementCount >= MAX_STEPS) {
                System.out.println("Game Over! Your score is -1");
                break;
            } else if (MAX_STEPS - movementCount <= 10) {
                System.out.println("Only " + (MAX_STEPS - movementCount) + " steps left!");
            }
        }
    }


    protected void TextInputHandle(String direction) {
        if (gameWon || movementCount >= MAX_STEPS) {
            return;
        }

        int dx = 0;
        int dy = 0;

        switch (direction) {
            case "UP", "KP_UP", "up", "w" -> {
                dy = -1;
                movementCount++;
            }
            case "DOWN", "KP_DOWN", "down", "s" -> {
                dy = 1;
                movementCount++;
            }
            case "LEFT", "KP_LEFT", "left", "a" -> {
                dx = -1;
                movementCount++;
            }
            case "RIGHT", "KP_RIGHT", "right", "d" -> {
                dx = 1;
                movementCount++;
            }
            case "eggs", "egg" -> System.out.println("Collected Eggs: " + collectedEggs);
            case "keys", "key" -> System.out.println("Collected Keys: " + collectedKeys);
            case "steps", "moves", "step" -> System.out.println("Movement Count: " + movementCount);
            case "help" -> System.out.println("""
                     To move you have to type w s a d, or, up down left right\s
                     To see how many Eggs or Keys you have, type 'eggs' and 'keys'.\s
                     To see how many steps you have taken, type 'steps' or 'moves' or 'step'. Total number of steps you can take is 100.\s
                     Have fun!""");
            case "save" -> saveTextData("1.save");
            case "load" -> loadTextData("1.save");
            default -> {
                // Ignore other key events
                return;
            }
        }

        int newX = playerX + dx;
        int newY = playerY + dy;

        // move validation
        if (isValidMove(newX, newY)) {
            // Collect key and remove specific key from grid
            if (keyLocations[newY][newX]) {
                keyLocations[newY][newX] = false;
                collectedKeys++;
                grid[newY][newX].setValue(' ');
                keyCounterText.setText("Key Count: " + collectedKeys);
            }

            if (lockedCells[newY][newX] && !openedLocks.contains(newY * MAP_SIZE + newX)) {
                // Collect lock and remove specific lock from grid
                if (collectedKeys > 0) {
                    collectedKeys--;
                    openedLocks.add(newY * MAP_SIZE + newX);
                    grid[newY][newX].setValue(' ');
                    keyCounterText.setText("Key Count: " + collectedKeys);
                } else {
                    // Player doesn't have a key to unlock the locked cell
                    return;
                }
            }

            if (eggLocations[newY][newX]) {
                // Collect egg and remove specific egg from grid
                eggLocations[newY][newX] = false;
                collectedEggs++;
                grid[newY][newX].setValue(' ');
                eggCounterText.setText("Egg Count: " + collectedEggs);
            }

            grid[playerY][playerX].setValue(' ');
            playerX = newX;
            playerY = newY;

            grid[playerY][playerX].setValue('P');

//            movementCount++;
            movementCounterText.setText("Steps Taken: " + movementCount + "/" + MAX_STEPS);



            // End of game conditions
            if (playerX == MAP_SIZE - 1 && playerY == 0 && collectedEggs == NUM_EGGS) {
                gameWon = true;
                grid[playerY][playerX].setValue('G');
            } else if (movementCount >= MAX_STEPS) {
                scoreText.setText("Game Over, score is -1");
            }
        }
    }


    private void initializeGrid() {
        grid = new SerializableRectangle[MAP_SIZE][MAP_SIZE]; // Initialize the grid

        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                grid[i][j] = new SerializableRectangle(TILE_SIZE, TILE_SIZE); // Initialize each grid element
                grid[i][j].setX(i);
                grid[i][j].setY(j);
            }
        }

        playerX = 0; // Set the player's starting X position
        playerY = MAP_SIZE - 1; // Set the player's starting Y position
        grid[playerY][playerX].setValue('P'); // Update the grid with the player's starting position

        // Initialize movementCounterText
        movementCounterText = new Text("Steps Taken: 0/" + MAX_STEPS);
        // Initialize other text objects (keyCounterText, eggCounterText, scoreText)
        keyCounterText = new Text("Key Count: 0");
        eggCounterText = new Text("Egg Count: 0");
        scoreText = new Text();


        // Initialize other necessary variables and objects
        openedLocks = new HashSet<>();
    }

    private void randomlyPlaceItems() {
        keyLocations = new boolean[MAP_SIZE][MAP_SIZE]; // Initialize keyLocations array
        eggLocations = new boolean[MAP_SIZE][MAP_SIZE]; // Initialize eggLocations array
        lockedCells = new boolean[MAP_SIZE][MAP_SIZE]; // Initialize lockedCells array

        Random random = new Random();

        // Generate a list of valid positions for items
        List<Integer> validPositions = new ArrayList<>();
        for (int i = 0; i < MAP_SIZE * MAP_SIZE; i++) {
            int x = i % MAP_SIZE;
            int y = i / MAP_SIZE;
            if (Math.abs(x - playerX) > 1 || Math.abs(y - playerY) > 1) {
                validPositions.add(i);
            }
        }

        int keysPlaced = 0;
        while (keysPlaced < NUM_KEYS) {
            if (validPositions.isEmpty()) {
                break; // No more valid positions available, exit the loop
            }

            int index = random.nextInt(validPositions.size());
            int position = validPositions.remove(index);
            int x = position % MAP_SIZE;
            int y = position / MAP_SIZE;

            keyLocations[y][x] = true;
            keysPlaced++;
        }

        int eggsPlaced = 0;
        while (eggsPlaced < NUM_EGGS) {
            if (validPositions.isEmpty()) {
                break; // No more valid positions available, exit the loop
            }

            int index = random.nextInt(validPositions.size());
            int position = validPositions.remove(index);
            int x = position % MAP_SIZE;
            int y = position / MAP_SIZE;

            eggLocations[y][x] = true;
            eggsPlaced++;
        }

        int locksPlaced = 0;
        while (locksPlaced < d) {
            if (validPositions.isEmpty()) {
                break; // No more valid positions available, exit the loop
            }

            int index = random.nextInt(validPositions.size());
            int position = validPositions.remove(index);
            int x = position % MAP_SIZE;
            int y = position / MAP_SIZE;

            lockedCells[y][x] = true;
            locksPlaced++;
        }
    }


    private void drawGrid() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (i == playerY && j == playerX) {
                    grid[i][j].setValue('P');
                } else if (keyLocations[i][j]) {
                    grid[i][j].setValue('K');
                } else if (lockedCells[i][j]) {
                    grid[i][j].setValue('L');
                } else if (eggLocations[i][j]) {
                    grid[i][j].setValue('E');
                } else {
                    grid[i][j].setValue('.');
                }
            }
        }

        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                System.out.print(grid[i][j].getValue() + " ");
            }
            System.out.println();
        }
    }

    public static void playText() {
        Optional<String> result = GameGUI.InputValidator.showInputDialog("Enter Value", "Enter the number of locks (0-10):");
        result.ifPresent(d -> {
            if (GameGUI.InputValidator.isValidInput(d)) {
                int value = Integer.parseInt(d);
                TextPlayHandler handler = new TextPlayHandler(value);
                handler.loadTextForPlay();
            } else {
                GameGUI.InputValidator.showInvalidInputAlert();
            }
        });
    }

    public void saveTextData(String fileName) {
        try {
            TextGameData data = new TextGameData();
            data.playerX = playerX;
            data.playerY = playerY;
            data.keyLocations = keyLocations;
            data.lockedCells = lockedCells;
            data.eggLocations = eggLocations;
            data.openedLocks = openedLocks;
            data.collectedKeys = collectedKeys;
            data.collectedEggs = collectedEggs;
            data.movementCount = movementCount;
            data.gameWon = gameWon;
            data.movementCounterTextData = movementCounterText.getText();
            data.scoreTextData = scoreText.getText();

            ResourceManager.save(data, fileName);
        } catch (Exception e) {
            System.out.println("Couldn't save: " + e.getMessage());
        }
    }

    public void loadTextData(String fileName) {
        try {
            TextGameData loadedData = (TextGameData) ResourceManager.load(fileName);
            playerX = loadedData.playerX;
            playerY = loadedData.playerY;
            keyLocations = loadedData.keyLocations;
            lockedCells = loadedData.lockedCells;
            eggLocations = loadedData.eggLocations;
            openedLocks = loadedData.openedLocks;
            collectedKeys = loadedData.collectedKeys;
            collectedEggs = loadedData.collectedEggs;
            movementCount = loadedData.movementCount;
            gameWon = loadedData.gameWon;
            movementCounterText.setText(loadedData.movementCounterTextData);
            scoreText.setText(loadedData.scoreTextData);

            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    grid[y][x].setText("");
                }
            }

            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    if (keyLocations[y][x]) {
                        grid[y][x].setText("K");
                    }
                }
            }

            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    if (lockedCells[y][x]) {
                        grid[y][x].setText("L");
                    }
                }
            }

            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    if (eggLocations[y][x]) {
                        grid[y][x].setText("E");
                    }
                }
            }

            grid[playerY][playerX].setText("P");

            // Refresh the grid to update the displayed text
            //refreshGrid();
        } catch (Exception e) {
            System.out.println("Couldn't load save data: " + e.getMessage());
        }
    }

    private static class TextGameData implements Serializable {
        int playerX;
        int playerY;
        boolean[][] keyLocations;
        boolean[][] lockedCells;
        boolean[][] eggLocations;
        Set<Integer> openedLocks;
        int collectedKeys;
        int collectedEggs;
        int movementCount;
        boolean gameWon;
        String movementCounterTextData;
        String scoreTextData;
    }

} // end of class
