package com.example.EggHuntGame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public class GameGUI extends GameEngine {
    private final int d;
    private Image keyImage;
    private Image eggImage;
    private Image lockImage;

    public GameGUI(int d) {
        super();
        this.d = d;
    }
    // creates the UI for menu, with play, text and help buttons.
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
    // main game GUI
    public void constructGui(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        grid = new SerializableRectangle[MAP_SIZE][MAP_SIZE];
        keyLocations = new boolean[MAP_SIZE][MAP_SIZE];
        lockedCells = new boolean[MAP_SIZE][MAP_SIZE];
        eggLocations = new boolean[MAP_SIZE][MAP_SIZE];
        openedLocks = new HashSet<>();
        collectedKeys = 0;
        collectedEggs = 0;
        movementCount = 0;

        initializeGame(d);

        // Load the PNG images
        keyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/key_icon.png")));
        lockImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lock_icon.png")));
        eggImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/egg_icon.png")));
        playerImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/player_icon.png")));

        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                SerializableRectangle tile = new SerializableRectangle(TILE_SIZE, TILE_SIZE);
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
        Button save_Button = new Button("SAVE");
        save_Button.setOnAction(event -> saveData("1.save"));
        save_Button.setFocusTraversable(false);

        Button load_Button = new Button("Load");
        load_Button.setOnAction(event -> loadData("1.save"));
        load_Button.setFocusTraversable(false);

        // Add the save and load buttons to the GUI
        VBox buttonsBox = new VBox(10, save_Button, load_Button);
        buttonsBox.setAlignment(Pos.CENTER);
        root.setRight(buttonsBox);

        primaryStage.show();

    } // TODO double check overlapping tiles (eggs keys locks)

    // All data saved into "1.save" to be loaded.
    public void saveData (String fileName){
        try {
            GameEngine data = new GameEngine();
            data.playerX = playerX;
            data.playerY = playerY;
            data.keyLocations = keyLocations;
            data.lockedCells = lockedCells;
            data.eggLocations = eggLocations;
            data.openedLocks = openedLocks;
            data.collectedKeys = collectedKeys;
            data.collectedEggs = collectedEggs;
            data.movementCount = movementCount;
            data.grid = grid;
            data.keyLocations[newY][newX] = keyLocations[newY][newX];
            data.grid[newY][newX] = grid[newY][newX];
            data.lockedCells[newY][newX] = lockedCells[newY][newX];
            data.eggLocations[newY][newX] = eggLocations[newY][newX];
            data.grid[playerY][playerX] = grid[playerY][playerX];
            data.grid[playerY][playerX].setFill(new ImagePattern(playerImage));
            data.gameWon = gameWon;
            data.movementCounterTextData = movementCounterText.getText(); // Store the text value
            data.scoreTextData = scoreText.getText(); // Store the text value

            ResourceManager.save(data, fileName);
        // catch exception thrown in ResourceManager
        } catch (Exception e) {
            System.out.println("Couldn't save: " + e.getMessage());
        }
    }
    // data to be loaded from "1.save"
    public void loadData(String fileName) {
        try {
            GameEngine loadedData = (GameEngine) ResourceManager.load(fileName);
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
            // Recreate the Text objects using the stored data
            movementCounterText.setText(loadedData.movementCounterTextData);
            scoreText.setText(loadedData.scoreTextData);

            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    grid[y][x].setFill(Color.WHITE); // Clear the grid
                }
            }

            // Set the fill for key locations
            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    if (keyLocations[y][x]) {
                        grid[y][x].setFill(new ImagePattern(keyImage));
                    }
                }
            }

            // Set the fill for locked cells
            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    if (lockedCells[y][x]) {
                        grid[y][x].setFill(new ImagePattern(lockImage));
                    }
                }
            }

            // Set the fill for egg locations
            for (int y = 0; y < MAP_SIZE; y++) {
                for (int x = 0; x < MAP_SIZE; x++) {
                    if (eggLocations[y][x]) {
                        grid[y][x].setFill(new ImagePattern(eggImage));
                    }
                }
            }

            // Set the fill for the player's tile
            grid[playerY][playerX].setFill(new ImagePattern(playerImage));

            refreshGrid(); // helps fills in images for egg,keys,locks and player

        // catch exception thrown in ResourceManager
        } catch (Exception e) {
            System.out.println("Couldn't load save data: " + e.getMessage());
        }
    }


    public void initializeGame(int d) {
        playerX = 0;
        playerY = MAP_SIZE - 1;
        openedLocks.clear();
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
        // Help button displayed in the Menu
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

    public void refreshGrid() {
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                SerializableRectangle tile = grid[y][x];
                if (keyLocations[y][x]) {
                    tile.setFill(new ImagePattern(keyImage));
                } else if (lockedCells[y][x]) {
                    tile.setFill(new ImagePattern(lockImage));
                } else if (eggLocations[y][x]) {
                    tile.setFill(new ImagePattern(eggImage));
                } else {
                    tile.setFill(Color.WHITE);
                }
            }
        }

        grid[playerY][playerX].setFill(new ImagePattern(playerImage));
        keyCounterText.setText("Key Count: " + collectedKeys);
        eggCounterText.setText("Egg Count: " + collectedEggs);
        movementCounterText.setText("Steps Taken: " + movementCount);

        if (gameWon) {
            scoreText.setText("Congratulations! You won the game!");
        }
    }



} // end of class
