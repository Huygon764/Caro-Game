package com.example.carogame.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.carogame.controller.AndroidGameController
import com.example.carogame.core.model.GameState
import com.example.carogame.core.model.Player

@Composable
fun BoardView(
    controller: AndroidGameController,
    boardUpdateTrigger: Int,
    modifier: Modifier = Modifier
) {
    val gameState = controller.getGameLogic().gameState
    val board = gameState.board
    val boardSize = board.size  // Đổi tên biến để tránh xung đột

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
            .pointerInput(gameState.state, gameState.currentPlayer.id) {
                detectTapGestures { offset ->
                    // Chỉ cho phép đánh khi game đang chơi
                    if (gameState.state != GameState.State.PLAYING) return@detectTapGestures

                    // Chỉ cho phép đánh khi là lượt người chơi
                    if (gameState.currentPlayer.isAI) return@detectTapGestures

                    // Tính vị trí ô
                    val canvasWidth = size.width
                    val cellSize = canvasWidth / boardSize
                    val row = (offset.y / cellSize).toInt()
                    val col = (offset.x / cellSize).toInt()

                    if (row in 0 until boardSize && col in 0 until boardSize) {
                        controller.makeMove(row, col)
                    }
                }
            }
    ) {
        val canvasWidth = size.width
        val cellSize = canvasWidth / boardSize

        // Vẽ lưới
        val strokeWidth = 1f

        // Vẽ đường ngang
        for (i in 0..boardSize) {
            val y = i * cellSize
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = strokeWidth
            )
        }

        // Vẽ đường dọc
        for (i in 0..boardSize) {
            val x = i * cellSize
            drawLine(
                color = Color.LightGray,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = strokeWidth
            )
        }

        // Vẽ X và O
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val cellValue = board.getCellValue(row, col)
                val x = col * cellSize
                val y = row * cellSize

                when (cellValue) {
                    Player.PLAYER_X -> drawX(x, y, cellSize)
                    Player.PLAYER_O -> drawO(x, y, cellSize)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawX(
    x: Float,
    y: Float,
    cellSize: Float
) {
    val padding = cellSize * 0.2f
    val strokeWidth = cellSize * 0.1f

    drawLine(
        color = Color.Blue,
        start = Offset(x + padding, y + padding),
        end = Offset(x + cellSize - padding, y + cellSize - padding),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    drawLine(
        color = Color.Blue,
        start = Offset(x + cellSize - padding, y + padding),
        end = Offset(x + padding, y + cellSize - padding),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawO(
    x: Float,
    y: Float,
    cellSize: Float
) {
    val padding = cellSize * 0.2f
    val strokeWidth = cellSize * 0.1f

    drawOval(
        color = Color.Red,
        topLeft = Offset(x + padding, y + padding),
        size = Size(cellSize - 2 * padding, cellSize - 2 * padding),
        style = Stroke(width = strokeWidth)
    )
}
