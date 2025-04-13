package com.example.carogame.core.event;

import com.example.carogame.core.model.GameState;
import com.example.carogame.core.model.Player;

public interface GameEventListener {
    void onGameStateChanged(GameState gameState);
    void onMoveMade(int row, int col, int playerId);
    void onGameOver(GameState.State state);

    void onTimeOut(Player player);
}
