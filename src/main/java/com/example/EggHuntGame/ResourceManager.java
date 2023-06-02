package com.example.EggHuntGame;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceManager{
    // provides static methods for saving and loading objects to / from files.
    public static void save(Serializable data, String fileName) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))){
            oos.writeObject(data);
        }
    }

    public static Object load(String fileName) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            return ois.readObject();
        }
    }


}