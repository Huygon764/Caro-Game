package com.example.carogame.core.logic;

import com.example.carogame.core.model.Board;

public class WinChecker {
    // Caro cần 5 quân liên tiếp để thắng
    private static final int WIN_CONDITION = 5;

    public static boolean checkWin(Board board, int row, int col, int playerId) {
        return checkHorizontal(board, row, col, playerId) ||
                checkVertical(board, row, col, playerId) ||
                checkDiagonalLeft(board, row, col, playerId) ||
                checkDiagonalRight(board, row, col, playerId);
    }

    private static boolean checkHorizontal(Board board, int row, int col, int playerId) {
        int count = 1; // Bắt đầu với 1 cho quân vừa đặt
        int size = board.getSize();

        // Kiểm tra bên trái
        for (int c = col - 1; c >= 0; c--) {
            if (board.getCellValue(row, c) == playerId) {
                count++;
            } else {
                break;
            }
        }

        // Kiểm tra bên phải
        for (int c = col + 1; c < size; c++) {
            if (board.getCellValue(row, c) == playerId) {
                count++;
            } else {
                break;
            }
        }

        return count >= WIN_CONDITION;
    }

    private static boolean checkVertical(Board board, int row, int col, int playerId) {
        int count = 1;
        int size = board.getSize();

        // Kiểm tra phía trên
        for (int r = row - 1; r >= 0; r--) {
            if (board.getCellValue(r, col) == playerId) {
                count++;
            } else {
                break;
            }
        }

        // Kiểm tra phía dưới
        for (int r = row + 1; r < size; r++) {
            if (board.getCellValue(r, col) == playerId) {
                count++;
            } else {
                break;
            }
        }

        return count >= WIN_CONDITION;
    }

    private static boolean checkDiagonalLeft(Board board, int row, int col, int playerId) {
        int count = 1;
        int size = board.getSize();

        // Kiểm tra góc trên bên trái
        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
            if (board.getCellValue(r, c) == playerId) {
                count++;
            } else {
                break;
            }
        }

        // Kiểm tra góc dưới bên phải
        for (int r = row + 1, c = col + 1; r < size && c < size; r++, c++) {
            if (board.getCellValue(r, c) == playerId) {
                count++;
            } else {
                break;
            }
        }

        return count >= WIN_CONDITION;
    }

    private static boolean checkDiagonalRight(Board board, int row, int col, int playerId) {
        int count = 1;
        int size = board.getSize();

        // Kiểm tra góc trên bên phải
        for (int r = row - 1, c = col + 1; r >= 0 && c < size; r--, c++) {
            if (board.getCellValue(r, c) == playerId) {
                count++;
            } else {
                break;
            }
        }

        // Kiểm tra góc dưới bên trái
        for (int r = row + 1, c = col - 1; r < size && c >= 0; r++, c--) {
            if (board.getCellValue(r, c) == playerId) {
                count++;
            } else {
                break;
            }
        }

        return count >= WIN_CONDITION;
    }
}
