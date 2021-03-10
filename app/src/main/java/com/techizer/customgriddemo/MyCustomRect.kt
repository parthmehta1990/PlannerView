package com.techizer.customgriddemo

import android.R.bool
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


class MyCustomRect(context: Context, attrs: AttributeSet):View(context, attrs),ScaleGestureDetector.OnScaleGestureListener {

    var canvasWidth = 0
    var canvasHeight = 0
    var gridSize = 10f

    var ecart: Float = Math.max(1920, 1080) / gridSize

    //last j index value
    var lj = 0

    //last i index value
    var li = 0

    private var _scaleFactor = 0f
    private var _xoffset = 0f
    private var _yoffset = 0f

    var _prevx=0f
    var _prevy=0f

    var paint: Paint? = null
    var ArcPaint: Paint? = null
    var offset = 50
    var cornersRadius = 25
    init{
        paint = Paint()
        paint!!.setStyle(Paint.Style.FILL);
        paint!!.setColor(Color.RED);
        paint!!.setAntiAlias(true);

        ArcPaint = Paint()
        ArcPaint!!.setStyle(Paint.Style.FILL);
        ArcPaint!!.setColor(Color.BLUE);
       // ArcPaint!!.setAntiAlias(true);
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvasWidth = canvas!!.getWidth();
        canvasHeight = canvas!!.getHeight();

        canvas!!.drawColor(Color.CYAN)


            var j = 0
            while (j <= Math.min(canvasWidth, canvasHeight)) {
                var i = 0
                while (i <= Math.max(canvasWidth, canvasHeight)) {
                    li = i
                    i += ecart.toInt()
                }
                lj = j
                j += ecart.toInt()
            }

        canvas.clipRect(0, 0, lj, canvas.getHeight());

        run {
            var j = 0
            while (j <= Math.min(canvasWidth, canvasHeight)) {
                var i = 0
                while (i <= Math.max(canvasWidth, canvasHeight)) {

                    //horizontal lines
                    canvas.drawLine(j.toFloat(), i.toFloat(), i.toFloat(), i.toFloat(), paint!!)

                    //vertical lines
                    canvas.drawLine(j.toFloat(), i.toFloat(), j.toFloat(), j.toFloat(), paint!!)
                    i += ecart.toInt()
                }
                j += ecart.toInt()
            }
        }

        // Initialize a new RectF instance
        // Initialize a new RectF instance
        val rectF = RectF(
            offset.toFloat(),  // left
            offset.toFloat(),  // top
            (300).toFloat(),  // right
            (1000).toFloat() // bottom
        )

        // Finally, draw the rounded corners rectangle object on the canvas
        canvas.drawRoundRect(
            rectF, // rect
            cornersRadius.toFloat(), // rx
            cornersRadius.toFloat(), // ry
            paint!! // Paint
        );

        canvas.drawCircle(150.0f, 150.0f, 50.0f, ArcPaint!!)

        canvas!!.save();
        canvas.scale(_scaleFactor, _scaleFactor);//for zoom
        canvas.translate(_xoffset, _yoffset);//for pan
        //DO NORMAL DRAWING HERE
        canvas.restore();

        invalidate()
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        _scaleFactor *= detector!!.scaleFactor
        _scaleFactor = Math.max(0.1f, Math.min(_scaleFactor, 5.0f));
        invalidate();
        return true;
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true;
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                _prevx = e.x
                _prevy = e.y
            }
            MotionEvent.ACTION_UP -> {
                _xoffset += e.x - _prevx
                _yoffset += e.y - _prevy
                invalidate()
                _prevx = e.x
                _prevy = e.y
            }
        }

        println("Event"+e)
        invalidate()
        return true//_scaleGestureDetector.onTouchEvent(e)
    }

}