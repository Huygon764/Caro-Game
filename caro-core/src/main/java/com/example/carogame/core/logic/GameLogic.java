package com.example.carogame.core.logic;

import com.example.carogame.core.model.Board;
import com.example.carogame.core.model.GameState;
import com.example.carogame.core.model.Player;
import com.example.carogame.core.event.GameEventListener;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private GameState gameState;
    private final List<GameEventListener> listeners = new ArrayList<>();

    public GameLogic(int boardSize, Player playerX, Player playerO) {
        this.gameState = new GameState(boardSize, playerX, playerO);
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

    public boolean makeMove(int row, int col) {
        if (gameState.getState() != GameState.State.PLAYING) {
            return false;
        }

        Board board = gameState.getBoard();
        Player currentPlayer = gameState.getCurrentPlayer();

        if (board.setCellValue(row, col, currentPlayer.getId())) {
            System.out.println("Game logic: Player " + currentPlayer.getId() + " makes a move at (" + row + ", " + col + ")");
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
    }

    private void notifyGameStateChanged() {
        for (GameEventListener listener : listeners) {
            listener.onGameStateChanged(gameState);
        }
    }

    private void notifyMoveMade(int row, int col, int playerId) {
        System.out.println("notify move");
        System.out.println("listeners: " + listeners);
        for (GameEventListener listener : listeners) {
            listener.onMoveMade(row, col, playerId);
        }
    }

    private void notifyGameOver(GameState.State state) {
        for (GameEventListener listener : listeners) {
            listener.onGameOver(state);
        }
    }
}
