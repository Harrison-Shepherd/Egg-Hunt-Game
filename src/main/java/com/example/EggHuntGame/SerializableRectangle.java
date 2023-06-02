package com.example.EggHuntGame;

import javafx.scene.shape.Rectangle;
import java.io.Serializable;

public class SerializableRectangle extends Rectangle implements Serializable {
    public SerializableRectangle(double width, double height) {
        super(width, height);
    }
}