package com.example.accountredirection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.accountredirection.databinding.ButtonGameMainBinding
import kotlin.reflect.KClass

class ImageButtonGame : AppCompatActivity() {

    private lateinit var binding: ButtonGameMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ButtonGameMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGameButtons()
        setupBackButton()
    }

    private fun setupGameButtons() {
        val gameButtons = mapOf(
            binding.imageButton1 to Pair(GuessNumber::class, R.string.game_guess_number),
            binding.imageButton2 to Pair(BMI_KT::class, R.string.game_bmi),
            binding.imageButton3 to Pair(SnakeGameActivity::class, R.string.game_snake),
            binding.imageButton4 to Pair(Minesweeper::class, R.string.game_minesweeper),
            binding.imageButton5 to Pair(Temperature_Conversion::class, R.string.game_temp_converter),
            binding.imageButton6 to Pair(TicTacToeActivity::class, R.string.game_tictactoe) // Corrected class reference
        )

        gameButtons.forEach { (button, pair) ->
            val (activityClass, stringResId) = pair
            button.setOnClickListener {
                launchActivity(activityClass, stringResId)
            }
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            showToast(R.string.logout_prompt)
            finish() // Correct way to go back
        }
    }

    private fun launchActivity(activityClass: KClass<out Activity>, toastResId: Int) {
        val intent = Intent(this, activityClass.java)
        startActivity(intent)
        showToast(toastResId)
    }

    private fun showToast(stringResId: Int) {
        Toast.makeText(this, getString(stringResId), Toast.LENGTH_SHORT).show()
    }
}
