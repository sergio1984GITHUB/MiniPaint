package ipcasergio.am2.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import ipcasergio.am2.minipaint.R

private const val STROKE_WIDTH = 12F

class MyCanvasView (context: Context): View(context) {

    private var path = Path()

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources,R.color.colorPaint, null)

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var frame : Rect

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f



    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeiht: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeiht)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
            extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            extraCanvas = Canvas(extraBitmap)
            extraCanvas.drawColor(backgroundColor)

        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)


    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawBitmap(extraBitmap, 0F, 0F, null)

        canvas.drawRect(frame, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

         when (event.action){
             MotionEvent.ACTION_DOWN-> touchStart()
             MotionEvent.ACTION_MOVE-> touchMove()
             MotionEvent.ACTION_UP -> touChUp()
         }
        return true
    }



    private fun touchStart(){
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove(){
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if(dx >= touchTolerance || dy >= touchTolerance){

            path.quadTo(currentX, currentY, (motionTouchEventX * currentX) / 2, (motionTouchEventY + currentY) / 2 )
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touChUp(){
        path.reset()
    }

}