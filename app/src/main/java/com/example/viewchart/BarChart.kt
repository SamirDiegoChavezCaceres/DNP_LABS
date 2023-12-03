package com.example.viewchart
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.min

class BarChart(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private var barWidth = 0f
    private var barSpacing = 0f
    private var animator: ValueAnimator? = null
    private var currentBarHeight = 0f
    private var borderSize = 0f
    private var borderColor = Color.parseColor("#000000")
    private var maxBarHeight = 0f
    private var maxHeightCanvas = 0f // Nuevo campo para almacenar la altura máxima permitida en el canvas

    companion object {
        const val BAR_CHART_ANIMATION_DURATION = 650L
    }

    private var barChartList = mutableListOf<Bar>()
    private var barLabels = mutableListOf<String>()
    private val labelTextSize = resources.getDimensionPixelSize(R.dimen.bar_chart_label_text_size).toFloat()

    init {
        paint.isAntiAlias = true
        paint.textSize = labelTextSize
        computeBars()
    }

    fun submitList(barChartList: ArrayList<Bar>) {
        this.barChartList = barChartList
        computeBars()
    }

    fun setBorder(borderSize: Float = this.borderSize, borderColor: Int = this.borderColor) {
        this.borderSize = borderSize
        this.borderColor = borderColor
        startBarChartAnimation()
    }

    private fun computeBars() {
        if (barChartList.isEmpty()) return

        maxBarHeight = barChartList.maxByOrNull { it.height }?.height ?: 0f

        // Calcular la altura máxima permitida en el canvas (ajustada a la altura de la vista)
        maxHeightCanvas = height.toFloat()

        // Calcular el factor de escala basado en la altura máxima permitida en el canvas
        val scale = if (maxBarHeight > maxHeightCanvas) maxHeightCanvas / maxBarHeight else 1f

        val totalBars = barChartList.size
        barWidth = (width - (totalBars + 1) * barSpacing) / totalBars

        // Aplicar el factor de escala a la altura máxima actual de las barras
        currentBarHeight = maxBarHeight * scale
        barLabels.clear()
        barChartList.forEach { bar ->
            val scaledBarHeight = bar.height * (maxHeightCanvas / maxBarHeight)
            val barHeight = (scaledBarHeight / maxBarHeight * currentBarHeight)
            val labelText = String.format("%.2f", bar.height) // Formatear el valor de la altura
            barLabels.add(labelText)
        }
        startBarChartAnimation()
    }

    private fun startBarChartAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = BAR_CHART_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentBarHeight = valueAnimator.animatedValue as Float * maxBarHeight
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBarChart(canvas)
    }

    private fun drawBarChart(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        var left = barSpacing

        for ((index, bar) in barChartList.withIndex()) {
            paint.color = bar.color

            val scaledBarHeight = bar.height * (maxHeightCanvas / maxBarHeight)
            val barHeight = (scaledBarHeight / maxBarHeight * currentBarHeight)

            canvas.drawRect(left, height - barHeight, left + barWidth, height.toFloat(), paint)

            // Dibujar etiquetas debajo de cada barra
            val labelX = left + barWidth / 2 - 50
            val labelY = height.toFloat() - 15 // Espaciado para las etiquetas
            paint.color = Color.BLACK
            canvas.drawText(barLabels[index], labelX, labelY, paint)

            left += barWidth + barSpacing
        }

        if (borderSize > 0f) {
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize
            canvas.drawRect(
                borderSize / 2f,
                borderSize / 2f,
                (width - borderSize / 2f),
                (height - borderSize / 2f),
                paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        computeBars()
    }
}


