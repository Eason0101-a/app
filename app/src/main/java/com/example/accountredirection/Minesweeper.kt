package com.example.accountredirection

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import android.content.Intent

class Minesweeper : AppCompatActivity() {
    // 遊戲主要元件
    private lateinit var gridLayoutMinesweeper: GridLayout // 遊戲棋盤
    private lateinit var tvStatus: TextView                // 顯示遊戲狀態
    private lateinit var btnRestart: Button                // 重新開始按鈕
    private lateinit var btn_Back: Button                  // 返回選單按鈕

    // 遊戲參數
    private val ROW_COUNT = 10   // 行數
    private val COL_COUNT = 10   // 列數
    private val MINE_COUNT = 15  // 地雷數量

    // 遊戲資料結構
    private var cells = Array(ROW_COUNT) { arrayOfNulls<TextView>(COL_COUNT) } // 每個格子
    private var isMine = Array(ROW_COUNT) { BooleanArray(COL_COUNT) }          // 是否有地雷
    private var isRevealed = Array(ROW_COUNT) { BooleanArray(COL_COUNT) }      // 是否被翻開
    private var isFlagged = Array(ROW_COUNT) { BooleanArray(COL_COUNT) }       // 是否被插旗
    private var isGameOver = false                                             // 是否結束

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.minesweeper_main)

        // 防止 UI 被狀態列遮住
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Minesweeper)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 綁定元件
        gridLayoutMinesweeper = findViewById(R.id.grid_layout_minesweeper)
        tvStatus = findViewById(R.id.tv_status)
        btnRestart = findViewById(R.id.btn_restart)
        btn_Back = findViewById(R.id.btn_back)

        // 重新開始按鈕
        btnRestart.setOnClickListener {
            resetGame()
        }

        // 返回選單
        btn_Back.setOnClickListener {
            val intent = Intent(this, ImageButtonGame::class.java)
            startActivity(intent)
            finish()
        }

        // 初始化遊戲
        setupGame()
    }

    /** 初始化遊戲棋盤 */
    private fun setupGame() {
        gridLayoutMinesweeper.removeAllViews()
        gridLayoutMinesweeper.columnCount = COL_COUNT
        gridLayoutMinesweeper.rowCount = ROW_COUNT
        gridLayoutMinesweeper.setBackgroundColor(Color.BLACK) // 背景色當邊框

        // 建立每個格子
        for (i in 0 until ROW_COUNT) {
            for (j in 0 until COL_COUNT) {
                isMine[i][j] = false
                isRevealed[i][j] = false
                isFlagged[i][j] = false
                cells[i][j] = createCell(i, j) // 建立一個格子
                gridLayoutMinesweeper.addView(cells[i][j])
            }
        }

        // 隨機放置地雷
        placeMines()
        tvStatus.text = "遊戲中..."
        isGameOver = false
    }

    /** 建立單一格子 */
    private fun createCell(row: Int, col: Int): TextView {
        val cell = TextView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    40f, // 高度 40dp
                    resources.displayMetrics
                ).toInt()
                columnSpec = GridLayout.spec(col, 1f)
                rowSpec = GridLayout.spec(row, 1f)
                setMargins(2, 2, 2, 2) // 2dp 邊框
            }
            setBackgroundColor(Color.LTGRAY) // 初始背景
            gravity = Gravity.CENTER
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                18f,
                resources.displayMetrics
            )

            // 點擊事件（翻格子）
            setOnClickListener {
                if (!isGameOver && !isFlagged[row][col]) {
                    onCellClick(row, col)
                }
            }

            // 長按事件（插旗）
            setOnLongClickListener {
                if (!isGameOver && !isRevealed[row][col]) {
                    toggleFlag(row, col)
                }
                true
            }
        }
        return cell
    }

    /** 隨機放置地雷 */
    private fun placeMines() {
        var minesPlaced = 0
        while (minesPlaced < MINE_COUNT) {
            val randomRow = Random.nextInt(ROW_COUNT)
            val randomCol = Random.nextInt(COL_COUNT)
            if (!isMine[randomRow][randomCol]) {
                isMine[randomRow][randomCol] = true
                minesPlaced++
            }
        }
    }

    /** 計算周圍地雷數 */
    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                val newRow = row + i
                val newCol = col + j
                if (newRow in 0 until ROW_COUNT &&
                    newCol in 0 until COL_COUNT &&
                    isMine[newRow][newCol]
                ) {
                    count++
                }
            }
        }
        return count
    }

    /** 點擊格子 */
    private fun onCellClick(row: Int, col: Int) {
        if (isRevealed[row][col] || isFlagged[row][col]) return

        isRevealed[row][col] = true
        cells[row][col]?.setBackgroundColor(Color.WHITE)

        if (isMine[row][col]) {
            // 踩到地雷
            cells[row][col]?.text = "💣"
            cells[row][col]?.setBackgroundColor(Color.RED)
            gameOver(false)
        } else {
            val mineCount = countAdjacentMines(row, col)
            if (mineCount > 0) {
                // 顯示數字
                cells[row][col]?.text = mineCount.toString()
                cells[row][col]?.setTextColor(getNumberColor(mineCount))
            } else {
                // 自動展開空白區域
                revealEmptyCells(row, col)
            }
            checkWinCondition()
        }
    }

    /** 插旗功能 */
    private fun toggleFlag(row: Int, col: Int) {
        if (isRevealed[row][col]) return
        isFlagged[row][col] = !isFlagged[row][col]

        if (isFlagged[row][col]) {
            cells[row][col]?.text = "🚩"
            cells[row][col]?.setBackgroundColor(Color.YELLOW)
        } else {
            cells[row][col]?.text = ""
            cells[row][col]?.setBackgroundColor(Color.LTGRAY)
        }
    }

    /** 自動展開空白區域 */
    private fun revealEmptyCells(row: Int, col: Int) {
        for (i in -1..1) {
            for (j in -1..1) {
                val newRow = row + i
                val newCol = col + j
                if (newRow in 0 until ROW_COUNT &&
                    newCol in 0 until COL_COUNT &&
                    !isMine[newRow][newCol] &&
                    !isRevealed[newRow][newCol]
                ) {
                    onCellClick(newRow, newCol)
                }
            }
        }
    }

    /** 數字顏色設定 */
    private fun getNumberColor(count: Int): Int {
        return when (count) {
            1 -> Color.BLUE
            2 -> Color.GREEN
            3 -> Color.RED
            4 -> Color.MAGENTA
            5 -> Color.DKGRAY
            6 -> Color.CYAN
            7 -> Color.BLACK
            8 -> Color.GRAY
            else -> Color.BLACK
        }
    }

    /** 勝利條件檢查 */
    private fun checkWinCondition() {
        var revealedCount = 0
        for (i in 0 until ROW_COUNT) {
            for (j in 0 until COL_COUNT) {
                if (isRevealed[i][j] && !isMine[i][j]) {
                    revealedCount++
                }
            }
        }
        if (revealedCount == (ROW_COUNT * COL_COUNT) - MINE_COUNT) {
            gameOver(true)
        }
    }

    /** 遊戲結束（輸/贏） */
    private fun gameOver(isWin: Boolean) {
        isGameOver = true
        if (isWin) {
            tvStatus.text = "恭喜你，獲勝了！"
            Toast.makeText(this, "恭喜你，獲勝了！", Toast.LENGTH_SHORT).show()
            // 顯示所有地雷
            for (i in 0 until ROW_COUNT) {
                for (j in 0 until COL_COUNT) {
                    if (isMine[i][j] && !isRevealed[i][j]) {
                        cells[i][j]?.text = "💣"
                        cells[i][j]?.setBackgroundColor(Color.GREEN)
                    }
                }
            }
        } else {
            tvStatus.text = "遊戲結束，你踩到地雷了！"
            Toast.makeText(this, "遊戲結束，你踩到地雷了！", Toast.LENGTH_SHORT).show()
            // 顯示所有地雷與錯誤旗子
            for (i in 0 until ROW_COUNT) {
                for (j in 0 until COL_COUNT) {
                    if (isMine[i][j]) {
                        cells[i][j]?.text = "💣"
                        cells[i][j]?.setBackgroundColor(Color.RED)
                    } else if (isFlagged[i][j] && !isMine[i][j]) {
                        cells[i][j]?.text = "❌"
                        cells[i][j]?.setBackgroundColor(Color.MAGENTA)
                    }
                }
            }
        }
    }

    /** 重置遊戲 */
    private fun resetGame() {
        for (i in 0 until ROW_COUNT) {
            for (j in 0 until COL_COUNT) {
                isMine[i][j] = false
                isRevealed[i][j] = false
                isFlagged[i][j] = false
                cells[i][j]?.text = ""
                cells[i][j]?.setBackgroundColor(Color.LTGRAY)
            }
        }
        setupGame()
    }
}
