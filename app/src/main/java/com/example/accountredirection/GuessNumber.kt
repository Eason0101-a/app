package com.example.accountredirection

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.accountredirection.databinding.GuessNumberMainBinding
import kotlin.random.Random

// A data class to hold the state of the game.
// A default value for attemptsLeft is crucial for robust initialization.
data class GameState(
    val secretNumber: Int = Random.nextInt(1, 101),
    var minRange: Int = 1,
    var maxRange: Int = 100,
    var attemptsLeft: Int = 5,
    var isGameActive: Boolean = true
)

class GuessNumber : AppCompatActivity() {

    private lateinit var binding: GuessNumberMainBinding
    // The game state is now initialized immediately, removing the fragile `lateinit`
    // and preventing crashes before the user interacts with the dialog.
    private var gameState: GameState = GameState()
    private val TAG = "GuessNumber"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GuessNumberMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Activity created. Initializing...")

        setupWindowInsets()
        setupClickListeners()

        // Display initial state and then prompt for changes.
        updateUI()
        promptForAttemptsAndStartGame()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainGuessNumber) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        binding.buttonGuess.setOnClickListener { 
            Log.d(TAG, "Guess button clicked.")
            handleGuess() 
        }
        binding.recreate.setOnClickListener { 
            Log.d(TAG, "Recreate button clicked.")
            promptForAttemptsAndStartGame() 
        }
        binding.answer.setOnClickListener { 
            Log.d(TAG, "Answer button clicked.")
            revealAnswer() 
        }
        binding.returnButton.setOnClickListener { 
            Log.d(TAG, "Return button clicked.")
            finish() 
        }
    }

    private fun startGame(totalAttempts: Int) {
        Log.d(TAG, "Starting new game with $totalAttempts attempts.")
        gameState = GameState(attemptsLeft = totalAttempts)
        binding.whether.text = getString(R.string.guess_number_start)
        updateUI()
    }

    private fun handleGuess() {
        val guessText = binding.numberInput.text.toString()
        Log.d(TAG, "Handling guess: '$guessText'")

        if (!gameState.isGameActive) {
            showToast(R.string.guess_number_game_over)
            return
        }

        val guess = guessText.toIntOrNull()

        if (guess == null || guess !in gameState.minRange..gameState.maxRange) {
            showToast(getString(R.string.guess_number_invalid_input, gameState.minRange, gameState.maxRange))
            return
        }

        gameState.attemptsLeft--

        when {
            guess < gameState.secretNumber -> {
                binding.whether.text = getString(R.string.guess_number_too_low)
                gameState.minRange = maxOf(gameState.minRange, guess + 1)
            }
            guess > gameState.secretNumber -> {
                binding.whether.text = getString(R.string.guess_number_too_high)
                gameState.maxRange = minOf(gameState.maxRange, guess - 1)
            }
            else -> {
                binding.whether.text = getString(R.string.guess_number_correct, gameState.secretNumber)
                gameState.isGameActive = false
            }
        }

        if (gameState.attemptsLeft == 0 && gameState.isGameActive) {
            binding.whether.text = getString(R.string.guess_number_no_attempts, gameState.secretNumber)
            gameState.isGameActive = false
        }

        binding.numberInput.text?.clear()
        updateUI()
    }

    private fun revealAnswer() {
        Log.d(TAG, "Revealing answer.")
        gameState.isGameActive = false
        binding.whether.text = getString(R.string.guess_number_answer_is, gameState.secretNumber)
        updateUI()
    }

    private fun updateUI() {
        Log.d(TAG, "Updating UI. Attempts left: ${gameState.attemptsLeft}")
        binding.interval.text = getString(R.string.guess_number_range, gameState.minRange, gameState.maxRange)
        binding.frequency.text = getString(R.string.guess_number_attempts, gameState.attemptsLeft)
        binding.buttonGuess.isEnabled = gameState.isGameActive
        binding.numberInput.isEnabled = gameState.isGameActive
    }

    private fun promptForAttemptsAndStartGame() {
        Log.d(TAG, "Prompting for attempts.")
        val inputEditText = EditText(this).apply {
            hint = getString(R.string.guess_number_dialog_hint)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            gravity = Gravity.CENTER
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.guess_number_dialog_title)
            .setMessage(R.string.guess_number_dialog_message)
            .setView(inputEditText)
            .setPositiveButton(R.string.guess_number_dialog_confirm) { _, _ ->
                val userInput = inputEditText.text.toString()
                val newAttempts = userInput.toIntOrNull()?.takeIf { it > 0 } ?: 5
                startGame(newAttempts)
                showToast(getString(R.string.guess_number_toast_attempts_set, newAttempts))
            }
            .setNegativeButton(R.string.guess_number_dialog_default) { _, _ ->
                startGame(5)
                showToast(getString(R.string.guess_number_toast_attempts_set, 5))
            }
            .setCancelable(false)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(stringResId: Int) {
        Toast.makeText(this, getString(stringResId), Toast.LENGTH_SHORT).show()
    }
}
