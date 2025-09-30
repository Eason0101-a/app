package com.example.accountredirection

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton // 保持這個導入，因為現在 btnBack 也是 ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button // 如果不再使用普通的 Button，可以移除這個導入

class ImageButtonGame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.button_game_main)

        // 初始化所有 ImageButton
        val imageButton1: ImageButton = findViewById(R.id.imageButton1)
        val imageButton2: ImageButton = findViewById(R.id.imageButton2)
        val imageButton3: ImageButton = findViewById(R.id.imageButton3)
        val imageButton4: ImageButton = findViewById(R.id.imageButton4)
        val imageButton5: ImageButton = findViewById(R.id.imageButton5)
        val imageButton6: ImageButton = findViewById(R.id.imageButton6)

        val btnBack: Button = findViewById(R.id.btnBack)

        // 設定每個按鈕的點擊事件監聽器
        imageButton1.setOnClickListener {
            val intent = Intent(this, GuessNumber::class.java)
            startActivity(intent)
            showToast("猜數字")
        }

        imageButton2.setOnClickListener {
            val intent = Intent(this,BMI_KT::class.java)
            startActivity(intent)
            showToast("BMI")
        }

        imageButton3.setOnClickListener {
            val intent = Intent(this, SnakeGameActivity::class.java)
            startActivity(intent)
            showToast("貪吃蛇")
        }

        imageButton4.setOnClickListener {
            val intent = Intent(this, Minesweeper::class.java)
            startActivity(intent)
            showToast("採地雷")
        }

        imageButton5.setOnClickListener {
            val intent = Intent(this, Temperature_Conversion::class.java)
            startActivity(intent)
            showToast("溫度轉換")
        }

        imageButton6.setOnClickListener {
            val intent = Intent(this, TicTacToeActivity::class.java)
            startActivity(intent)
            showToast("圈圈叉叉")
        }




        // 設定返回按鈕的點擊事件監聽器
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            showToast("返回登入頁面")
        }
    }
    // 輔助函數：顯示 Toast 訊息
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}