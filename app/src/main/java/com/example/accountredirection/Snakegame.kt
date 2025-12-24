package com.example.accountredirection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.accountredirection.databinding.SnakegameMainBinding

class SnakeGameActivity : AppCompatActivity() {

    private lateinit var binding: SnakegameMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SnakegameMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Set the score update listener on the game view
        binding.snakeGameView.setScoreUpdateListener { score ->
            binding.scoreTextView.text = getString(R.string.snake_score_format, score)
        }

        // Handle back button click
        binding.buttonBack.setOnClickListener {
            finish() // Correct way to navigate back
        }

        // Handle restart button click
        binding.buttonRestart.setOnClickListener {
            // Assuming SnakeGameView has a method to restart the game
            binding.snakeGameView.restartGame()
        }
    }
}
