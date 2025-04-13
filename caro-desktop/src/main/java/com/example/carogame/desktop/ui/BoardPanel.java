package com.example.carogame.desktop.ui;

import com.example.carogame.core.model.Board;
import com.example.carogame.core.model.Player;
import com.example.carogame.desktop.controller.DesktopGameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {
    private final DesktopGameController controller;
    private final int cellSize = 30;
    private final int boardSize;

    public BoardPanel(DesktopGameController controller) {
        this.controller = controller;
        this.boardSize = controller.getGameLogic().getGameState().getBoard().getSize();

        setPreferredSize(new Dimension(boardSize * cellSize, boardSize * cellSize));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("e: " + e);
                System.out.println("game state: " + controller.getGameLogic().getGameState().getState());
                if (controller.getGameLogic().getGameState().getState() ==
                        com.example.carogame.core.model.GameState.State.PLAYING) {

                    int row = e.getY() / cellSize;
                    int col = e.getX() / cellSize;
                    System.out.println("row: " + row);
                    System.out.println("col: " + col);

                    if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                        System.out.println("make move");
                        controller.makeMove(row, col);
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Cải thiện chất lượng vẽ
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ lưới
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= boardSize; i++) {
            g2d.drawLine(0, i * cellSize, boardSize * cellSize, i * cellSize);
            g2d.drawLine(i * cellSize, 0, i * cellSize, boardSize * cellSize);
        }

        // Vẽ X và O
        Board board = controller.getGameLogic().getGameState().getBoard();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int cellValue = board.getCellValue(row, col);
                if (cellValue == Player.PLAYER_X) {
                    drawX(g2d, col * cellSize, row * cellSize);
                } else if (cellValue == Player.PLAYER_O) {
                    drawO(g2d, col * cellSize, row * cellSize);
                }
            }
        }
    }

    private void drawX(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2));
        int padding = 5;
        g2d.drawLine(x + padding, y + padding, x + cellSize - padding, y + cellSize - padding);
        g2d.drawLine(x + cellSize - padding, y + padding, x + padding, y + cellSize - padding);
    }

    private void drawO(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        int padding = 5;
        g2d.drawOval(x + padding, y + padding, cellSize - 2 * padding, cellSize - 2 * padding);
    }
}
