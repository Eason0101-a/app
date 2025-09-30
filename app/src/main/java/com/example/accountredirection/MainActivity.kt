package com.example.accountredirection

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import android.content.Intent
import com.example.accountredirection.CaptchaCustomView // 引用自訂的驗證碼 View

class MainActivity : AppCompatActivity() {

    // 宣告 UI 元件
    private lateinit var etAccount: EditText       // 帳號輸入框
    private lateinit var etPassword: EditText      // 密碼輸入框
    private lateinit var etCaptcha: EditText       // 驗證碼輸入框
    private lateinit var captchaView: CaptchaCustomView // 自訂驗證碼顯示區
    private lateinit var btnLogin: Button          // 登入按鈕

    // 測試用固定帳號與密碼
    private val validAccount = "112233"
    private val validPassword = "112233"

    // 存放目前產生的驗證碼
    private var currentCaptcha: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 讓畫面支援全螢幕顯示
        setContentView(R.layout.activity_main)

        // 避免被系統狀態列、導航列遮住，動態調整 Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 綁定 UI 元件
        etAccount = findViewById(R.id.etAccount)
        etPassword = findViewById(R.id.etPassword)
        etCaptcha = findViewById(R.id.etCaptcha)
        captchaView = findViewById(R.id.captchaView)
        btnLogin = findViewById(R.id.btnLogin)

        // 一開始先產生一組驗證碼
        generateCaptcha()

        // 點擊驗證碼圖片會重新產生
        captchaView.setOnClickListener {
            generateCaptcha()
        }

        // 登入按鈕點擊事件
        btnLogin.setOnClickListener {
            val account = etAccount.text.toString().trim()   // 取得帳號
            val password = etPassword.text.toString().trim() // 取得密碼
            val captchaInput = etCaptcha.text.toString().trim() // 取得使用者輸入的驗證碼

            // 檢查是否有空白欄位
            if (account.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
                Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 檢查驗證碼是否正確（忽略大小寫）
            if (!captchaInput.equals(currentCaptcha, ignoreCase = true)) {
                Toast.makeText(this, "驗證碼錯誤，請重新輸入。", Toast.LENGTH_SHORT).show()
                generateCaptcha() // 錯誤時重新產生新的驗證碼
                etCaptcha.setText("") // 清空輸入框
                return@setOnClickListener
            }

            // 檢查帳號與密碼是否正確
            if (account == validAccount && password == validPassword) {
                Toast.makeText(this, "登入成功！", Toast.LENGTH_SHORT).show()

                // 登入成功後跳轉到遊戲頁面
                val intent = Intent(this, ImageButtonGame::class.java)
                startActivity(intent)

                finish() // 關閉目前的登入畫面（按返回鍵不會回來）
            } else {
                Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 產生隨機 4 碼驗證碼
    private fun generateCaptcha() {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" // 可用的字元
        currentCaptcha = (1..4)
            .map { source[Random.nextInt(source.length)] } // 隨機挑 4 個字元
            .joinToString("")

        captchaView.setCaptchaText(currentCaptcha) // 顯示到自訂 View 上
    }
}
