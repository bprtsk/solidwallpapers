package com.simply.solidwallpaper.ui.canvas

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import com.simply.solidwallpaper.R
import kotlin.math.roundToInt


class WallpaperCanvas(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var paint: Paint = Paint()
    private var area: Rect = Rect()
    private var angle = 0
    private var colors = intArrayOf()
    private var linearGradient: LinearGradient? = null

    init {
        context?.theme?.obtainStyledAttributes(attrs, R.styleable.WallpaperCanvas, 0, 0)?.apply {
            try {
                paint.color = getInteger(R.styleable.WallpaperCanvas_firstColor, Color.WHITE)
            } finally {
                recycle()
            }
        }

        addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            area = Rect(left, top, right, bottom)
            buildShaderAndInvalidate()
        }
    }

    private fun buildShaderAndInvalidate() {

        if (colors.size > 1) {
            linearGradient = generateGradientByArea(area)
            paint.shader = linearGradient
        } else {
            paint.shader = null
            paint.color = colors[0]
        }

        invalidate()
    }

    private fun generateGradientByArea(area: Rect): LinearGradient? {
        val x0 : Int
        val y0 : Int
        val x1 : Int
        val y1 : Int

        when (angle){
            0 -> {
                x0 = area.centerX()
                y0 = area.bottom
                x1 = area.centerX()
                y1 = area.top
            }
            45-> {
                x0 = area.left
                y0 = area.bottom
                x1 = area.right
                y1 = area.top
            }
            90 ->{
                x0 = area.left
                y0 = area.centerY()
                x1 = area.right
                y1 = area.centerY()
            }
            135 -> {
                x0 = area.left
                y0 = area.top
                x1 = area.right
                y1 = area.bottom
            }
            180 -> {
                x0 = area.centerX()
                y0 = area.top
                x1 = area.centerX()
                y1 = area.bottom
            }
            225 -> {
                x0 = area.right
                y0 = area.top
                x1 = area.left
                y1 = area.bottom
            }
            270 -> {
                x0 = area.right
                y0 = area.centerY()
                x1 = area.left
                y1 = area.centerY()
            }
            315 -> {
                x0 = area.right
                y0 = area.bottom
                x1 = area.left
                y1 = area.top
            }
            else -> {
                x0 = 0
                y0 = 0
                x1 = 0
                y1 = 0
            }
        }

        return LinearGradient(
            x0.toFloat(),
            y0.toFloat(),
            x1.toFloat(),
            y1.toFloat(),
            this.colors,
            null,
            Shader.TileMode.MIRROR
        )
    }

    fun setColors(colors: MutableList<Int>?) {
        if (colors == null) {
            return
        }
        this.colors = colors.toIntArray()

        buildShaderAndInvalidate()
    }

    fun setAngle(angle: Int){
        this.angle = angle

        buildShaderAndInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(area, paint)
    }

    fun getWallpaperBitmap(): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width : Int = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val fullArea = Rect(0, 0, width, height)

        val fullPaint = Paint()
        if (colors.size > 1) {
            linearGradient = generateGradientByArea(area)
            fullPaint.shader = linearGradient
        } else {
            fullPaint.shader = null
            fullPaint.color = colors[0]
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawRect(fullArea, fullPaint)

        return bitmap
    }
}