package com.example.accountredirection

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.accountredirection.databinding.BmiMainBinding

class BMI_KT : AppCompatActivity() {

    private lateinit var binding: BmiMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BmiMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupClickListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.buttonCalculation.setOnClickListener {
            calculateAndShowBmi()
        }

        binding.returnButton.setOnClickListener {
            finish() // Correct way to go back
        }
    }

    private fun calculateAndShowBmi() {
        val heightStr = binding.HeightInput.text.toString()
        val weightStr = binding.WeightInput.text.toString()

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, R.string.error_incomplete_data, Toast.LENGTH_SHORT).show()
            return
        }

        val heightInMeters = heightStr.toDouble() / 100
        val weightInKg = weightStr.toDouble()
        val bmi = calculateBmi(heightInMeters, weightInKg)
        val bmiCategory = getBmiCategory(bmi)

        binding.textViewResult.text = getString(R.string.bmi_result_format, bmi, bmiCategory)
    }

    private fun calculateBmi(heightInMeters: Double, weightInKg: Double): Double {
        return weightInKg / (heightInMeters * heightInMeters)
    }

    private fun getBmiCategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> getString(R.string.bmi_category_underweight)
            bmi < 24 -> getString(R.string.bmi_category_normal)
            bmi < 27 -> getString(R.string.bmi_category_overweight)
            else -> getString(R.string.bmi_category_obese)
        }
    }
}
