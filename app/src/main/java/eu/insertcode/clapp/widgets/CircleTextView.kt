/*
 *    Copyright 2020 Maarten de Goede
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.insertcode.clapp.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import eu.insertcode.clapp.R
import kotlin.math.max
import kotlin.math.min


/**
 * Created by maartendegoede on 2019-03-24.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */
class CircleTextView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
) : View(context, attrs, defStyleAttr) {

    // http://stackoverflow.com/questions/19043452/how-to-alter-the-output-of-android-canvas-drawtextonpath
    // https://blog.upcurve.co/how-to-create-a-gradient-textview-in-android-c21331da86ab
    // https://hemantvc.blogspot.com/2016/10/text-curve-clockwise-and-anticlockwise_95.html

    private val textsize: Float
    private val startColor: Int
    private val endColor: Int
    private val textPath by lazy { Path() }
    var text: String = ""
        set(value) {
            field = value.replace("\n", " ")
        }
    var textRadius = 0
        set(value) {
            field = min(360, max(-360, value))
            invalidate()
        }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView)

        startColor = a.getColor(R.styleable.CircleTextView_startColor, 0x000000)
        endColor = a.getColor(R.styleable.CircleTextView_endColor, 0x000000)

        textsize = a.getDimensionPixelSize(R.styleable.CircleTextView_textSize, 0).toFloat()
        textRadius = a.getInt(R.styleable.CircleTextView_textRadius, 90)
        text = a.getString(R.styleable.CircleTextView_text) ?: ""

        a.recycle()
    }

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = if (textsize == 0f) 50f else textsize
            shader = LinearGradient(0f, height / 2f, width.toFloat(), height / 2f,
                    startColor, endColor, Shader.TileMode.CLAMP)
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val circumference = (paint.measureText(text) + paint.strokeWidth * 2f) * 360f / Math.abs(textRadius)
        val diameter = circumference / Math.PI.toFloat()


        val startAngle = (if (textRadius > 0) 270 else 90) - textRadius / 2
        val centerX = (width) * .5f
        val centerY = (height) * .5f

        val left = centerX - diameter / 2f
        var top = centerY
        if (textRadius <= 0) {
            top += (height - 2f * centerY - diameter - 10)
            if (paint.strokeWidth > 0f)
                top -= paint.strokeWidth
        }
        textPath.reset()
        val right = left + diameter
        val bottom = top + diameter

        val rectf = RectF(left, top, right, bottom)
        textPath.addArc(rectf, startAngle.toFloat(), textRadius.toFloat())
        canvas.drawTextOnPath(text, textPath, paint.strokeWidth, 0.0f, paint)
    }
}