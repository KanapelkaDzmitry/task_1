package com.example.test_project;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        try {
            File file = new File("newFile.txt");
            if (!file.exists())
            file.createNewFile();

            Random random = new Random();

            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println("My name is Dima");
            printWriter.close();

        } catch (IOException exception) {
            System.out.println("Error! " + exception);
        }
    }
}
