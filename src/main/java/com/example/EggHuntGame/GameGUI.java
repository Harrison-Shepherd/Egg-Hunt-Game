package com.example.EggHuntGame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class GameGUI extends GameEngine {
    private final int d;
    private Image keyImage;
    private Image lockImage;
    private Image eggImage;

    public GameGUI(int d) {
        super();
        this.d = d;
    }

    public static void MenuGUI(Stage stage) {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Button playGuiButton = new Button("Play GUI");
        // calls playGUI() which asks for d and starts game
        playGuiButton.setOnAction(event -> GuiPlayHandler.playGui());

        Button playTextButton = new Button("Play Text");
        // calls playText() which asks for d and starts game
        playTextButton.setOnAction(event -> TextPlayHandler.playText());

        Button helpButton = new Button("Help");
        // calls showHelpPopup() and shows instructions on how to play
        helpButton.setOnAction(event -> help.showHelpPopup());

        root.getChildren().addAll(playGuiButton, playTextButton, helpButton);
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Welcome to the Egg Hunt Game!");
        stage.setScene(scene);
        stage.show(); // Add this line to display the stage with the scene
    }


    public void constructGui(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        grid = new Rectangle[MAP_SIZE][MAP_SIZE];
        keyLocations = new boolean[MAP_SIZE][MAP_SIZE];
        lockedCells = new boolean[MAP_SIZE][MAP_SIZE];
        eggLocations = new boolean[MAP_SIZE][MAP_SIZE];
        openedChests = new HashSet<>();
        collectedKeys = 0;
        collectedEggs = 0;
        movementCount = 0;

        initializeGame(d);

        // Load the PNG images
        this.keyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/key_icon.png")));
        this.lockImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lock_icon.png")));
        this.eggImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/egg_icon.png")));
        playerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/player_icon.png")));

        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setStroke(Color.BLACK);

                if (keyLocations[y][x]) {
                    tile.setFill(new ImagePattern(keyImage));
                } else if (lockedCells[y][x]) {
                    tile.setFill(new ImagePattern(lockImage));
                } else if (eggLocations[y][x]) {
                    tile.setFill(new ImagePattern(eggImage));
                } else {
                    tile.setFill(Color.WHITE);
                }

                grid[y][x] = tile;
                gridPane.add(tile, x, y);
            }
        }

        grid[playerY][playerX].setFill(new ImagePattern(playerImage));

        root.setCenter(gridPane);

        Scene scene = new Scene(root, 650, 600); // Set the desired width and height of the stage
        scene.setOnKeyPressed(e -> handleArrowButtonPress(String.valueOf(e.getCode())));

        keyCounterText = new Text("Key Count: 0");
        eggCounterText = new Text("Egg Count: 0");
        movementCounterText = new Text("Steps Taken: 0");
        scoreText = new Text("");

        GridPane countersPane = new GridPane();
        countersPane.setAlignment(Pos.CENTER);
        countersPane.setHgap(10);

        countersPane.add(keyCounterText, 0, 0);
        countersPane.add(eggCounterText, 1, 0);
        countersPane.add(movementCounterText, 2, 0);
        countersPane.add(scoreText, 3, 0);

        root.setTop(countersPane);

        primaryStage.setTitle("Maze Runner Game");
        primaryStage.setScene(scene);

        // Create the arrow buttons
        Button upButton = createArrowButton("↑");
        Button downButton = createArrowButton("↓");
        Button rightButton = createArrowButton("→");
        Button leftButton = createArrowButton("←");

        upButton.setOnAction(event -> handleArrowButtonPress("UP"));
        downButton.setOnAction(event -> handleArrowButtonPress("DOWN"));
        rightButton.setOnAction(event -> handleArrowButtonPress("RIGHT"));
        leftButton.setOnAction(event -> handleArrowButtonPress("LEFT"));

        // Create an invisible grid layout for arrow buttons
        GridPane arrowButtonsGrid = new GridPane();
        arrowButtonsGrid.setAlignment(Pos.CENTER);
        arrowButtonsGrid.setHgap(10);
        arrowButtonsGrid.setVgap(10);

        // Add arrow buttons to the grid layout
        arrowButtonsGrid.add(new Label(), 0, 0);
        arrowButtonsGrid.add(upButton, 1, 0);
        arrowButtonsGrid.add(new Label(), 2, 0);
        arrowButtonsGrid.add(leftButton, 0, 1);
        arrowButtonsGrid.add(rightButton, 2, 1);
        arrowButtonsGrid.add(new Label(), 0, 2);
        arrowButtonsGrid.add(downButton, 1, 2);
        arrowButtonsGrid.add(new Label(), 2, 2);
        root.setLeft(arrowButtonsGrid);








        // Create the save and load buttons
        Button saveButton = new Button("Save");
        //saveButton.setOnAction(event -> saveGame("game_state.dat"));
        saveButton.setFocusTraversable(false);

        Button loadButton = new Button("Load");
//        loadButton.setOnAction(event -> {
//            GameEngine loadedGame = loadGame("game_state.dat");
//            if (loadedGame != null) {
//                // Update the current game state with the loaded game state
//                this.playerX = loadedGame.playerX;
//                this.playerY = loadedGame.playerY;
//                this.keyLocations = loadedGame.keyLocations;
//                this.lockedCells = loadedGame.lockedCells;
//                this.eggLocations = loadedGame.eggLocations;
//                this.gameWon = loadedGame.gameWon;
//                this.openedChests = loadedGame.openedChests;
//                this.collectedKeys = loadedGame.collectedKeys;
//                this.collectedEggs = loadedGame.collectedEggs;
//                this.movementCount = loadedGame.movementCount;
//                this.grid = loadedGame.grid;
//
//                // Update the GUI with the loaded game state
//                updateGUI();
//                System.out.println("Game loaded successfully!");
//            } else {
//                System.out.println("Failed to load game!");
//            }
//        });
        loadButton.setFocusTraversable(false);

        // Add the save and load buttons to the GUI
        VBox buttonsBox = new VBox(10, saveButton, loadButton);
        buttonsBox.setAlignment(Pos.CENTER);
        root.setRight(buttonsBox);

        primaryStage.show();
    }







    public void initializeGame(int d) {
        playerX = 0;
        playerY = MAP_SIZE - 1;
        openedChests.clear();
        collectedKeys = 0;
        collectedEggs = 0;
        gameWon = false;
        movementCount = 0;

        // Generate key locations
        for (int i = 0; i < NUM_KEYS; i++) {
            int x;
            int y;
            do {
                x = (int) (Math.random() * MAP_SIZE);
                y = (int) (Math.random() * MAP_SIZE);
            } while (keyLocations[y][x] || lockedCells[y][x] || eggLocations[y][x]);

            keyLocations[y][x] = true;
        }

        // Generate locked cells
        for (int i = 0; i < d; i++) {
            int x;
            int y;
            do {
                x = (int) (Math.random() * MAP_SIZE);
                y = (int) (Math.random() * MAP_SIZE);
            } while (keyLocations[y][x] || lockedCells[y][x] || eggLocations[y][x]);

            lockedCells[y][x] = true;
        }

        // Generate egg locations
        for (int i = 0; i < NUM_EGGS; i++) {
            int x;
            int y;
            do {
                x = (int) (Math.random() * MAP_SIZE);
                y = (int) (Math.random() * MAP_SIZE);
            } while (keyLocations[y][x] || lockedCells[y][x] || eggLocations[y][x]);

            eggLocations[y][x] = true;
        }
    }

    static class GuiPlayHandler {
        private final int d;

        public GuiPlayHandler(int d) {
            this.d = d;
        }

        public void loadGuiForPlay() {
            // Load and display the GUI for playing with the given integer d
            // ...
            System.out.println("Loading GUI for play with d = " + d);
            GameGUI gameGUI = new GameGUI(d);
            Stage testStage = new Stage();
            gameGUI.constructGui(testStage);
        }

        static void playGui() {
            Optional<String> result = InputValidator.showInputDialog("Enter Value", "Enter the number of locks (0-10):");
            result.ifPresent(d -> {
                if (InputValidator.isValidInput(d)) {
                    int value = Integer.parseInt(d);
                    GuiPlayHandler handler = new GuiPlayHandler(value);
                    handler.loadGuiForPlay();
                    System.out.println(d);
                } else {
                    InputValidator.showInvalidInputAlert();
                }
            });
        }
    }


    static class InputValidator {

        public static boolean isValidInput(String input) {
            try {
                int value = Integer.parseInt(input);
                return value >= 0 && value <= 10;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public static Optional<String> showInputDialog(String title, String prompt) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(title);
            dialog.setHeaderText(null);
            dialog.setContentText(prompt);
            return dialog.showAndWait();
        }

        public static void showInvalidInputAlert() {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid integer between 0 and 10.");
            alert.showAndWait();
        }
    }


    static class help {

        static void showHelpPopup() {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
            helpAlert.setTitle("Help");
            helpAlert.setHeaderText("Game instructions!");



            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setText("Egg hunt is a basic 10x10 grid game. Your goal is to collect all 5 eggs and make it to the exit." +
                    " The exit is located in the top right cell. There will also be 5 keys spawned across the map which allows you to open locked cells. " +
                    "You will be asked how many locked cells you want to start the game with. " +
                    "You have a maximum of 100 steps to make it to the exit. Good luck!!!");

            helpAlert.getDialogPane().setContent(textArea);
            helpAlert.showAndWait();
        }
    }

    private Button createArrowButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(40, 40);
        button.setStyle("-fx-font-size: 16px;");
        button.setFocusTraversable(false); // Add this line to disable focus traversal
        return button;
    }

//    private void updateGUI() {
//        // Update the GUI components with the loaded game state
//        keyCounterText.setText("Key Count: " + collectedKeys);
//        eggCounterText.setText("Egg Count: " + collectedEggs);
//        movementCounterText.setText("Steps Taken: " + movementCount);
//
//        for (int i = 0; i < MAP_SIZE; i++) {
//            for (int j = 0; j < MAP_SIZE; j++) {
//                // Update the grid cells based on the loaded game state
//                boolean isPlayer = (i == playerY && j == playerX);
//                boolean isKey = keyLocations[i][j];
//                boolean isLocked = lockedCells[i][j];
//                boolean isEgg = eggLocations[i][j];
//
//                Rectangle rect = grid[i][j];
//
//                if (isPlayer) {
//                    rect.setFill(new ImagePattern(playerImage));
//                } else if (isKey) {
//                    rect.setFill(new ImagePattern(keyImage));
//                } else if (isLocked) {
//                    rect.setFill(new ImagePattern(lockImage));
//                } else if (isEgg) {
//                    rect.setFill(new ImagePattern(eggImage));
//                } else {
//                    rect.setFill(Color.WHITE);
//                }
//            }
//        }
//
//        // Check if the game is won and update the message label accordingly
//        if (gameWon) {
//            scoreText.setText("Congratulations! You won the game!");
//        } else {
//            scoreText.setText("Use arrow keys to move. Collect all keys and eggs.");
//        }
//    }






}
