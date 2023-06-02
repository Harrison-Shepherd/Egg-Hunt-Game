package com.example.EggHuntGame;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.*;

public class SerializableRectangle extends Rectangle implements Serializable {
    private char value;
    private transient Text text;

    public SerializableRectangle(double width, double height) {
        super(width, height);
        text = new Text();
        text.setX(getX());
        text.setY(getY());
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        text.setText(Character.toString(value));
        this.value = value;
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(text.getText());
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String textValue = in.readUTF();
        text = new Text(textValue);
        text.setX(getX());
        text.setY(getY());
    }
}
