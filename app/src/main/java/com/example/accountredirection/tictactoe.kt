package com.example.accountredirection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TicTacToeActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
    private lateinit var turnTextView: TextView  // 顯示「輪到誰」
    private lateinit var resultTextView: TextView // 顯示結果（贏了/平手）
    private var currentPlayer = "X"
    private var board = Array(3) { arrayOfNulls<String>(3) }
    private var gameActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tictactoe_main)

        // 設定系統欄位的 padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_tictactoe)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化元件
        turnTextView = findViewById(R.id.turnTextView)
        resultTextView = findViewById(R.id.resultTextView)

        buttons = listOf(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9)
        )

        setupBoard()
        updateTurnText()

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (gameActive && button.text.isEmpty()) {
                    val row = index / 3
                    val col = index % 3
                    placeMark(button, row, col)

                    if (checkForWin()) {
                        resultTextView.text = "$currentPlayer 贏了！"
                        turnTextView.text = ""
                        gameActive = false
                    } else if (isBoardFull()) {
                        resultTextView.text = "平手！"
                        turnTextView.text = ""
                        gameActive = false
                    } else {
                        togglePlayer()
                        updateTurnText()
                    }
                }
            }
        }

        // 重新開始按鈕監聽
        val resetButton = findViewById<Button>(R.id.again_button)
        resetButton.setOnClickListener {
            resetGame()
        }

        // 返回按鈕監聽
        val buttonBack = findViewById<Button>(R.id.buttonback)
        buttonBack.setOnClickListener {
            val intent = Intent(this, ImageButtonGame::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = null
            }
        }
        buttons.forEach { it.text = "" }
        gameActive = true
        currentPlayer = "X"
        updateTurnText()
        resultTextView.text = ""
    }

    private fun updateTurnText() {
        turnTextView.text = "輪到 $currentPlayer"
    }

    private fun placeMark(button: Button, row: Int, col: Int) {
        button.text = currentPlayer
        board[row][col] = currentPlayer
    }

    private fun togglePlayer() {
        currentPlayer = if (currentPlayer == "X") "O" else "X"
    }

    private fun checkForWin(): Boolean {
        // 檢查行
        for (i in 0..2) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true
            }
        }
        // 檢查列
        for (i in 0..2) {
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true
            }
        }
        // 檢查對角線
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true
        }
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return true
        }
        return false
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == null) {
                    return false
                }
            }
        }
        return true
    }

    private fun resetGame() {
        setupBoard()
    }
}
