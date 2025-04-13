package com.example.carogame.desktop.ui;

import com.example.carogame.core.event.GameEventListener;
import com.example.carogame.core.model.GameSettings;
import com.example.carogame.core.model.GameState;
import com.example.carogame.desktop.controller.DesktopGameController;
import com.example.carogame.core.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CaroFrame extends JFrame implements GameEventListener {
    private final DesktopGameController controller;
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JComboBox<String> gameModeComboBox;
    private final JButton newGameButton;

    public CaroFrame(String title) {
        super(title);

        // Cấu hình ban đầu
        int boardSize = 20;
        GameSettings.GameMode mode = GameSettings.GameMode.PLAYER_VS_CPU;
        GameSettings settings = new GameSettings(boardSize, mode);

        // Khởi tạo controller
        controller = new DesktopGameController(settings);
        controller.getGameLogic().addListener(this);

        // Tạo các thành phần UI
        boardPanel = new BoardPanel(controller);

        statusLabel = new JLabel("Lượt của Player X");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        newGameButton = new JButton("Game mới");
        newGameButton.addActionListener(this::onNewGameClicked);

        gameModeComboBox = new JComboBox<>(new String[]{"Chơi với máy", "Chơi 2 người"});
        gameModeComboBox.addActionListener(this::onGameModeChanged);

        // Layout
        setLayout(new BorderLayout());

        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(new JLabel("Chế độ chơi:"));
        controlPanel.add(gameModeComboBox);
        controlPanel.add(newGameButton);

        // Thêm thành phần vào frame
        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Thiết lập thuộc tính frame
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void onNewGameClicked(ActionEvent e) {
        controller.startNewGame();
    }

    private void onGameModeChanged(ActionEvent e) {
        int selectedIndex = gameModeComboBox.getSelectedIndex();
        GameSettings.GameMode mode = (selectedIndex == 0) ?
                GameSettings.GameMode.PLAYER_VS_CPU : GameSettings.GameMode.PLAYER_VS_PLAYER;
        System.out.println("Selected mode: " + mode);
        // Cập nhật cài đặt và khởi động lại game
        GameSettings settings = new GameSettings(20, mode);
        controller.updateSettings(settings);
        controller.startNewGame();
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.getState() == GameState.State.PLAYING) {
            statusLabel.setText("Lượt của " + currentPlayer.getName());
        }

        boardPanel.repaint();
    }

    @Override
    public void onMoveMade(int row, int col, int playerId) {
        System.out.println("Repain");
        boardPanel.repaint();
    }

    @Override
    public void onGameOver(GameState.State state) {
        switch (state) {
            case PLAYER_X_WON:
                statusLabel.setText("Player X đã thắng!");
                break;
            case PLAYER_O_WON:
                if (controller.getGameLogic().getGameState().getPlayerO().isAI()) {
                    statusLabel.setText("CPU đã thắng!");
                } else {
                    statusLabel.setText("Player O đã thắng!");
                }
                break;
            case DRAW:
                statusLabel.setText("Kết quả hòa!");
                break;
        }

        boardPanel.repaint();
    }
}
