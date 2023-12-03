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

class PieChart(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private var size = 0
    private var animator: ValueAnimator? = null
    private var currentSweepAngle = 0f
    private var borderSize = 0f
    private var borderColor = Color.parseColor("#000000")
    private var rectF: RectF = RectF(0f, 0f, 0f, 0f)

    companion object {
        const val PIE_CHART_START_ANGLE = -90f
        const val PIE_CHART_ANIMATION_DURATION = 650L
    }

    private var pieChartList = mutableListOf<Slice>()
    private var sliceLabels = mutableListOf<String>()
    private val labelTextSize = resources.getDimensionPixelSize(R.dimen.bar_chart_label_text_size).toFloat()

    init {
        paint.isAntiAlias = true
        paint.textSize = labelTextSize
        computeSlices()
    }

    fun submitList(
        pieChartList: ArrayList<Slice>,

        ) {
        this.pieChartList = pieChartList
        computeSlices()
    }

    fun setBorder(
        borderSize: Float = this.borderSize,
        borderColor: Int = this.borderColor,
    ) {
        this.borderSize = borderSize
        this.borderColor = borderColor
        startPieChartAnimation()
    }

    private fun computeSlices() {
        if (pieChartList.isEmpty()) return
        var startAngle = 0f

        sliceLabels.clear()

        for (i in 0 until pieChartList.size) {
            val percent = pieChartList[i].percentage
            val sweepSize = 3.6f * percent
            pieChartList[i].startAngle = startAngle
            pieChartList[i].sweepSize = sweepSize

            // Generar una etiqueta para cada segmento basada en el índice
            sliceLabels.add(String.format("%.2f",percent)+"%")

            startAngle += sweepSize
        }
        startPieChartAnimation()
    }

    private fun startPieChartAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = PIE_CHART_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPieChart(canvas)
    }

    private fun drawPieChart(canvas: Canvas) {

        paint.style = Paint.Style.FILL
        var temp = 0f
        for ((index, slice) in pieChartList.withIndex()) {
            if (currentSweepAngle >= slice.startAngle) {
                paint.color = slice.color
                canvas.drawArc(
                    rectF,
                    PIE_CHART_START_ANGLE + temp,
                    currentSweepAngle - slice.startAngle,
                    true,
                    paint
                )
                temp += slice.sweepSize


            }
        }
        for ((index, slice) in pieChartList.withIndex()) {
            val totalCanvasWidth = width.toFloat() - 15

            // Calcular el espacio entre las etiquetas
            val labelSpacing = totalCanvasWidth / pieChartList.size

            // Calcular la posición X de la etiqueta
            val labelX = labelSpacing * index + labelSpacing / 2

            val labelY = rectF.bottom  // Espaciado para las etiquetas
            paint.color = Color.BLACK
            canvas.drawText(sliceLabels[index], labelX, labelY, paint)
        }
        if (borderSize > 0f) {
            val radius = size / 2f - borderSize
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize
            canvas.drawCircle(size / 2f, size / 2f, radius, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF = RectF(
            (measuredWidth - size + borderSize) / 2f,
            (measuredHeight - size + borderSize) / 2f,
            (measuredWidth + size - borderSize) / 2f,
            (measuredHeight + size - borderSize) / 2f,
        )
    }

}