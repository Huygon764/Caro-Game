package com.example.carogame.desktop.ui;

import com.example.carogame.core.event.GameEventListener;
import com.example.carogame.core.model.Board;
import com.example.carogame.core.model.GameSettings;
import com.example.carogame.core.model.GameState;
import com.example.carogame.core.model.Player;
import com.example.carogame.core.utils.GameConstants;
import com.example.carogame.desktop.controller.DesktopGameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class CaroFrame extends JFrame implements GameEventListener {
    private final DesktopGameController controller;
    private final ModernBoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private Timer uiTimer;
    private int remainingTime;
    private final Color DARK_BG = new Color(22, 27, 34);
    private final Color GRID_COLOR = new Color(45, 55, 72);
    private final Color HIGHLIGHT_COLOR = new Color(46, 56, 64);
    private final Color TEXT_COLOR = new Color(237, 242, 247);
    private final Color PLAYER_X_COLOR = new Color(129, 230, 217);
    private final Color PLAYER_O_COLOR = new Color(245, 101, 101);
    private final Color TIMER_WARNING = new Color(245, 158, 11);

    public CaroFrame(String title) {
        super(title);
        setBackground(DARK_BG);

        // Cấu hình ban đầu
        GameSettings settings = new GameSettings(20, GameSettings.GameMode.PLAYER_VS_CPU);

        // Khởi tạo controller
        controller = new DesktopGameController(settings);
        controller.getGameLogic().addListener(this);

        // Panel chính với thiết kế hiện đại
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Thanh tiêu đề phía trên
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tạo bàn cờ hiện đại
        boardPanel = new ModernBoardPanel(controller);
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Panel điều khiển phía dưới
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Thiết lập frame
        setContentPane(mainPanel);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Bắt đầu timer
        startUITimer(30);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Tạo status label
        statusLabel = new JLabel("Lượt của Player X");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(TEXT_COLOR);
        panel.add(statusLabel, BorderLayout.WEST);

        // Tạo timer panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerPanel.setOpaque(false);

        timerLabel = new JLabel(GameConstants.DEFAULT_TIME_LIMIT + "s");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timerLabel.setForeground(TEXT_COLOR);

        JLabel timerIcon = new JLabel("\uD83D\uDD53"); // Unicode đồng hồ
        timerIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        timerIcon.setForeground(TEXT_COLOR);

        timerPanel.add(timerIcon);
        timerPanel.add(timerLabel);
        panel.add(timerPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Tạo combobox chế độ chơi
        JComboBox<String> gameModeCombo = new JComboBox<>(new String[]{"Chơi với máy", "Chơi 2 người"});
        customizeComboBox(gameModeCombo);
        gameModeCombo.addActionListener(e -> {
            int selectedIndex = gameModeCombo.getSelectedIndex();
            GameSettings.GameMode mode = (selectedIndex == 0) ?
                    GameSettings.GameMode.PLAYER_VS_CPU : GameSettings.GameMode.PLAYER_VS_PLAYER;

            GameSettings settings = new GameSettings(20, mode);
            controller.updateSettings(settings);
            controller.startNewGame();
        });

        // Tạo nút với thiết kế đẹp
        JButton newGameButton = createStyledButton("Game mới", new Color(49, 130, 206));
        newGameButton.addActionListener(e -> controller.startNewGame());

        JButton undoButton = createStyledButton("Hoàn tác", new Color(160, 174, 192));
        undoButton.addActionListener(e -> controller.getGameLogic().undo());

        // Thêm vào panel
        panel.add(gameModeCombo);
        panel.add(newGameButton);
        panel.add(undoButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));

        // Hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void customizeComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(45, 55, 72));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setPreferredSize(new Dimension(150, 40));

        // Tùy chỉnh renderer cho combobox
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(66, 153, 225) : new Color(45, 55, 72));
                setForeground(TEXT_COLOR);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    // Hiển thị đồng hồ đếm ngược
    private void startUITimer(int seconds) {
        if (uiTimer != null) {
            uiTimer.stop();
        }

        remainingTime = seconds;
        timerLabel.setText(remainingTime + "s");

        uiTimer = new Timer(1000, e -> {
            remainingTime--;
            timerLabel.setText(remainingTime + "s");

            if (remainingTime <= 10) {
                timerLabel.setForeground(TIMER_WARNING);
            } else {
                timerLabel.setForeground(TEXT_COLOR);
            }

            if (remainingTime <= 0) {
                ((Timer)e.getSource()).stop();
            }
        });

        uiTimer.start();
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        Player currentPlayer = gameState.getCurrentPlayer();
        if (gameState.getState() == GameState.State.PLAYING) {
            statusLabel.setText("Lượt của " + currentPlayer.getName());
            statusLabel.setForeground(currentPlayer.getId() == Player.PLAYER_X ?
                    PLAYER_X_COLOR : PLAYER_O_COLOR);
            startUITimer(gameState.getTimeLimit());
        }

        boardPanel.repaint();
    }

    @Override
    public void onMoveMade(int row, int col, int playerId) {
        boardPanel.repaint();
    }

    @Override
    public void onGameOver(GameState.State state) {
        if (uiTimer != null) {
            uiTimer.stop();
        }

        String message = "";
        switch (state) {
            case PLAYER_X_WON:
                message = "Player X đã thắng!";
                statusLabel.setForeground(PLAYER_X_COLOR);
                break;
            case PLAYER_O_WON:
                if (controller.getGameLogic().getGameState().getPlayerO().isAI()) {
                    message = "CPU đã thắng!";
                } else {
                    message = "Player O đã thắng!";
                }
                statusLabel.setForeground(PLAYER_O_COLOR);
                break;
            case DRAW:
                message = "Kết quả hòa!";
                statusLabel.setForeground(TEXT_COLOR);
                break;
        }

        statusLabel.setText(message);
        boardPanel.repaint();

        // Hiển thị popup thông báo
        showWinnerPopup(message);
    }

    @Override
    public void onTimeOut(Player player) {
        if (uiTimer != null) {
            uiTimer.stop();
        }

        String message = player.getName() + " đã hết thời gian và thua!";
        statusLabel.setText(message);

        // Hiển thị popup thông báo
        showWinnerPopup(message);
    }

    // Hiển thị popup thông báo người thắng
    private void showWinnerPopup(String message) {
        // Tạo một JDialog cho popup
        JDialog popup = new JDialog(this, "Kết quả", true);
        popup.setLayout(new BorderLayout());
        popup.getContentPane().setBackground(DARK_BG);

        // Tạo panel chứa thông báo
        JPanel messagePanel = new JPanel(new BorderLayout(0, 20));
        messagePanel.setBackground(DARK_BG);
        messagePanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Thêm icon
        JLabel iconLabel = new JLabel("\uD83C\uDFC6", SwingConstants.CENTER); // Trophy emoji
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(new Color(250, 204, 21)); // Gold color

        // Thêm thông báo
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(TEXT_COLOR);

        // Thêm nút đóng
        JButton closeButton = createStyledButton("Đóng", new Color(49, 130, 206));
        closeButton.addActionListener(e -> popup.dispose());

        // Thêm vào panel
        messagePanel.add(iconLabel, BorderLayout.NORTH);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(closeButton, BorderLayout.SOUTH);

        popup.add(messagePanel, BorderLayout.CENTER);
        popup.setSize(350, 250);
        popup.setLocationRelativeTo(this);

        popup.setVisible(true);

        // Hiệu ứng fade-in
        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            float opacity = popup.getOpacity();
            if (opacity < 1.0f) {
                popup.setOpacity(Math.min(opacity + 0.05f, 1.0f));
            } else {
                ((Timer)e.getSource()).stop();
            }
        });

        fadeTimer.start();
    }

    // Lớp bàn cờ hiện đại
    private class ModernBoardPanel extends JPanel {
        private final DesktopGameController controller;
        private final int cellSize = 30;
        private int hoveredRow = -1;
        private int hoveredCol = -1;

        public ModernBoardPanel(DesktopGameController controller) {
            this.controller = controller;

            setBackground(DARK_BG);

            // Thêm mouse motion listener cho hiệu ứng hover
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int boardSize = controller.getGameLogic().getGameState().getBoard().getSize();
                    int newHoveredRow = e.getY() / cellSize;
                    int newHoveredCol = e.getX() / cellSize;

                    if (newHoveredRow != hoveredRow || newHoveredCol != hoveredCol) {
                        hoveredRow = (newHoveredRow >= 0 && newHoveredRow < boardSize) ? newHoveredRow : -1;
                        hoveredCol = (newHoveredCol >= 0 && newHoveredCol < boardSize) ? newHoveredCol : -1;
                        repaint();
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    mouseMoved(e);
                }
            });

            // Mouse exit event
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    hoveredRow = -1;
                    hoveredCol = -1;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (controller.getGameLogic().getGameState().getState() ==
                            GameState.State.PLAYING) {

                        int boardSize = controller.getGameLogic().getGameState().getBoard().getSize();
                        int row = e.getY() / cellSize;
                        int col = e.getX() / cellSize;

                        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
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

            // Bật anti-aliasing cho vẽ đẹp hơn
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Board board = controller.getGameLogic().getGameState().getBoard();
            int boardSize = board.getSize();
            int width = boardSize * cellSize;
            int height = boardSize * cellSize;

            // Vẽ nền bàn cờ
            g2d.setColor(DARK_BG);
            g2d.fillRect(0, 0, width, height);

            // Vẽ lưới
            g2d.setColor(GRID_COLOR);
            g2d.setStroke(new BasicStroke(0.5f));

            for (int i = 0; i <= boardSize; i++) {
                g2d.drawLine(0, i * cellSize, width, i * cellSize);
                g2d.drawLine(i * cellSize, 0, i * cellSize, height);
            }

            // Vẽ ô đang hover
            if (hoveredRow >= 0 && hoveredCol >= 0 &&
                    board.getCellValue(hoveredRow, hoveredCol) == 0 &&
                    controller.getGameLogic().getGameState().getState() == GameState.State.PLAYING) {

                g2d.setColor(HIGHLIGHT_COLOR);
                g2d.fillRect(hoveredCol * cellSize, hoveredRow * cellSize, cellSize, cellSize);

                // Preview X hoặc O (mờ)
                int currentPlayerId = controller.getGameLogic().getGameState().getCurrentPlayer().getId();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

                if (currentPlayerId == Player.PLAYER_X) {
                    drawModernX(g2d, hoveredCol * cellSize, hoveredRow * cellSize, PLAYER_X_COLOR);
                } else {
                    drawModernO(g2d, hoveredCol * cellSize, hoveredRow * cellSize, PLAYER_O_COLOR);
                }

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            // Tô màu cho ô mới đánh
            int lastMoveRow = controller.getGameLogic().getLastMoveRow();
            int lastMoveCol = controller.getGameLogic().getLastMoveCol();

            if (lastMoveRow >= 0 && lastMoveCol >= 0) {
                g2d.setColor(HIGHLIGHT_COLOR);
                g2d.fillRect(lastMoveCol * cellSize, lastMoveRow * cellSize, cellSize, cellSize);
            }

            // Vẽ X và O
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    int cellValue = board.getCellValue(row, col);
                    if (cellValue == Player.PLAYER_X) {
                        drawModernX(g2d, col * cellSize, row * cellSize, PLAYER_X_COLOR);
                    } else if (cellValue == Player.PLAYER_O) {
                        drawModernO(g2d, col * cellSize, row * cellSize, PLAYER_O_COLOR);
                    }
                }
            }
        }

        private void drawModernX(Graphics2D g2d, int x, int y, Color color) {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int padding = (int)(cellSize * 0.25);
            g2d.drawLine(x + padding, y + padding, x + cellSize - padding, y + cellSize - padding);
            g2d.drawLine(x + cellSize - padding, y + padding, x + padding, y + cellSize - padding);
        }

        private void drawModernO(Graphics2D g2d, int x, int y, Color color) {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int padding = (int)(cellSize * 0.25);
            int diameter = cellSize - 2 * padding;
            g2d.draw(new Ellipse2D.Double(x + padding, y + padding, diameter, diameter));
        }
    }
}
