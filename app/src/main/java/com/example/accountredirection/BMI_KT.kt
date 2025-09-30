package com.example.accountredirection

// 匯入需要的套件
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

// 定義 BMI_KT 活動 (Activity)，繼承 AppCompatActivity
class BMI_KT : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 啟用邊到邊的顯示
        enableEdgeToEdge()
        // 設定畫面使用的 XML 版面配置檔
        setContentView(R.layout.bmi_main)

        // 自動調整系統狀態列和導覽列的內距，避免 UI 被遮擋
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 取得輸入身高的 EditText
        val HeightInput = findViewById<EditText>(R.id.HeightInput)
        // 取得輸入體重的 EditText
        val WeightInput = findViewById<EditText>(R.id.WeightInput)
        // 取得計算按鈕
        val button_Calculation = findViewById<Button>(R.id.button_Calculation)
        // 取得顯示結果的 TextView
        val textView_result = findViewById<TextView>(R.id.textView_result)
        // 取得返回按鈕
        val Return_button = findViewById<Button>(R.id.Return_button)

        // 計算 BMI 的按鈕事件
        button_Calculation.setOnClickListener {
            // 讀取輸入框文字
            val Height = HeightInput.text.toString()
            val Weight = WeightInput.text.toString()

            // 檢查是否輸入完整
            if (Height.isEmpty() || Weight.isEmpty()) {
                Toast.makeText(this, "請輸入完整資料", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 將身高轉換成公尺 (原本輸入單位是公分)
            val height = Height.toDouble() / 100
            val weight = Weight.toDouble()
            // 計算 BMI
            val bmi = weight / (height * height)

            // 判斷 BMI 所屬的分類
            val category = when {
                bmi < 18.5 -> "過輕"
                bmi < 24 -> "正常"
                bmi < 27 -> "過重"
                else -> "肥胖"
            }

            // 四捨五入到小數點後兩位
            val bmiRounded = String.format("%.2f", bmi)
            // 顯示計算結果
            textView_result.text = "你的 BMI 是 $bmiRounded ($category)"
        }

        // 返回按鈕事件：回到 ImageButtonGame 活動
        Return_button.setOnClickListener {
            val intent= Intent(this, ImageButtonGame::class.java)
            startActivity(intent)
        }
    }
}
