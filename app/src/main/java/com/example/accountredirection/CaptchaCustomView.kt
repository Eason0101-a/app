package com.example.accountredirection // 確保這個套件名稱與您的專案相符

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

// 自訂驗證碼的 View (繼承自 View)
// @JvmOverloads 讓這個類別在 Java 或 Kotlin 呼叫時能自動帶入預設值
class CaptchaCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, // XML 中定義的屬性
    defStyleAttr: Int = 0        // 預設樣式
) : View(context, attrs, defStyleAttr) {

    // 繪製文字的畫筆
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 70f     // 字體大小
        strokeWidth = 3f   // 筆畫粗細
    }

    // 繪製干擾線的畫筆
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 5f
    }

    // 驗證碼文字
    private var captchaText: String = ""
    // 每個字母對應的顏色
    private val charColors = mutableListOf<Int>()

    // 設定驗證碼文字，並為每個字母隨機分配顏色
    fun setCaptchaText(text: String) {
        captchaText = text
        charColors.clear()
        for (i in text.indices) {
            charColors.add(generateRandomColor()) // 產生隨機顏色
        }
        invalidate() // 重新繪製畫面
    }

    // 產生隨機顏色 (R/G/B 在 0~200 之間，避免太亮或太暗)
    private fun generateRandomColor(): Int {
        val red = Random.nextInt(200)
        val green = Random.nextInt(200)
        val blue = Random.nextInt(200)
        return Color.rgb(red, green, blue)
    }

    // 繪製內容
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 如果沒有驗證碼字串，直接跳出
        if (captchaText.isEmpty()) {
            return
        }

        // 計算每個字母的寬度
        val charWidth = textPaint.measureText("W")
        // 計算整段驗證碼的總寬度
        val totalTextWidth = textPaint.measureText(captchaText)
        // 計算起始 X (讓文字水平置中)
        val startX = (width - totalTextWidth) / 2f

        var currentX = startX
        // 一個一個字母繪製
        for (i in captchaText.indices) {
            val char = captchaText[i]
            val charColor = charColors.getOrElse(i) { generateRandomColor() }

            canvas.save() // 儲存當前畫布狀態

            // 隨機旋轉 (-10° ~ +10°)
            val rotation = Random.nextFloat() * 20 - 10
            canvas.rotate(rotation, currentX + charWidth / 2, height / 2f)

            // 隨機垂直偏移 (-10 ~ +10)
            val offsetY = Random.nextFloat() * 20 - 10
            canvas.translate(0f, offsetY)

            // 設定字體顏色並繪製文字
            textPaint.color = charColor
            canvas.drawText(
                char.toString(),
                currentX,
                height / 2f + textPaint.textSize / 3, // 調整 Y 讓字母置中
                textPaint
            )

            canvas.restore() // 還原畫布狀態

            // 移動到下一個字母位置 (並隨機加入一點空隙)
            currentX += charWidth + Random.nextFloat() * 10
        }

        // 隨機畫 2~3 條干擾線
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
}
