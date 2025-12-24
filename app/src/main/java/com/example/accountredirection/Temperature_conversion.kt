package com.example.accountredirection

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.accountredirection.databinding.TemperatureconversionMainBinding

class Temperature_Conversion : AppCompatActivity() {

    private lateinit var binding: TemperatureconversionMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TemperatureconversionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupClickListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainTemperatureconversion) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.buttonConversion.setOnClickListener {
            convertTemperature()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun convertTemperature() {
        val inputText = binding.TemperatureInput.text.toString()
        if (inputText.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show()
            return
        }

        val inputTemp = inputText.toDoubleOrNull()
        if (inputTemp == null) {
            Toast.makeText(this, R.string.error_invalid_input, Toast.LENGTH_SHORT).show()
            return
        }

        val isCelsiusToFahrenheit = binding.radioCToF.isChecked
        val result: Double
        val resultUnit: String

        if (isCelsiusToFahrenheit) {
            result = (inputTemp * 9 / 5) + 32
            resultUnit = "°F"
        } else {
            result = (inputTemp - 32) * 5 / 9
            resultUnit = "°C"
        }

        binding.resultTextView.text = getString(R.string.conversion_result_format, result, resultUnit)
    }
}
