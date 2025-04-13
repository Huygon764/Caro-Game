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

//        GameState gameState = new GameState(settings.getBoardSize(), playerX, playerO);
//        gameLogic.setGameState(gameState);
        gameLogic = new GameLogic(settings.getBoardSize(), playerX, playerO);
//        System.out.println("switch state: " + this);
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
//            System.out.println("Desktop controller: Player " + gameLogic.getGameState().getCurrentPlayer().getId() + " makes a move at (" + row + ", " + col + ")");
            // Nếu đến lượt AI và game vẫn đang chơi
            if (aiPlayer != null &&
                    gameLogic.getGameState().getCurrentPlayer().isAI() &&
                    gameLogic.getGameState().getState() == GameState.State.PLAYING) {

                AIPlayer.Move aiMove = aiPlayer.makeMove(gameLogic.getGameState().getBoard());
                System.out.println("AI move: " + aiMove.row + ", " + aiMove.col);
                if (aiMove != null) {
                    gameLogic.makeMove(aiMove.row, aiMove.col);
                }
            }
        }
    }
//public void makeMove(int row, int col) {
//    // Nếu đây là lượt của người chơi (không phải AI)
//    if (!gameLogic.getGameState().getCurrentPlayer().isAI()) {
//        if (gameLogic.makeMove(row, col)) {
//            // Nếu sau nước đi của người chơi, đến lượt AI và game vẫn đang chơi
//            if (aiPlayer != null &&
//                    gameLogic.getGameState().getCurrentPlayer().isAI() &&
//                    gameLogic.getGameState().getState() == GameState.State.PLAYING) {
//
//                AIPlayer.Move aiMove = aiPlayer.makeMove(gameLogic.getGameState().getBoard());
//                if (aiMove != null) {
//                    gameLogic.makeMove(aiMove.row, aiMove.col);
//                }
//            }
//        }
//    }
//    // Không cho phép người chơi đánh khi đến lượt AI
//}

    public void startNewGame() {
        gameLogic.startNewGame();
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
}
