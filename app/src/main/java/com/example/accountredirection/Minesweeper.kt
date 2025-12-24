package com.example.accountredirection

import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.accountredirection.databinding.MinesweeperMainBinding
import kotlin.random.Random

class Minesweeper : AppCompatActivity() {

    private lateinit var binding: MinesweeperMainBinding

    private val ROW_COUNT = 10
    private val COL_COUNT = 10
    private val MINE_COUNT = 15

    private val cells = Array(ROW_COUNT) { arrayOfNulls<TextView>(COL_COUNT) }
    private var isMine = Array(ROW_COUNT) { BooleanArray(COL_COUNT) }
    private var isRevealed = Array(ROW_COUNT) { BooleanArray(COL_COUNT) }
    private var isFlagged = Array(ROW_COUNT) { BooleanArray(COL_COUNT) }
    private var isGameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MinesweeperMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupClickListeners()
        resetGame()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainMinesweeper) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.btnRestart.setOnClickListener { resetGame() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun resetGame() {
        isGameOver = false
        binding.tvStatus.text = getString(R.string.status_playing)
        initializeGrid()
        placeMines()
    }

    private fun initializeGrid() {
        binding.gridLayoutMinesweeper.removeAllViews()
        binding.gridLayoutMinesweeper.columnCount = COL_COUNT
        binding.gridLayoutMinesweeper.rowCount = ROW_COUNT

        for (row in 0 until ROW_COUNT) {
            for (col in 0 until COL_COUNT) {
                isMine[row][col] = false
                isRevealed[row][col] = false
                isFlagged[row][col] = false
                val cell = createCell(row, col)
                cells[row][col] = cell
                binding.gridLayoutMinesweeper.addView(cell)
            }
        }
    }

    private fun createCell(row: Int, col: Int): TextView {
        val cellHeight = resources.getDimensionPixelSize(R.dimen.mine_cell_height)
        val cellMargin = resources.getDimensionPixelSize(R.dimen.mine_cell_margin)

        return TextView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = cellHeight
                columnSpec = GridLayout.spec(col, 1f)
                rowSpec = GridLayout.spec(row, 1f)
                setMargins(cellMargin, cellMargin, cellMargin, cellMargin)
            }
            setBackgroundColor(ContextCompat.getColor(context, R.color.mine_cell_default))
            gravity = Gravity.CENTER
            textSize = resources.getDimension(R.dimen.mine_cell_text_size)

            setOnClickListener { onCellClicked(row, col) }
            setOnLongClickListener {
                toggleFlag(row, col)
                true
            }
        }
    }

    private fun placeMines() {
        var minesPlaced = 0
        while (minesPlaced < MINE_COUNT) {
            val r = Random.nextInt(ROW_COUNT)
            val c = Random.nextInt(COL_COUNT)
            if (!isMine[r][c]) {
                isMine[r][c] = true
                minesPlaced++
            }
        }
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (isGameOver || isRevealed[row][col] || isFlagged[row][col]) return

        isRevealed[row][col] = true
        cells[row][col]?.setBackgroundColor(ContextCompat.getColor(this, R.color.mine_cell_revealed))

        if (isMine[row][col]) {
            cells[row][col]?.text = getString(R.string.mine_emoji)
            gameOver(false)
        } else {
            val adjacentMines = countAdjacentMines(row, col)
            if (adjacentMines > 0) {
                cells[row][col]?.apply {
                    text = adjacentMines.toString()
                    setTextColor(getNumberColor(adjacentMines))
                }
            } else {
                revealEmptyCells(row, col) // Recursive reveal
            }
            checkWinCondition()
        }
    }

    private fun toggleFlag(row: Int, col: Int) {
        if (isGameOver || isRevealed[row][col]) return

        isFlagged[row][col] = !isFlagged[row][col]
        cells[row][col]?.apply {
            if (isFlagged[row][col]) {
                text = getString(R.string.flag_emoji)
                setBackgroundColor(ContextCompat.getColor(context, R.color.flag_background))
            } else {
                text = ""
                setBackgroundColor(ContextCompat.getColor(context, R.color.mine_cell_default))
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (r in (row - 1)..(row + 1)) {
            for (c in (col - 1)..(col + 1)) {
                if (r in 0 until ROW_COUNT && c in 0 until COL_COUNT && isMine[r][c]) {
                    count++
                }
            }
        }
        return count
    }

    private fun revealEmptyCells(row: Int, col: Int) {
        for (r in (row - 1)..(row + 1)) {
            for (c in (col - 1)..(col + 1)) {
                if (r in 0 until ROW_COUNT && c in 0 until COL_COUNT && !isRevealed[r][c]) {
                    onCellClicked(r, c) // This will handle all logic
                }
            }
        }
    }

    private fun checkWinCondition() {
        val nonMineCellsRevealed = (0 until ROW_COUNT).sumOf { r ->
            (0 until COL_COUNT).count { c -> isRevealed[r][c] && !isMine[r][c] }
        }

        if (nonMineCellsRevealed == (ROW_COUNT * COL_COUNT - MINE_COUNT)) {
            gameOver(true)
        }
    }

    private fun gameOver(isWin: Boolean) {
        isGameOver = true
        val message = if (isWin) getString(R.string.status_win) else getString(R.string.status_lose)
        binding.tvStatus.text = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Reveal all mines
        for (r in 0 until ROW_COUNT) {
            for (c in 0 until COL_COUNT) {
                cells[r][c]?.apply {
                    if (isMine[r][c]) {
                        text = getString(R.string.mine_emoji)
                        setBackgroundColor(if (isWin) ContextCompat.getColor(context, R.color.mine_background_win) else ContextCompat.getColor(context, R.color.mine_background_lose))
                    } else if (isFlagged[r][c] && !isMine[r][c]) {
                        text = getString(R.string.wrong_flag_emoji)
                        setBackgroundColor(ContextCompat.getColor(context, R.color.wrong_flag_background))
                    }
                }
            }
        }
    }

    private fun getNumberColor(count: Int): Int {
        val colorRes = when (count) {
            1 -> R.color.mine_number_1
            2 -> R.color.mine_number_2
            3 -> R.color.mine_number_3
            4 -> R.color.mine_number_4
            5 -> R.color.mine_number_5
            6 -> R.color.mine_number_6
            7 -> R.color.mine_number_7
            8 -> R.color.mine_number_8
            else -> R.color.black
        }
        return ContextCompat.getColor(this, colorRes)
    }
}
