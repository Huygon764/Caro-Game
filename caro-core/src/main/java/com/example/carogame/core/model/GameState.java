package com.example.carogame.core.model;

public class GameState {
    public enum State {
        PLAYING,
        PLAYER_X_WON,
        PLAYER_O_WON,
        DRAW
    }

    private final Board board;
    private Player playerX;
    private Player playerO;
    private Player currentPlayer;
    private State state;

    public GameState(int boardSize, Player playerX, Player playerO) {
        this.board = new Board(boardSize);
        this.playerX = playerX;
        this.playerO = playerO;
        this.currentPlayer = playerX; // X đi trước
        this.state = State.PLAYING;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayerX() {
        return playerX;
    }

    public Player getPlayerO() {
        return playerO;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void updatePlayers(Player playerX, Player playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void reset() {
        board.reset();
        currentPlayer = playerX;
        state = State.PLAYING;
    }
}
