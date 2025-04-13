package com.example.carogame.core.model;

public class Board {
    private int[][] cells;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.cells = new int[size][size];
        reset();
    }

    public int getSize() {
        return size;
    }

    public int getCellValue(int row, int col) {
        if (isValidPosition(row, col)) {
            return cells[row][col];
        }
        return -1; // Vị trí không hợp lệ
    }

    public boolean setCellValue(int row, int col, int value) {
        if (isCellEmpty(row, col)) {
            cells[row][col] = value;
            return true;
        }
        return false;
    }

    public boolean resetCellValue(int row, int col) {
        cells[row][col] = 0;
        return true;
    }

    public boolean isCellEmpty(int row, int col) {
        return isValidPosition(row, col) && cells[row][col] == 0;
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = 0;
            }
        }
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
}
