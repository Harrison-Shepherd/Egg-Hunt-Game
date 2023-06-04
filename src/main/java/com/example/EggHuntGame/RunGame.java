package com.example.EggHuntGame;

import javafx.application.Application;
import javafx.stage.Stage;


public class RunGame extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameGUI.MenuGUI(primaryStage);
        primaryStage.show();
        //JFXPanel fjxPanel = new JFXPanel();
    }


} // end of class
