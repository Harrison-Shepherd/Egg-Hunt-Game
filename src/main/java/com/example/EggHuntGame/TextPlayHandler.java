package com.example.EggHuntGame;

import java.util.Optional;

public class TextPlayHandler {
    private final int d;

    public TextPlayHandler(int d) {
        this.d = d;
    }

    public void loadTextForPlay() {
        // Load and display the text-based interface for playing with the given integer d
        // ...
        System.out.println("Loading text-based interface for play with d = " + d);
    }

    static void playText(){
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
}
