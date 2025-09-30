package com.example.accountredirection

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import android.content.Intent

// 猜數字遊戲 Activity
class GuessNumber : AppCompatActivity() {
    // 宣告 UI 元件
    private lateinit var resultTextView: TextView     // 顯示結果 (太大/太小/答對)
    private lateinit var rangeTextView: TextView      // 顯示範圍
    private lateinit var attemptsTextView: TextView   // 顯示剩餘次數
    private lateinit var guessEditText: EditText      // 玩家輸入的數字
    private lateinit var guessButton: Button          // 猜測按鈕
    private lateinit var restartButton: Button        // 重新開始按鈕
    private lateinit var revealButton: Button         // 顯示答案按鈕
    private lateinit var Return_button: Button        // 返回主畫面按鈕

    // 遊戲相關變數
    private var randomNumber: Int = 0   // 隨機產生的答案
    private var minRange: Int = 1       // 最小範圍
    private var maxRange: Int = 100     // 最大範圍
    private var attemptsLeft: Int = 5   // 預設猜謎次數

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.guess_number_main)

        // 設定畫面避免被系統狀態列或導覽列遮到
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_GuessNumber)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 綁定畫面元件
        resultTextView = findViewById(R.id.Whether)
        rangeTextView = findViewById(R.id.Interval)
        guessEditText = findViewById(R.id.NumberInput)
        guessButton = findViewById(R.id.button_Guess)
        attemptsTextView = findViewById(R.id.Frequency)
        restartButton = findViewById(R.id.Recreate)
        revealButton = findViewById(R.id.Answer)
        Return_button = findViewById(R.id.Return_button)

        // 返回主畫面
        Return_button.setOnClickListener {
            val intent = Intent(this, ImageButtonGame::class.java)
            startActivity(intent)
        }

        showAttemptsDialog() // 啟動時先讓玩家設定猜謎次數
        startGame()          // 初始化遊戲

        // 猜數字按鈕事件
        guessButton.setOnClickListener {
            val guessText = guessEditText.text.toString()
            if (guessText.isNotEmpty()) {
                val guess = guessText.toIntOrNull()
                if (guess != null && guess in minRange..maxRange) {
                    attemptsLeft-- // 減少一次機會
                    updateAttemptsTextView()
                    checkGuess(guess) // 判斷大小
                    guessEditText.text.clear()

                    // 如果用完次數而且還沒答對
                    if (attemptsLeft == 0 && !resultTextView.text.contains("恭喜")) {
                        resultTextView.text = "次數用完！答案是 $randomNumber"
                        guessButton.isEnabled = false
                        guessEditText.isEnabled = false
                    }
                } else {
                    Toast.makeText(this, "請輸入 $minRange 到 $maxRange 之間的數字", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "請輸入數字", Toast.LENGTH_SHORT).show()
            }
        }

        // 重新開始遊戲
        restartButton.setOnClickListener {
            showAttemptsDialog()
        }

        // 公布答案
        revealButton.setOnClickListener {
            resultTextView.text = "答案是 $randomNumber"
            guessButton.isEnabled = false
            guessEditText.isEnabled = false
        }
    }

    // 初始化遊戲 (可指定猜謎次數)
    private fun startGame(customAttempts: Int = 5) {
        randomNumber = Random.nextInt(1, 101) // 產生 1~100 的隨機數字
        minRange = 1
        maxRange = 100
        attemptsLeft = customAttempts
        updateRangeTextView()
        updateAttemptsTextView()
        resultTextView.text = "開始猜數字吧！"
        guessButton.isEnabled = true
        guessEditText.isEnabled = true
    }

    // 更新範圍顯示
    private fun updateRangeTextView() {
        rangeTextView.text = "範圍：$minRange ~ $maxRange"
    }

    // 更新剩餘次數顯示
    private fun updateAttemptsTextView() {
        attemptsTextView.text = "剩餘次數：$attemptsLeft"
    }

    // 顯示輸入猜謎次數的對話框
    private fun showAttemptsDialog() {
        val inputEditText = EditText(this).apply {
            hint = "請輸入猜謎次數"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            gravity = Gravity.CENTER
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("設定猜謎次數")
            .setMessage("請輸入你想要的猜謎次數：")
            .setView(inputEditText)
            .setPositiveButton("確定") { _, _ ->
                val userInput = inputEditText.text.toString()
                val newAttempts = userInput.toIntOrNull()
                if (newAttempts != null && newAttempts > 0) {
                    startGame(newAttempts) // 用玩家輸入的次數重新開始遊戲
                    Toast.makeText(this, "已設定猜謎次數為 $newAttempts 次", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "請輸入有效的正整數", Toast.LENGTH_SHORT).show()
                    showAttemptsDialog() // 如果輸入不正確，重新叫出對話框
                }
            }
            .setCancelable(false) // 不允許點外面取消
            .create()

        dialog.show()
    }

    // 檢查玩家的猜測
    private fun checkGuess(guess: Int) {
        if (guess < randomNumber) {
            resultTextView.text = "太小了！"
            minRange = maxOf(minRange, guess) // 更新最小範圍
        } else if (guess > randomNumber) {
            resultTextView.text = "太大了！"
            maxRange = minOf(maxRange, guess) // 更新最大範圍
        } else {
            resultTextView.text = "恭喜你猜對了！答案是 $randomNumber"
            guessButton.isEnabled = false
            guessEditText.isEnabled = false
        }
        updateRangeTextView()
    }
}
