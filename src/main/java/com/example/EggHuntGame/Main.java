package com.example.EggHuntGame;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Main extends Application {

    private Parent createContent() {
        TextField variable1 = new TextField();
        TextField variable2 = new TextField();

        Button save_Button = new Button("SAVE");
        save_Button.setOnAction(event -> {
            SaveData data = new SaveData();
            data.var1 = variable1.getText();
            data.var2 = Integer.parseInt(variable2.getText());
            try {
                ResourceManager.save(data, "1.save");
            } catch (Exception e) {
                System.out.println("Couldn't save: " + e.getMessage());
            }
        });

        Button load_Button = new Button("LOAD");
        load_Button.setOnAction(event -> {
            try {
                SaveData data = (SaveData) ResourceManager.load("1.save");
                variable1.setText(data.var1);
                variable2.setText(String.valueOf(data.var2));
            } catch (Exception e) {
                System.out.println("Couldn't load save data: " + e.getMessage());
            }
        });

//
//
//
//
//
//

    return null;
    }


    @Override
    public void start(Stage stage) throws Exception {

    }
}
