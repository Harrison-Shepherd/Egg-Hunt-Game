package com.example.EggHuntGame;

import javafx.scene.shape.Rectangle;
import java.io.Serializable;

public class SerializableRectangle extends Rectangle implements Serializable {
    // class for my javafx shape objects so they can be properly saved. Could probably be done another way
    public SerializableRectangle(double width, double height) {
        super(width, height);
    }
}