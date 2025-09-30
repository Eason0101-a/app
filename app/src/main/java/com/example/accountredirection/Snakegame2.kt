package com.example.accountredirection

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.math.abs

class SnakeGameView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var snake: LinkedList<Pair<Int, Int>> = LinkedList()
    private var food: Pair<Int, Int> = Pair(10, 10)
    private var direction = Direction.RIGHT
    private val gridSize = 40
    private var gameLoop: Timer? = null
    private val paint = Paint()
    private var score = 0
    private var widthInGrids: Int = 0
    private var heightInGrids: Int = 0
    private var scoreUpdateListener: ((Int) -> Unit)? = null
    private val handler = Handler(context.mainLooper)
    private var touchStartX: Float = 0f
    private var touchStartY: Float = 0f
    private val swipeThreshold = 50

    init {
        holder.addCallback(this)
        paint.color = Color.GREEN
        snake.add(Pair(5, 10))
    }

    fun setScoreUpdateListener(listener: (Int) -> Unit) {
        scoreUpdateListener = listener
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        widthInGrids = width / gridSize
        heightInGrids = height / gridSize
        startGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        widthInGrids = width / gridSize
        heightInGrids = height / gridSize
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGame()
    }

    private fun startGame() {
        snake.clear()
        snake.add(Pair(5, 10))
        direction = Direction.RIGHT
        score = 0
        scoreUpdateListener?.invoke(score)
        placeFood()
        gameLoop?.cancel()
        gameLoop = Timer()
        gameLoop?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                handler.post {
                    updateGame()
                    drawGame()
                }
            }
        }, 0, 150)
    }

    private fun stopGame() {
        gameLoop?.cancel()
        gameLoop = null
    }

    private fun updateGame() {
        val head = snake.first
        val newHead = when (direction) {
            Direction.UP -> Pair(head.first, head.second - 1)
            Direction.DOWN -> Pair(head.first, head.second + 1)
            Direction.LEFT -> Pair(head.first - 1, head.second)
            Direction.RIGHT -> Pair(head.first + 1, head.second)
        }

        if (newHead == food) {
            snake.addFirst(newHead)
            placeFood()
            score++
            scoreUpdateListener?.invoke(score)
        } else {
            snake.addFirst(newHead)
            snake.removeLast()
        }

        if (newHead.first < 0 || newHead.first >= widthInGrids ||
            newHead.second < 0 || newHead.second >= heightInGrids ||
            snake.drop(1).contains(newHead)) {
            stopGame()
            startGame()
        }
    }

    private fun placeFood() {
        do {
            food = Pair(Random().nextInt(widthInGrids), Random().nextInt(heightInGrids))
        } while (snake.contains(food))
    }

    private fun drawGame() {
        val canvas = holder.lockCanvas() ?: return
        canvas.drawColor(Color.BLACK)

        paint.color = Color.RED
        canvas.drawRect(
            food.first * gridSize.toFloat(),
            food.second * gridSize.toFloat(),
            (food.first + 1) * gridSize.toFloat(),
            (food.second + 1) * gridSize.toFloat(),
            paint
        )

        paint.color = Color.GREEN
        for (segment in snake) {
            canvas.drawRect(
                segment.first * gridSize.toFloat(),
                segment.second * gridSize.toFloat(),
                (segment.first + 1) * gridSize.toFloat(),
                (segment.second + 1) * gridSize.toFloat(),
                paint
            )
        }

        holder.unlockCanvasAndPost(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                handleSwipe(touchStartX, touchStartY, event.x, event.y)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleSwipe(startX: Float, startY: Float, endX: Float, endY: Float) {
        val deltaX = endX - startX
        val deltaY = endY - startY

        if (abs(deltaX) > swipeThreshold || abs(deltaY) > swipeThreshold) {
            if (abs(deltaX) > abs(deltaY)) {
                if (deltaX > 0 && direction != Direction.LEFT) direction = Direction.RIGHT
                else if (deltaX < 0 && direction != Direction.RIGHT) direction = Direction.LEFT
            } else {
                if (deltaY > 0 && direction != Direction.UP) direction = Direction.DOWN
                else if (deltaY < 0 && direction != Direction.DOWN) direction = Direction.UP
            }
        }
    }

    fun handleTouchEvent(x: Float, y: Float) {
        // 預留未來觸控功能
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
