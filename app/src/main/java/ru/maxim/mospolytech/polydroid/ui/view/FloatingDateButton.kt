package ru.maxim.mospolytech.polydroid.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.maxim.mospolytech.polydroid.R
import ru.maxim.mospolytech.polydroid.util.DateFormatUtils

class FloatingDateButton(context: Context, attrs: AttributeSet?) : FloatingActionButton(context, attrs) {

    constructor(context: Context) : this(context, null)

    private val datePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.dateButtonTextColor)
        textSize = dpToPx(20F)
    }
    private val monthPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.dateButtonTextColor)
        textSize = dpToPx(16F)
    }

    private val day = DateFormatUtils.getCurrentDay()
    private val month = DateFormatUtils.getCurrentMonth()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(day, width/2F - datePaint.measureText(day)/2, height/2F, datePaint)
        canvas?.drawText(month, width/2F - monthPaint.measureText(month)/2, height/2F + dpToPx(16F), monthPaint)
    }

    private fun dpToPx(dp: Float): Float =
        applyDimension(COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}