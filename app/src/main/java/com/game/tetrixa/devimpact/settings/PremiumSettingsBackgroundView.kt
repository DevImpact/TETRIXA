package com.game.tetrixa.devimpact.settings

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.ColorUtils
import kotlin.math.sin

class PremiumSettingsBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress = 0f
    private var scrollOffset = 0f

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 9000L
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        interpolator = LinearInterpolator()
        addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!animator.isStarted) animator.start()
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }

    fun setParallaxOffset(offset: Int) {
        scrollOffset = offset * 0.08f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat().coerceAtLeast(1f)
        val h = height.toFloat().coerceAtLeast(1f)
        val start = ColorUtils.blendARGB(Color.rgb(16, 26, 69), Color.rgb(44, 32, 111), progress)
        val middle = ColorUtils.blendARGB(Color.rgb(35, 60, 136), Color.rgb(108, 75, 206), progress)
        val end = ColorUtils.blendARGB(Color.rgb(7, 16, 39), Color.rgb(11, 18, 43), progress)
        paint.shader = LinearGradient(0f, -scrollOffset, w, h + scrollOffset, intArrayOf(start, middle, end), floatArrayOf(0f, 0.46f, 1f), Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, w, h, paint)

        drawGlow(canvas, w * (0.14f + progress * 0.22f), h * 0.12f - scrollOffset, w * 0.62f, Color.argb(112, 255, 216, 107))
        drawGlow(canvas, w * 0.86f, h * (0.22f + progress * 0.18f) - scrollOffset, w * 0.68f, Color.argb(92, 108, 75, 206))
        drawGlow(canvas, w * 0.42f, h * 0.86f - scrollOffset, w * 0.76f, Color.argb(76, 47, 230, 184))

        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.4f
        paint.color = Color.argb(34, 255, 216, 107)
        for (i in 0 until 7) {
            val top = h * (0.1f + i * 0.13f) - scrollOffset * 0.4f
            canvas.drawLine(w * 0.08f, top, w * 0.92f, top + sin(i + progress * 3.14f) * 18f, paint)
        }

        particlePaint.style = Paint.Style.FILL
        for (i in 0 until 36) {
            val phase = (i * 0.173f + progress) % 1f
            val x = ((i * 73) % 100) / 100f * w + sin((phase + i) * 6.28f) * 14f
            val y = ((i * 47) % 100) / 100f * h - scrollOffset + sin((phase * 2f + i) * 6.28f) * 20f
            particlePaint.color = if (i % 5 == 0) Color.argb(72, 255, 216, 107) else Color.argb(34 + (i % 4) * 10, 255, 255, 255)
            canvas.drawCircle(x, y, 1.8f + (i % 3), particlePaint)
        }
    }

    private fun drawGlow(canvas: Canvas, cx: Float, cy: Float, radius: Float, color: Int) {
        glowPaint.shader = RadialGradient(cx, cy, radius, color, Color.TRANSPARENT, Shader.TileMode.CLAMP)
        canvas.drawCircle(cx, cy, radius, glowPaint)
        glowPaint.shader = null
    }
}
