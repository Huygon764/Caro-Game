package com.example.carogame.core.logic;

import com.example.carogame.core.ai.AIPlayer;
import com.example.carogame.core.model.Board;
import com.example.carogame.core.model.GameState;
import com.example.carogame.core.model.Player;
import com.example.carogame.core.event.GameEventListener;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private GameState gameState;
    private final List<GameEventListener> listeners = new ArrayList<>();

    private int lastMoveRow = 0;
    private int lastMoveCol = 0;
    private final List<AIPlayer.Move> moveHistory = new ArrayList<>();

    public GameLogic(int boardSize, Player playerX, Player playerO) {
        this.gameState = new GameState(boardSize, playerX, playerO);
        this.gameState.setGameLogic(this);
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    // Thêm getter cho lastMove để highlight
    public int getLastMoveRow() {
        return lastMoveRow;
    }

    public int getLastMoveCol() {
        return lastMoveCol;
    }

    public boolean undo() {
        if (moveHistory.isEmpty()) {
            System.out.println("Không có nước đi nào để hoàn tác");
            return false;
        }

        // Dừng timer nếu đang chạy
        gameState.stopTimer();

        // Lấy và xóa nước đi cuối cùng khỏi lịch sử
        AIPlayer.Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        System.out.println("lastMove: " + lastMove.col + " " + lastMove.row);

        // Xóa quân cờ khỏi bàn cờ
        gameState.getBoard().resetCellValue(lastMove.row, lastMove.col);

        if (gameState.getOpponentPlayer().isAI() && moveHistory.size() > 0) {
            AIPlayer.Move aiMove = moveHistory.remove(moveHistory.size() - 1);
            gameState.getBoard().resetCellValue(aiMove.row, aiMove.col);
            gameState.switchPlayer();
        }

        // Cập nhật lastMove
        if (!moveHistory.isEmpty()) {
            AIPlayer.Move prevMove = moveHistory.get(moveHistory.size() - 1);
            lastMoveRow = prevMove.row;
            lastMoveCol = prevMove.col;
        } else {
            lastMoveRow = -1;
            lastMoveCol = -1;
        }

        // Đặt lại trạng thái game nếu đã kết thúc
        if (gameState.getState() != GameState.State.PLAYING) {
            gameState.setState(GameState.State.PLAYING);
        }

        // Quay lại người chơi trước
        gameState.switchPlayer();

        // Khởi động lại timer
        gameState.startTimer();

        // Thông báo thay đổi
        notifyGameStateChanged();
        notifyMoveMade(lastMoveRow, lastMoveCol, gameState.getCurrentPlayer().getId() == Player.PLAYER_X ? Player.PLAYER_O : Player.PLAYER_X);

        System.out.println("Đã hoàn tác nước đi gần nhất");
        return true;
    }

    public boolean makeMove(int row, int col) {
        if (gameState.getState() != GameState.State.PLAYING) {
            return false;
        }

        // Dừng timer của lượt hiện tại
        gameState.stopTimer();

        Board board = gameState.getBoard();
        Player currentPlayer = gameState.getCurrentPlayer();

        if (board.setCellValue(row, col, currentPlayer.getId())) {
            System.out.println("Game logic: Player " + currentPlayer.getId() + " makes a move at (" + row + ", " + col + ")");

            // Lưu lại nước đi cho undo
            lastMoveRow = row;
            lastMoveCol = col;
            moveHistory.add(new AIPlayer.Move(row, col));

            // Thông báo về nước đi
            notifyMoveMade(row, col, currentPlayer.getId());

            // Kiểm tra thắng
            if (WinChecker.checkWin(board, row, col, currentPlayer.getId())) {
                GameState.State newState = currentPlayer.getId() == Player.PLAYER_X ?
                        GameState.State.PLAYER_X_WON : GameState.State.PLAYER_O_WON;
                gameState.setState(newState);
                notifyGameOver(newState);
                return true;
            }

            // Kiểm tra hòa
            if (board.isFull()) {
                gameState.setState(GameState.State.DRAW);
                notifyGameOver(GameState.State.DRAW);
                return true;
            }

            // Chuyển lượt chơi
            gameState.switchPlayer();
            notifyGameStateChanged();

            return true;
        }

        return false;
    }

    public void startNewGame() {
        gameState.reset();
        notifyGameStateChanged();

        moveHistory.clear();
        lastMoveRow = -1;
        lastMoveCol = -1;

        // Khởi động timer
        gameState.startTimer();
    }

    public void handleTimeout() {
        notifyGameTimeout(gameState.getCurrentPlayer());
    }

    private void notifyGameStateChanged() {
        for (GameEventListener listener : listeners) {
            listener.onGameStateChanged(gameState);
        }
    }

    private void notifyMoveMade(int row, int col, int playerId) {
        for (GameEventListener listener : listeners) {
            listener.onMoveMade(row, col, playerId);
        }
    }

    private void notifyGameOver(GameState.State state) {
        for (GameEventListener listener : listeners) {
            listener.onGameOver(state);
        }
    }

    private void notifyGameTimeout(Player player) {
        for (GameEventListener listener : listeners) {
            listener.onTimeOut(player);
        }
    }
}
