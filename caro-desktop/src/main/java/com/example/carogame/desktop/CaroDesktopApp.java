package com.example.carogame.desktop;

import com.example.carogame.desktop.ui.CaroFrame;

import javax.swing.SwingUtilities;

public class CaroDesktopApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CaroFrame frame = new CaroFrame("Game Caro");
            frame.setVisible(true);
        });
    }
}
