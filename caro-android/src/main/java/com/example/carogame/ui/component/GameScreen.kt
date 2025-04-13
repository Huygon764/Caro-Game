package com.example.carogame.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carogame.controller.AndroidGameController
import com.example.carogame.core.event.GameEventListener
import com.example.carogame.core.model.GameSettings
import com.example.carogame.core.model.GameState
import com.example.carogame.ui.component.BoardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

@Composable
fun GameScreen(
    boardSize: Int = 20,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var gameMode by remember { mutableStateOf(GameSettings.GameMode.PLAYER_VS_CPU) }
    var gameStatus by remember { mutableStateOf("Lượt của Player X") }

    var boardUpdateTrigger by remember { mutableStateOf(0) }

    val gameSettings = remember(gameMode) {
        GameSettings(boardSize, gameMode)
    }

    val controller = remember(gameSettings) {
        AndroidGameController(gameSettings, scope)
    }

    // Cập nhật GameState khi có thay đổi
    LaunchedEffect(controller) {
        controller.getGameLogic().addListener(object : GameEventListener {
            override fun onGameStateChanged(gameState: GameState) {
                val currentPlayer = gameState.currentPlayer
                if (gameState.state == GameState.State.PLAYING) {
                    gameStatus = "Lượt của ${currentPlayer.name}"
                }
                boardUpdateTrigger++
            }

            override fun onMoveMade(row: Int, col: Int, playerId: Int) {
                // Không cần xử lý trong màn hình này
                boardUpdateTrigger++
            }

            override fun onGameOver(state: GameState.State) {
                gameStatus = when (state) {
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
                boardUpdateTrigger++
            }
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thanh trạng thái
        Text(
            text = gameStatus,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Bàn cờ
        BoardView(
            controller = controller,
            boardUpdateTrigger = boardUpdateTrigger,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(4.dp)
        )

        // Các nút điều khiển
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chế độ chơi:")
                Spacer(modifier = Modifier.width(8.dp))

                Row {
                    RadioButton(
                        selected = gameMode == GameSettings.GameMode.PLAYER_VS_CPU,
                        onClick = {
                            gameMode = GameSettings.GameMode.PLAYER_VS_CPU
                            controller.updateSettings(GameSettings(boardSize, gameMode))
                            controller.startNewGame()
                        }
                    )
                    Text(
                        text = "Chơi với máy",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 4.dp, end = 16.dp)
                    )

                    RadioButton(
                        selected = gameMode == GameSettings.GameMode.PLAYER_VS_PLAYER,
                        onClick = {
                            gameMode = GameSettings.GameMode.PLAYER_VS_PLAYER
                            controller.updateSettings(GameSettings(boardSize, gameMode))
                            controller.startNewGame()
                        }
                    )
                    Text(
                        text = "Chơi 2 người",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 4.dp)
                    )
                }
            }

            Button(
                onClick = { controller.startNewGame() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text("Game mới")
            }
        }
    }
}
