package com.example;

import javax.swing.*;

public class Manager {
    public static void main(String[] args) {
        Editor editor = new Editor();
        editor.generateFilePanel();
        //editor.constructJsonPanel();
        //editor.printStats();
    }
    public void printErrorInPopUp(String message) {
        JOptionPane.showMessageDialog(null, "An error occurred: " + message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}


