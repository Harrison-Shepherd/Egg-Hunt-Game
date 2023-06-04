package com.example.EggHuntGame;

import javafx.scene.text.Text;

import java.io.*;

public class SerializableText extends Text implements Serializable {

    public SerializableText() {
        super();
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getText());
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setText(in.readUTF());
    }
}
