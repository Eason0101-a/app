package com.example.accountredirection

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge

class Temperature_Conversion : AppCompatActivity() {

    private lateinit var temperatureInput: EditText
    private lateinit var conversionSwitch: Switch
    private lateinit var switchStatusLabel: TextView
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.temperatureconversion_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_temperatureconversion)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        temperatureInput = findViewById(R.id.TemperatureInput)
        conversionSwitch = findViewById(R.id.conversionSwitch)
        switchStatusLabel = findViewById(R.id.switchStatusLabel)
        convertButton = findViewById(R.id.button_Conversion)
        resultTextView = findViewById(R.id.resultTextView)
        backButton = findViewById(R.id.backButton)

        resultTextView.text = "請輸入溫度"
        updateSwitchLabel()

        convertButton.setOnClickListener {
            performConversion()
        }

        // 切換Switch只更新標籤，不馬上轉換，避免用戶誤觸
        conversionSwitch.setOnCheckedChangeListener { _, _ ->
            updateSwitchLabel()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, ImageButtonGame::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateSwitchLabel() {
        switchStatusLabel.text = if (conversionSwitch.isChecked) {
            "目前為：華氏轉攝氏"
        } else {
            "目前為：攝氏轉華氏"
        }
    }

    private fun performConversion() {
        val temperatureText = temperatureInput.text.toString()
        if (temperatureText.isNotEmpty()) {
            try {
                val temperature = temperatureText.toDouble()
                val isFahrenheitToCelsius = conversionSwitch.isChecked

                val result = if (isFahrenheitToCelsius) {
                    fahrenheitToCelsius(temperature)
                } else {
                    celsiusToFahrenheit(temperature)
                }

                val unit = if (isFahrenheitToCelsius) "°C" else "°F"
                resultTextView.text = String.format("%.2f %s", result, unit)
            } catch (e: NumberFormatException) {
                resultTextView.text = "格式錯誤，請輸入數字"
            }
        } else {
            resultTextView.text = "請輸入溫度"
        }
    }

    private fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }

    private fun fahrenheitToCelsius(fahrenheit: Double): Double {
        return (fahrenheit - 32) * 5 / 9
    }
}
