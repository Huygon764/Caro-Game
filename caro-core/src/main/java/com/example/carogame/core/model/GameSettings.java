package com.example.carogame.core.model;

public class GameSettings {
    public enum GameMode {
        PLAYER_VS_CPU,
        PLAYER_VS_PLAYER
    }

    private int boardSize;
    private GameMode gameMode;

    public GameSettings(int boardSize, GameMode gameMode) {
        this.boardSize = boardSize;
        this.gameMode = gameMode;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
