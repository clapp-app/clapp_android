/*
 *    Copyright 2019 Maarten de Goede
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

package eu.insertcode.clapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val vibrator by lazy { this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_clap.setOnTouchListener(object : View.OnTouchListener {
            private var handler: Handler? = null
            private val r = Runnable { clap() }

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> true.also {
                        handler = handler ?: Handler().apply { post(r) }
                    }
                    MotionEvent.ACTION_UP -> true.also {
                        handler?.removeCallbacks(r)
                        handler = null
                    }
                    else -> false
                }
            }

            @Suppress("DEPRECATION")
            fun clap() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(10, 255))
                else vibrator.vibrate(50)
                ClapSoundManager.playClap()
                button_clap.startAnimation(scaleAnimation)

                handler?.postDelayed(r, 200)
            }
        })

    }

    val scaleAnimation = AnimationSet(true).apply {
        val fromScale = 1f
        val toScale = 2f
        val growAnimation = ScaleAnimation(
                fromScale, toScale, fromScale, toScale,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
        )
        val shrinkAnimation = ScaleAnimation(
                toScale, fromScale, toScale, fromScale,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f
        )

        addAnimation(growAnimation)
        addAnimation(shrinkAnimation)
        duration = 200
    }
}
