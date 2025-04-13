package com.example.carogame.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.carogame.controller.AndroidGameController
import com.example.carogame.core.event.GameEventListener
import com.example.carogame.core.model.GameSettings
import com.example.carogame.core.model.GameState
import com.example.carogame.core.model.Player
import com.example.carogame.core.utils.GameConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Bảng màu hiện đại
private val DarkBg = Color(0xFF161B22)
private val GridColor = Color(0xFFEDF2F7)
private val HighlightColor = Color(0xFF2E3840)
private val TextColor = Color(0xB0EDF2F7)
private val PlayerXColor = Color(0xFF81E6D9) // Teal
private val PlayerOColor = Color(0xFFF56565) // Red
private val WarningColor = Color(0xFFF59E0B) // Amber

@Composable
fun GameScreen(
    boardSize: Int = 20,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var gameMode by remember { mutableStateOf(GameSettings.GameMode.PLAYER_VS_CPU) }
    var gameStatus by remember { mutableStateOf("Lượt của Player X") }

    var boardUpdateTrigger by remember { mutableStateOf(0) }

    val remainingSeconds = remember { mutableStateOf(GameConstants.DEFAULT_TIME_LIMIT) }
    var timerJob by remember { mutableStateOf<Job?>(null) }
    var isTimerActive by remember { mutableStateOf(false) }

    // State for win popup
    var showWinDialog by remember { mutableStateOf(false) }
    var winMessage by remember { mutableStateOf("") }

    // Animation for progress indicator
    val progress by animateFloatAsState(
        targetValue = remainingSeconds.value.toFloat() / GameConstants.DEFAULT_TIME_LIMIT,
        label = "Timer Progress"
    )

    val gameSettings = remember(gameMode) {
        GameSettings(boardSize, gameMode)
    }

    val controller = remember(gameSettings) {
        AndroidGameController(gameSettings, scope)
    }

    // State to track hovered cell
    val hoveredCell = remember { mutableStateOf(Pair(-1, -1)) }

    // Bắt đầu thời gian đếm ngược
    fun startTimer() {
        timerJob?.cancel()
        remainingSeconds.value = GameConstants.DEFAULT_TIME_LIMIT
        isTimerActive = true

        timerJob = scope.launch {
            while (remainingSeconds.value > 0 && isTimerActive) {
                delay(1000)
                remainingSeconds.value--

                if (remainingSeconds.value <= 0) {
                    controller.getGameLogic().handleTimeout()
                }
            }
        }
    }

    // Dừng đếm ngược
    fun stopTimer() {
        isTimerActive = false
        timerJob?.cancel()
        timerJob = null
    }

    // Cập nhật GameState khi có thay đổi
    LaunchedEffect(controller) {
        controller.getGameLogic().addListener(object : GameEventListener {
            override fun onGameStateChanged(gameState: GameState) {
                val currentPlayer = gameState.currentPlayer
                if (gameState.state == GameState.State.PLAYING) {
                    gameStatus = "Lượt của ${currentPlayer.name}"
                    startTimer()
                } else {
                    stopTimer()
                }
                boardUpdateTrigger++
            }

            override fun onMoveMade(row: Int, col: Int, playerId: Int) {
                boardUpdateTrigger++
            }

            override fun onGameOver(state: GameState.State) {
                val message = when (state) {
                    GameState.State.PLAYER_X_WON -> "Player X đã thắng!"
                    GameState.State.PLAYER_O_WON -> {
                        if (controller.getGameLogic().gameState.playerO.isAI) {
                            "CPU đã thắng!"
                        } else {
                            "Player O đã thắng!"
                        }
                    }
                    GameState.State.DRAW -> "Kết quả hòa!"
                    else -> ""
                }
                gameStatus = message
                winMessage = message
                showWinDialog = true
                stopTimer()
                boardUpdateTrigger++
            }

            override fun onTimeOut(player: Player) {
                gameStatus = "${player.name} đã hết thời gian và thua!"
                winMessage = "${player.name} đã hết thời gian và thua!"
                showWinDialog = true
                stopTimer()
                boardUpdateTrigger++
            }
        })
    }

    // Win Dialog
    if (showWinDialog) {
        WinnerDialog(
            message = winMessage,
            onDismiss = { showWinDialog = false }
        )
    }

    // Main UI
    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header 
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game status
                val statusColor = when {
                    gameStatus.contains("Player X") -> PlayerXColor
                    gameStatus.contains("Player O") || gameStatus.contains("CPU") -> PlayerOColor
                    else -> TextColor
                }

                Text(
                    text = gameStatus,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.weight(1f)
                )

                // Timer
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                remainingSeconds.value <= GameConstants.WARNING_TIME -> Color(0x33F59E0B)
                                else -> Color(0x1A3182CE)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxSize(),
                        color = when {
                            remainingSeconds.value <= 5 -> Color.Red
                            remainingSeconds.value <= GameConstants.WARNING_TIME -> WarningColor
                            else -> Color(0xFF3182CE)
                        },
                        strokeWidth = 3.dp
                    )

                    Text(
                        text = "${remainingSeconds.value}",
                        color = when {
                            remainingSeconds.value <= 5 -> Color.Red
                            remainingSeconds.value <= GameConstants.WARNING_TIME -> WarningColor
                            else -> Color(0xFF3182CE)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Game board
            ModernBoardView(
                controller = controller,
                boardUpdateTrigger = boardUpdateTrigger,
                hoveredCell = hoveredCell,
                gameMode = gameMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.6f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                    .background(DarkBg)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Game controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Game mode selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chế độ chơi:",
                        color = TextColor,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Custom toggle buttons
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2D3748))
                    ) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (gameMode == GameSettings.GameMode.PLAYER_VS_CPU)
                                        Color(0xFF3182CE) else Color.Transparent
                                )
                                .clickable {
                                    gameMode = GameSettings.GameMode.PLAYER_VS_CPU
                                    controller.updateSettings(GameSettings(boardSize, gameMode))
                                    controller.startNewGame()
                                    boardUpdateTrigger++
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chơi với máy",
                                color = if (gameMode == GameSettings.GameMode.PLAYER_VS_CPU)
                                    Color.White else Color(0xFFCBD5E0),
                                fontWeight = if (gameMode == GameSettings.GameMode.PLAYER_VS_CPU)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (gameMode == GameSettings.GameMode.PLAYER_VS_PLAYER)
                                        Color(0xFF3182CE) else Color.Transparent
                                )
                                .clickable {
                                    gameMode = GameSettings.GameMode.PLAYER_VS_PLAYER
                                    controller.updateSettings(GameSettings(boardSize, gameMode))
                                    controller.startNewGame()
                                    boardUpdateTrigger++
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chơi 2 người",
                                color = if (gameMode == GameSettings.GameMode.PLAYER_VS_PLAYER)
                                    Color.White else Color(0xFFCBD5E0),
                                fontWeight = if (gameMode == GameSettings.GameMode.PLAYER_VS_PLAYER)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New game button
                    Button(
                        onClick = {
                            controller.startNewGame()
                            boardUpdateTrigger++
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3182CE)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Game mới")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Undo button
                    Button(
                        onClick = {
                            controller.getGameLogic().undo()
                            boardUpdateTrigger++
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A5568)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Hoàn tác")
                    }
                }
            }
        }
    }
}

@Composable
fun ModernBoardView(
    controller: AndroidGameController,
    boardUpdateTrigger: Int,
    hoveredCell: MutableState<Pair<Int, Int>>,
    gameMode: GameSettings.GameMode,
    modifier: Modifier = Modifier
) {
    val gameState = controller.getGameLogic().gameState
    val board = gameState.board
    val boardSize = board.size

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .hoverable(interactionSource)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(boardUpdateTrigger, gameMode) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (gameState.state != GameState.State.PLAYING) return@detectTapGestures

                            if (gameMode == GameSettings.GameMode.PLAYER_VS_CPU &&
                                gameState.currentPlayer.isAI) {
                                return@detectTapGestures
                            }

                            val canvasWidth = size.width
                            val cellSize = canvasWidth / boardSize
                            val row = (offset.y / cellSize).toInt()
                            val col = (offset.x / cellSize).toInt()

                            if (row in 0 until boardSize && col in 0 until boardSize) {
                                controller.makeMove(row, col)
                            }
                        },
                        onPress = { offset ->
                            val canvasWidth = size.width
                            val cellSize = canvasWidth / boardSize
                            val row = (offset.y / cellSize).toInt()
                            val col = (offset.x / cellSize).toInt()

                            if (row in 0 until boardSize && col in 0 until boardSize) {
                                hoveredCell.value = Pair(row, col)
                            }

                            awaitRelease()
                            hoveredCell.value = Pair(-1, -1)
                        }
                    )
                }
        ) {
            val boardWidth = size.width
            val boardHeight = size.height
            val cellSize = minOf(boardWidth / boardSize, boardHeight / boardSize)

            // Tính toán offset để căn giữa grid
            val startX = (boardWidth - cellSize * boardSize) / 2
            val startY = 0f  // Bắt đầu từ trên cùng để tận dụng chiều cao

            // Vẽ grid
            for (i in 0..boardSize) {
                drawLine(
                    color = GridColor,
                    start = Offset(startX, startY + i * cellSize),
                    end = Offset(startX + boardSize * cellSize, startY + i * cellSize),
                    strokeWidth = 0.5f
                )

                drawLine(
                    color = GridColor,
                    start = Offset(startX + i * cellSize, startY),
                    end = Offset(startX + i * cellSize, startY + boardSize * cellSize),
                    strokeWidth = 0.5f
                )
            }

            // Highlight last move
            val lastMoveRow = controller.getGameLogic().getLastMoveRow()
            val lastMoveCol = controller.getGameLogic().getLastMoveCol()

            if (lastMoveRow >= 0 && lastMoveCol >= 0) {
                drawRect(
                    color = HighlightColor,
                    topLeft = Offset(lastMoveCol * cellSize, lastMoveRow * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }

            // Highlight hovered cell
            val (hoveredRow, hoveredCol) = hoveredCell.value
            if (hoveredRow >= 0 && hoveredCol >= 0 &&
                hoveredRow < boardSize && hoveredCol < boardSize &&
                board.getCellValue(hoveredRow, hoveredCol) == 0 &&
                gameState.state == GameState.State.PLAYING) {

                drawRect(
                    color = HighlightColor.copy(alpha = 0.5f),
                    topLeft = Offset(hoveredCol * cellSize, hoveredRow * cellSize),
                    size = Size(cellSize, cellSize)
                )

                // Preview the current player's mark with transparency
                val currentPlayerId = gameState.currentPlayer.id
                if (currentPlayerId == Player.PLAYER_X) {
                    drawModernX(
                        cellSize = cellSize,
                        topLeft = Offset(hoveredCol * cellSize, hoveredRow * cellSize),
                        color = PlayerXColor.copy(alpha = 0.3f)
                    )
                } else {
                    drawModernO(
                        cellSize = cellSize,
                        topLeft = Offset(hoveredCol * cellSize, hoveredRow * cellSize),
                        color = PlayerOColor.copy(alpha = 0.3f)
                    )
                }
            }

            // Draw X and O
            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    val cellValue = board.getCellValue(row, col)
                    val x = col * cellSize
                    val y = row * cellSize

                    when (cellValue) {
                        Player.PLAYER_X -> drawModernX(cellSize, Offset(x, y), PlayerXColor)
                        Player.PLAYER_O -> drawModernO(cellSize, Offset(x, y), PlayerOColor)
                    }
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawModernX(
    cellSize: Float,
    topLeft: Offset,
    color: Color
) {
    val padding = cellSize * 0.25f
    val x = topLeft.x
    val y = topLeft.y

    drawLine(
        color = color,
        start = Offset(x + padding, y + padding),
        end = Offset(x + cellSize - padding, y + cellSize - padding),
        strokeWidth = cellSize * 0.1f,
        cap = StrokeCap.Round
    )

    drawLine(
        color = color,
        start = Offset(x + cellSize - padding, y + padding),
        end = Offset(x + padding, y + cellSize - padding),
        strokeWidth = cellSize * 0.1f,
        cap = StrokeCap.Round
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawModernO(
    cellSize: Float,
    topLeft: Offset,
    color: Color
) {
    val padding = cellSize * 0.25f
    val x = topLeft.x
    val y = topLeft.y
    val diameter = cellSize - 2 * padding

    drawCircle(
        color = color,
        radius = diameter / 2,
        center = Offset(x + cellSize / 2, y + cellSize / 2),
        style = Stroke(width = cellSize * 0.1f)
    )
}

@Composable
fun WinnerDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkBg
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy icon with pulsating animation
                val infiniteTransition = rememberInfiniteTransition(label = "trophy")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "trophy scale"
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3182CE)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Đóng")
                }
            }
        }
    }
}