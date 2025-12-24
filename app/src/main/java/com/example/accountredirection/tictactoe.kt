package com.example.accountredirection

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.accountredirection.databinding.TictactoeMainBinding

class TicTacToeActivity : AppCompatActivity() { // Renamed class to follow conventions

    private lateinit var binding: TictactoeMainBinding
    private var isPlayerX = true
    private var board = Array(3) { IntArray(3) }
    private lateinit var buttons: Array<Array<Button>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TictactoeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBoard()
        setupClickListeners()
    }

    private fun initializeBoard() {
        buttons = arrayOf(
            arrayOf(binding.button1, binding.button2, binding.button3),
            arrayOf(binding.button4, binding.button5, binding.button6),
            arrayOf(binding.button7, binding.button8, binding.button9)
        )
    }

    private fun setupClickListeners() {
        for (r in 0..2) {
            for (c in 0..2) {
                buttons[r][c].setOnClickListener { onCellClicked(r, c) }
            }
        }
        binding.againButton.setOnClickListener { resetGame() }
        binding.buttonback.setOnClickListener { finish() }
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (board[row][col] != 0) return

        board[row][col] = if (isPlayerX) 1 else 2
        val button = buttons[row][col]
        button.text = if (isPlayerX) "X" else "O"
        button.setTextColor(if (isPlayerX) ContextCompat.getColor(this, R.color.mine_number_3) else ContextCompat.getColor(this, R.color.mine_number_1))

        if (checkForWin()) {
            val winner = if (isPlayerX) getString(R.string.winner_x) else getString(R.string.winner_o)
            binding.turnTextView.text = winner
            disableBoard()
        } else if (isBoardFull()) {
            binding.turnTextView.text = getString(R.string.draw)
        } else {
            isPlayerX = !isPlayerX
            binding.turnTextView.text = if (isPlayerX) getString(R.string.turn_x) else getString(R.string.turn_o)
        }
    }

    private fun checkForWin(): Boolean {
        // Check rows, columns, and diagonals
        for (i in 0..2) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return true
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return true
        }
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return true
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return true
        return false
    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it != 0 } }
    }

    private fun disableBoard() {
        buttons.forEach { row -> row.forEach { it.isEnabled = false } }
    }

    private fun resetGame() {
        board = Array(3) { IntArray(3) }
        isPlayerX = true
        binding.turnTextView.text = getString(R.string.turn_x)
        buttons.forEach { row ->
            row.forEach {
                it.text = ""
                it.isEnabled = true
            }
        }
    }
}
