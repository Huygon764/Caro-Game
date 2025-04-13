package com.example.carogame.desktop.controller;

import com.example.carogame.core.ai.AIPlayer;
import com.example.carogame.core.event.GameEventListener;
import com.example.carogame.core.logic.GameLogic;
import com.example.carogame.core.model.GameSettings;
import com.example.carogame.core.model.GameState;
import com.example.carogame.core.model.Player;

public class DesktopGameController implements GameEventListener {
    private GameLogic gameLogic;
    private AIPlayer aiPlayer;
    private GameSettings settings;

    public DesktopGameController(GameSettings settings) {
        this.settings = settings;
        initGame();
    }

    private void initGame() {
        Player playerX = new Player(Player.PLAYER_X, "Player X", false);
        Player playerO;

        if (settings.getGameMode() == GameSettings.GameMode.PLAYER_VS_CPU) {
            playerO = new Player(Player.PLAYER_O, "CPU", true);
            aiPlayer = new AIPlayer(Player.PLAYER_O);
        } else {
            playerO = new Player(Player.PLAYER_O, "Player O", false);
            aiPlayer = null;
        }

        gameLogic = new GameLogic(settings.getBoardSize(), playerX, playerO);
        gameLogic.addListener(this);
    }

    public void updateSettings(GameSettings newSettings) {
        this.settings = newSettings;

        // Tạo players mới
        Player playerX = new Player(Player.PLAYER_X, "Player X", false);
        Player playerO;

        if (settings.getGameMode() == GameSettings.GameMode.PLAYER_VS_CPU) {
            playerO = new Player(Player.PLAYER_O, "CPU", true);
            aiPlayer = new AIPlayer(Player.PLAYER_O);
        } else {
            playerO = new Player(Player.PLAYER_O, "Player O", false);
            aiPlayer = null;
        }

        GameState gameState = gameLogic.getGameState();
        gameState.updatePlayers(playerX, playerO);
        gameState.reset();
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }

    public void makeMove(int row, int col) {
        if (gameLogic.makeMove(row, col)) {
            // Nếu đến lượt AI và game vẫn đang chơi
            if (aiPlayer != null &&
                    gameLogic.getGameState().getCurrentPlayer().isAI() &&
                    gameLogic.getGameState().getState() == GameState.State.PLAYING) {

                AIPlayer.Move aiMove = aiPlayer.makeMove(gameLogic.getGameState().getBoard());
                if (aiMove != null) {
                    gameLogic.makeMove(aiMove.row, aiMove.col);
                }
            }
        }
    }

    public void startNewGame() {
        gameLogic.startNewGame();
    }

    public void setTimerLogic() {
        gameLogic.getGameState().startTimer();
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        // Sẽ được UI cập nhật xử lý
    }

    @Override
    public void onMoveMade(int row, int col, int playerId) {
        // Sẽ được UI cập nhật xử lý
    }

    @Override
    public void onGameOver(GameState.State state) {
        // Sẽ được UI cập nhật xử lý
    }

    @Override
    public void onTimeOut(Player player) {

    }
}
