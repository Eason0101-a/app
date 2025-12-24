package com.example.accountredirection

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.accountredirection.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // For demonstration purposes, hardcoded credentials
    private val validAccount = "112233"
    private val validPassword = "112233"
    private var currentCaptcha: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupListeners()
        generateCaptcha()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.constraintLayout) { v, insets -> // Corrected from binding.root
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.captchaView.setOnClickListener {
            generateCaptcha()
        }
    }

    private fun performLogin() {
        val account = binding.etAccount.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val captchaInput = binding.etCaptcha.text.toString().trim()

        if (account.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
            showToast(R.string.fill_all_fields)
            return
        }

        if (!captchaInput.equals(currentCaptcha, ignoreCase = true)) {
            showToast(R.string.captcha_error)
            generateCaptcha()
            binding.etCaptcha.setText("")
            return
        }

        if (account == validAccount && password == validPassword) {
            showToast(R.string.login_success)
            val intent = Intent(this, ImageButtonGame::class.java)
            startActivity(intent)
            finish()
        } else {
            showToast(R.string.login_failed)
        }
    }

    private fun generateCaptcha() {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        currentCaptcha = (1..4)
            .map { source[Random.nextInt(source.length)] }
            .joinToString("")
        binding.captchaView.setCaptchaText(currentCaptcha)
    }

    private fun showToast(stringResId: Int) {
        Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show()
    }
}
