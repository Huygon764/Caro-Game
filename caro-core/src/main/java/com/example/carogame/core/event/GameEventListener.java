package com.example.carogame.core.event;

import com.example.carogame.core.model.GameState;

public interface GameEventListener {
    void onGameStateChanged(GameState gameState);
    void onMoveMade(int row, int col, int playerId);
    void onGameOver(GameState.State state);
}
