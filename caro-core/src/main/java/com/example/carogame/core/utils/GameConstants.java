package com.example.carogame.core.utils;

public class GameConstants {

    private GameConstants() {}

    // Game constants
    public static final int BOARD_SIZE = 20;
    public static final int EMPTY_CELL = 0;
    public static final int PLAYER_X_VALUE = 1;
    public static final int PLAYER_O_VALUE = 2;
    public static final int WIN_CONDITION = 5; // Số quân liên tiếp để thắng

    // Timer constants
    public static final int DEFAULT_TIME_LIMIT = 30; // seconds
    public static final int WARNING_TIME = 10; // Thời gian bắt đầu cảnh báo
}
