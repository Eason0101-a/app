package com.example.accountredirection // 確保這個套件名稱與您的專案相符

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
import kotlin.random.Random

// 自訂驗證碼的 View (繼承自 View)
// @JvmOverloads 讓這個類別在 Java 或 Kotlin 呼叫時能自動帶入預設值
class CaptchaCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, // XML 中定義的屬性
    defStyleAttr: Int = 0        // 預設樣式
) : View(context, attrs, defStyleAttr) {

    // --- 屬性 ---

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 70f     // 字體大小
        strokeWidth = 3f   // 筆畫粗細
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 5f
    }

    private var captchaText: String = ""
    private val charColors = mutableListOf<Int>()

    // --- View 生命週期 ---

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (captchaText.isEmpty() || width == 0 || height == 0) {
            return
        }

        drawCaptchaText(canvas)
        drawInterferenceLines(canvas)
    }

    // --- 公開 API ---

    fun setCaptchaText(text: String) {
        captchaText = text
        charColors.clear()
        for (i in text.indices) {
            charColors.add(generateRandomColor())
        }
        invalidate() // 觸發 onDraw
    }

    // --- 私有輔助方法 ---

    private fun drawCaptchaText(canvas: Canvas) {
        // Pre-calculate widths for each character
        val charWidths = captchaText.map { textPaint.measureText(it.toString()) }
        
        // Create N-1 random spacings for N characters
        val spacings = if (captchaText.length > 1) {
            List(captchaText.length - 1) { Random.nextFloat() * 10 }
        } else {
            emptyList()
        }

        val totalTextWidth = charWidths.sum() + spacings.sum()
        val startX = (width - totalTextWidth) / 2f
        var currentX = startX

        for (i in captchaText.indices) {
            val char = captchaText[i]
            val charString = char.toString()
            val charWidth = charWidths[i]
            val charColor = charColors.getOrElse(i) { generateRandomColor() }

            canvas.withSave {
                // 隨機旋轉
                val rotation = Random.nextFloat() * 20 - 10
                rotate(rotation, currentX + charWidth / 2, height / 2f)

                // 隨機垂直偏移
                val offsetY = Random.nextFloat() * 20 - 10
                translate(0f, offsetY)

                // 繪製文字
                textPaint.color = charColor
                drawText(
                    charString,
                    currentX,
                    height / 2f + textPaint.textSize / 3,
                    textPaint
                )
            }
            
            currentX += charWidth
            if (i < spacings.size) {
                currentX += spacings[i]
            }
        }
    }

    private fun drawInterferenceLines(canvas: Canvas) {
        val numLines = Random.nextInt(2, 4)
        for (i in 0 until numLines) {
            linePaint.color = generateRandomColor()
            val startLineX = Random.nextFloat() * width
            val startLineY = Random.nextFloat() * height
            val endLineX = Random.nextFloat() * width
            val endLineY = Random.nextFloat() * height
            canvas.drawLine(startLineX, startLineY, endLineX, endLineY, linePaint)
        }
    }

    private fun generateRandomColor(): Int {
        val red = Random.nextInt(200)
        val green = Random.nextInt(200)
        val blue = Random.nextInt(200)
        return Color.rgb(red, green, blue)
    }
}
