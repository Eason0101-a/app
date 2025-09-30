package com.example.accountredirection

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SnakeGameActivity : AppCompatActivity() {
    private lateinit var gameView: SnakeGameView
    private lateinit var scoreTextView: TextView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snakegame_main)

        gameView = findViewById(R.id.snakeGame2)
        scoreTextView = findViewById(R.id.scoreTextView)
        backButton = findViewById(R.id.button_back)

        gameView.setScoreUpdateListener { score ->
            scoreTextView.text = "Score: $score"
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, ImageButtonGame::class.java))
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            gameView.handleTouchEvent(event.x, event.y)
            return true
        }
        return super.onTouchEvent(event)
    }
}
