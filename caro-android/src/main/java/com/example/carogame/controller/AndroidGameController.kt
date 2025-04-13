package com.example.carogame.controller

import com.example.carogame.core.ai.AIPlayer
import com.example.carogame.core.event.GameEventListener
import com.example.carogame.core.logic.GameLogic
import com.example.carogame.core.model.GameSettings
import com.example.carogame.core.model.GameState
import com.example.carogame.core.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AndroidGameController(
    private var settings: GameSettings,
    private val scope: CoroutineScope
) : GameEventListener {

    private lateinit var gameLogic: GameLogic
    private var aiPlayer: AIPlayer? = null

    init {
        initGame()
    }

    private fun initGame() {
        val playerX = Player(Player.PLAYER_X, "Player X", false)
        val playerO: Player

        if (settings.gameMode == GameSettings.GameMode.PLAYER_VS_CPU) {
            playerO = Player(Player.PLAYER_O, "CPU", true)
            aiPlayer = AIPlayer(Player.PLAYER_O)
        } else {
            playerO = Player(Player.PLAYER_O, "Player O", false)
            aiPlayer = null
        }

        gameLogic = GameLogic(settings.boardSize, playerX, playerO)
        gameLogic.addListener(this)
    }

    fun updateSettings(newSettings: GameSettings) {
        this.settings = newSettings

        // Tạo players mới
        val playerX = Player(Player.PLAYER_X, "Player X", false)
        val playerO: Player

        if (settings.gameMode == GameSettings.GameMode.PLAYER_VS_CPU) {
            playerO = Player(Player.PLAYER_O, "CPU", true)
            aiPlayer = AIPlayer(Player.PLAYER_O)
        } else {
            playerO = Player(Player.PLAYER_O, "Player O", false)
            aiPlayer = null
        }

        val gameState = gameLogic.gameState
        gameState.updatePlayers(playerX, playerO)
        gameState.reset()
    }

    fun getGameLogic(): GameLogic = gameLogic

    fun makeMove(row: Int, col: Int) {
        println("row, col: $row, $col" );
        if (gameLogic.makeMove(row, col)) {
            // Nếu đến lượt AI và game vẫn đang chơi
            if (aiPlayer != null &&
                gameLogic.gameState.currentPlayer.isAI &&
                gameLogic.gameState.state == GameState.State.PLAYING) {

                // Tạo độ trễ nhỏ trước khi AI đánh
                scope.launch(Dispatchers.Default) {
                    delay(500) // 500ms delay

                    val aiMove = aiPlayer?.makeMove(gameLogic.gameState.board)
                    if (aiMove != null) {
                        launch(Dispatchers.Main) {
                            gameLogic.makeMove(aiMove.row, aiMove.col)
                        }
                    }
                }
            }
        }
    }

    fun startNewGame() {
        gameLogic.startNewGame()
    }

    override fun onGameStateChanged(gameState: GameState) {
        // Sẽ được UI cập nhật xử lý
    }

    override fun onMoveMade(row: Int, col: Int, playerId: Int) {
        // Sẽ được UI cập nhật xử lý
    }

    override fun onGameOver(state: GameState.State) {
        // Sẽ được UI cập nhật xử lý
    }
}
