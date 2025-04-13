package com.example.carogame.core.ai;

import com.example.carogame.core.model.Board;
import java.util.*;

public class AIPlayer {
    private final int playerId;
    private final int opponentId;
    private final Random random = new Random();

    // Các giá trị điểm số cho từng mẫu hình
    private static final int FIVE = 1000000;
    private static final int OPEN_FOUR = 100000;
    private static final int FOUR = 10000;
    private static final int OPEN_THREE = 5000;
    private static final int THREE = 1000;
    private static final int OPEN_TWO = 500;
    private static final int TWO = 100;

    // Độ sâu tìm kiếm Minimax
    private static final int MAX_DEPTH = 3;

    // Hướng
    private static final int[][] DIRECTIONS = {
            {1, 0},   // Ngang
            {0, 1},   // Dọc
            {1, 1},   // Chéo xuống
            {1, -1}   // Chéo lên
    };

    // Định nghĩa mẫu hình pattern
    private static final String[][] PATTERNS = {
            {"11111", "FIVE"},
            {"011110", "OPEN_FOUR"},
            {"011112", "FOUR"},
            {"211110", "FOUR"},
            {"01110", "OPEN_THREE"},
            {"10111", "THREE"},
            {"11101", "THREE"},
            {"11011", "THREE"},
            {"0110", "OPEN_TWO"},
            {"1010", "TWO"}
    };

    public AIPlayer(int playerId) {
        this.playerId = playerId;
        this.opponentId = (playerId == 1) ? 2 : 1;
    }

    public static class Move {
        public int row;
        public int col;
        public int score;

        public Move(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public Move(int row, int col, int score) {
            this.row = row;
            this.col = col;
            this.score = score;
        }

        @Override
        public String toString() {
            return "(" + row + "," + col + ") = " + score;
        }
    }

    public Move makeMove(Board originalBoard) {
        System.out.println("AI starting to evaluate move...");

        // Tạo bản sao bàn cờ - QUAN TRỌNG để tránh thay đổi trạng thái bàn cờ thật
        Board board = cloneBoard(originalBoard);

        // Nếu bàn cờ trống, đánh vào trung tâm
        if (isBoardEmpty(board)) {
            int center = board.getSize() / 2;
            System.out.println("AI chose center: " + center + "," + center);
            return new Move(center, center);
        }

        // 1. Tìm nước thắng ngay
        Move winningMove = findWinningMove(board, playerId);
        if (winningMove != null) {
            System.out.println("AI found winning move: " + winningMove);
            return winningMove;
        }

        // 2. Chặn nước thắng của đối thủ
        Move blockingMove = findWinningMove(board, opponentId);
        if (blockingMove != null) {
            System.out.println("AI blocking opponent's win: " + blockingMove);
            return blockingMove;
        }

        // 3. Tìm nước tạo thế "bốn mở hai đầu"
        Move openFourMove = findOpenFourMove(board);
        if (openFourMove != null) {
            System.out.println("AI found open four move: " + openFourMove);
            return openFourMove;
        }

        // 4. Chặn nước tạo thế "bốn mở hai đầu" của đối thủ
        Move blockOpenFourMove = findOpenFourMove(board, opponentId);
        if (blockOpenFourMove != null) {
            System.out.println("AI blocking opponent's open four: " + blockOpenFourMove);
            return blockOpenFourMove;
        }

        // 5. Tìm nước tạo nhiều thế "ba mở hai đầu"
        Move doubleThreatsMove = findDoubleThreatsMove(board);
        if (doubleThreatsMove != null) {
            System.out.println("AI found double threats move: " + doubleThreatsMove);
            return doubleThreatsMove;
        }

        // 6. Sử dụng Minimax cho các trường hợp phức tạp
        Move bestMove = minimaxRoot(board, MAX_DEPTH);
        System.out.println("AI chose move by minimax: " + bestMove);
        return bestMove;
    }

    // Tạo bản sao của bàn cờ
    private Board cloneBoard(Board original) {
        int size = original.getSize();
        Board clone = new Board(size);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = original.getCellValue(i, j);
                if (value != 0) {
                    clone.setCellValue(i, j, value);
                }
            }
        }

        return clone;
    }

    // Kiểm tra bàn cờ trống
    private boolean isBoardEmpty(Board board) {
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getCellValue(i, j) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // Tìm nước thắng ngay
    private Move findWinningMove(Board board, int player) {
        List<Move> moves = generateMoves(board);

        for (Move move : moves) {
            // Tạo bản sao của bàn cờ để không ảnh hưởng đến bàn cờ đang đánh giá
            Board tempBoard = cloneBoard(board);

            tempBoard.setCellValue(move.row, move.col, player);
            boolean isWinning = hasFiveInARow(tempBoard, move.row, move.col, player);

            if (isWinning) {
                return move;
            }
        }

        return null;
    }

    // Tìm nước tạo thế "bốn mở hai đầu"
    private Move findOpenFourMove(Board board) {
        return findOpenFourMove(board, playerId);
    }

    private Move findOpenFourMove(Board board, int player) {
        List<Move> moves = generateMoves(board);

        for (Move move : moves) {
            // Tạo bản sao của bàn cờ để không ảnh hưởng đến bàn cờ đang đánh giá
            Board tempBoard = cloneBoard(board);

            tempBoard.setCellValue(move.row, move.col, player);

            for (int[] dir : DIRECTIONS) {
                String pattern = getLinePattern(tempBoard, move.row, move.col, dir[0], dir[1], player);
                if (pattern.contains("011110")) {  // Mẫu "bốn mở hai đầu"
                    return move;
                }
            }
        }

        return null;
    }

    // Tìm nước tạo nhiều thế "ba mở hai đầu"
    private Move findDoubleThreatsMove(Board board) {
        List<Move> moves = generateMoves(board);

        for (Move move : moves) {
            // Tạo bản sao của bàn cờ để không ảnh hưởng đến bàn cờ đang đánh giá
            Board tempBoard = cloneBoard(board);

            tempBoard.setCellValue(move.row, move.col, playerId);

            int openThreeCount = 0;
            for (int[] dir : DIRECTIONS) {
                String pattern = getLinePattern(tempBoard, move.row, move.col, dir[0], dir[1], playerId);
                if (pattern.contains("01110")) {
                    openThreeCount++;
                }
            }

            if (openThreeCount >= 2) {
                return move;
            }
        }

        return null;
    }

    // Kiểm tra có 5 quân liên tiếp không
    private boolean hasFiveInARow(Board board, int row, int col, int player) {
        for (int[] dir : DIRECTIONS) {
            int count = 1;  // Tính cả quân vừa đánh

            // Đếm theo chiều thuận
            for (int i = 1; i <= 4; i++) {
                int newRow = row + i * dir[0];
                int newCol = col + i * dir[1];
                if (!isValidCell(board, newRow, newCol) || board.getCellValue(newRow, newCol) != player) {
                    break;
                }
                count++;
            }

            // Đếm theo chiều ngược
            for (int i = 1; i <= 4; i++) {
                int newRow = row - i * dir[0];
                int newCol = col - i * dir[1];
                if (!isValidCell(board, newRow, newCol) || board.getCellValue(newRow, newCol) != player) {
                    break;
                }
                count++;
            }

            if (count >= 5) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra ô có hợp lệ không
    private boolean isValidCell(Board board, int row, int col) {
        int size = board.getSize();
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    // Lấy mẫu hình theo một đường thẳng
    private String getLinePattern(Board board, int row, int col, int dx, int dy, int player) {
        StringBuilder pattern = new StringBuilder();
        int size = board.getSize();

        // Lấy tối đa 5 ô mỗi hướng
        for (int i = -5; i <= 5; i++) {
            int newRow = row + i * dx;
            int newCol = col + i * dy;

            if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) {
                pattern.append('2');  // Biên bàn cờ
            } else {
                int value = board.getCellValue(newRow, newCol);
                if (value == player) {
                    pattern.append('1');  // Quân của người chơi
                } else if (value == 0) {
                    pattern.append('0');  // Ô trống
                } else {
                    pattern.append('2');  // Quân đối thủ
                }
            }
        }

        return pattern.toString();
    }

    // Tạo danh sách các nước đi khả thi
    private List<Move> generateMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        Set<String> moveSet = new HashSet<>();  // Tránh trùng lặp
        int size = board.getSize();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getCellValue(i, j) != 0) {  // Ô có quân
                    // Xem xét các ô xung quanh trong phạm vi 2 ô
                    for (int di = -2; di <= 2; di++) {
                        for (int dj = -2; dj <= 2; dj++) {
                            if (di == 0 && dj == 0) continue;

                            int ni = i + di;
                            int nj = j + dj;

                            if (isValidCell(board, ni, nj) && board.getCellValue(ni, nj) == 0) {
                                String key = ni + "," + nj;
                                if (!moveSet.contains(key)) {
                                    moveSet.add(key);
                                    moves.add(new Move(ni, nj));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Nếu không có nước nào (hiếm khi xảy ra), thử các vị trí gần trung tâm
        if (moves.isEmpty()) {
            int center = size / 2;
            for (int di = -2; di <= 2; di++) {
                for (int dj = -2; dj <= 2; dj++) {
                    int ni = center + di;
                    int nj = center + dj;
                    if (isValidCell(board, ni, nj) && board.getCellValue(ni, nj) == 0) {
                        moves.add(new Move(ni, nj));
                    }
                }
            }
        }

        return moves;
    }

    // Triển khai Minimax với cắt tỉa Alpha-Beta
    private Move minimaxRoot(Board board, int depth) {
        List<Move> possibleMoves = generateMoves(board);

        // Đánh giá nhanh các nước đi để sắp xếp (tăng hiệu quả cắt tỉa)
        for (Move move : possibleMoves) {
            // Tạo bản sao của bàn cờ
            Board tempBoard = cloneBoard(board);

            tempBoard.setCellValue(move.row, move.col, playerId);
            move.score = evaluateBoard(tempBoard);
        }

        // Sắp xếp các nước đi theo thứ tự giảm dần để tăng hiệu quả cắt tỉa
        possibleMoves.sort((a, b) -> Integer.compare(b.score, a.score));

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (Move move : possibleMoves) {
            // Tạo bản sao của bàn cờ
            Board tempBoard = cloneBoard(board);

            tempBoard.setCellValue(move.row, move.col, playerId);
            int score = minimax(tempBoard, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
                bestMove.score = score;
            }
        }

        // In top 3 nước đi để debug
        System.out.println("Top moves by minimax:");
        for (int i = 0; i < Math.min(3, possibleMoves.size()); i++) {
            System.out.println((i+1) + ". " + possibleMoves.get(i));
        }

        return bestMove != null ? bestMove : (possibleMoves.isEmpty() ? findRandomMove(board) : possibleMoves.get(0));
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        // Kiểm tra điều kiện dừng
        if (depth == 0) {
            return evaluateBoard(board);
        }

        List<Move> moves = generateMoves(board);
        if (moves.isEmpty()) {
            return 0; // Hòa
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                // Tạo bản sao của bàn cờ
                Board tempBoard = cloneBoard(board);

                tempBoard.setCellValue(move.row, move.col, playerId);

                // Kiểm tra thắng nhanh
                if (hasFiveInARow(tempBoard, move.row, move.col, playerId)) {
                    return FIVE;
                }

                int eval = minimax(tempBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Cắt tỉa Alpha
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                // Tạo bản sao của bàn cờ
                Board tempBoard = cloneBoard(board);

                tempBoard.setCellValue(move.row, move.col, opponentId);

                // Kiểm tra thua nhanh
                if (hasFiveInARow(tempBoard, move.row, move.col, opponentId)) {
                    return -FIVE;
                }

                int eval = minimax(tempBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Cắt tỉa Beta
                }
            }
            return minEval;
        }
    }

    // Đánh giá bàn cờ
    private int evaluateBoard(Board board) {
        int aiScore = 0;
        int opponentScore = 0;
        int size = board.getSize();

        // Đánh giá theo hàng
        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= size - 5; j++) {
                aiScore += evaluateLine(board, i, j, 0, 1, playerId);
                opponentScore += evaluateLine(board, i, j, 0, 1, opponentId);
            }
        }

        // Đánh giá theo cột
        for (int i = 0; i <= size - 5; i++) {
            for (int j = 0; j < size; j++) {
                aiScore += evaluateLine(board, i, j, 1, 0, playerId);
                opponentScore += evaluateLine(board, i, j, 1, 0, opponentId);
            }
        }

        // Đánh giá theo đường chéo xuống
        for (int i = 0; i <= size - 5; i++) {
            for (int j = 0; j <= size - 5; j++) {
                aiScore += evaluateLine(board, i, j, 1, 1, playerId);
                opponentScore += evaluateLine(board, i, j, 1, 1, opponentId);
            }
        }

        // Đánh giá theo đường chéo lên
        for (int i = 4; i < size; i++) {
            for (int j = 0; j <= size - 5; j++) {
                aiScore += evaluateLine(board, i, j, -1, 1, playerId);
                opponentScore += evaluateLine(board, i, j, -1, 1, opponentId);
            }
        }

        // Chênh lệch giữa điểm AI và điểm đối thủ
        return aiScore - opponentScore;
    }

    // Đánh giá một đường dài 5 ô
    private int evaluateLine(Board board, int row, int col, int dRow, int dCol, int player) {
        int opponent = (player == playerId) ? opponentId : playerId;
        int playerCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;

        for (int i = 0; i < 5; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            int cell = board.getCellValue(newRow, newCol);

            if (cell == player) {
                playerCount++;
            } else if (cell == opponent) {
                opponentCount++;
            } else {
                emptyCount++;
            }
        }

        // Nếu có cả hai loại quân, không đánh giá
        if (playerCount > 0 && opponentCount > 0) {
            return 0;
        }

        // Tính điểm dựa trên số lượng quân
        if (playerCount == 5) return FIVE;
        if (playerCount == 4 && emptyCount == 1) return FOUR;
        if (playerCount == 3 && emptyCount == 2) return THREE;
        if (playerCount == 2 && emptyCount == 3) return TWO;

        return 0;
    }

    // Tìm nước đi ngẫu nhiên
    private Move findRandomMove(Board board) {
        int size = board.getSize();
        List<Move> emptySpots = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.getCellValue(i, j) == 0) {
                    emptySpots.add(new Move(i, j));
                }
            }
        }

        if (!emptySpots.isEmpty()) {
            return emptySpots.get(random.nextInt(emptySpots.size()));
        }

        // Bàn cờ đầy, trả về nước đi ở góc
        return new Move(0, 0);
    }
}