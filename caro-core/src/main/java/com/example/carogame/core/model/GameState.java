package com.example.carogame.core.model;

import com.example.carogame.core.logic.GameLogic;
import com.example.carogame.core.utils.GameConstants;

import java.util.Timer;
import java.util.TimerTask;

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

    // Thêm các biến quản lý thời gian
    private Timer timer;
    private int timeLimit = GameConstants.DEFAULT_TIME_LIMIT; // 30 giây mỗi lượt
    private TimerTask currentTimerTask;
    private GameLogic gameLogic; // Tham chiếu ngược


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

    public Player getOpponentPlayer() {
        return (currentPlayer == playerX) ? playerO : playerX;
    }

    public void updatePlayers(Player playerX, Player playerO) {
        this.playerX = playerX;
        this.playerO = playerO;
        this.currentPlayer = playerX;
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

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    // Phương thức khởi động timer
    public void startTimer() {
        stopTimer(); // Dừng timer cũ nếu có

        timer = new Timer();
        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Start time");
                // Thời gian hết, người chơi hiện tại thua
                if (state == State.PLAYING) {
                    if (currentPlayer.getId() == Player.PLAYER_X) {
                        setState(State.PLAYER_O_WON);
                    } else {
                        setState(State.PLAYER_X_WON);
                    }

                    // Thông báo timeout qua gameLogic
                    if (gameLogic != null) {
                        gameLogic.handleTimeout();
                    }
                }
            }
        };

        // Thời gian mặc định: 30 giây
        timer.schedule(currentTimerTask, 1000  * GameConstants.DEFAULT_TIME_LIMIT);
    }

    // Dừng timer
    public void stopTimer() {
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    // Các getter/setter cho thời gian
    public void setTimeLimit(int seconds) {
        this.timeLimit = seconds;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void reset() {
        board.reset();
        currentPlayer = playerX;
        state = State.PLAYING;

        stopTimer();
    }
}
