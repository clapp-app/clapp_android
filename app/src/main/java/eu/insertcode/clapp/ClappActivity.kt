/*
 *    Copyright 2023 Maarten de Goede
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
import android.content.Intent
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import eu.insertcode.clapp.databinding.ActivityMainBinding
import eu.insertcode.clapp.extensions.SimpleOnSeekBarChangeListener
import eu.insertcode.clapp.extensions.getColorCompat
import eu.insertcode.clapp.extensions.openInBrowser
import eu.insertcode.clapp.extensions.tintMenuItemsCompat
import kotlin.math.sqrt


class ClappActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val vibrator by lazy { this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    private var clapSpeed = 200L

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(clappAppInstance.themeId)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        ClapSoundManager.loadAll()

        volumeControlStream = AudioManager.STREAM_MUSIC

        binding.seekbar.progressDrawable.setColorFilter(getColorCompat(R.color.colorSecondary), PorterDuff.Mode.SRC_IN)
        binding.seekbar.thumb.setColorFilter(getColorCompat(R.color.colorSecondary), PorterDuff.Mode.SRC_IN)
        binding.seekbar.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // (progress-max)*-1+min, calculation explained in view
                clapSpeed = ((progress - binding.seekbar.max) * -1 + 100).toLong()
            }
        })

        binding.buttonClap.setOnTouchListener(object : View.OnTouchListener {
            private var handler: Handler? = null
            private val r = Runnable { clap() }

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> true.also {
                        handler = (handler ?: Handler(Looper.getMainLooper())).apply { post(r) }
                    }

                    MotionEvent.ACTION_UP -> true.also {
                        handler?.removeCallbacks(r)
                        handler = null
                    }

                    else -> false
                }
            }

            fun clap() {
                performClap()

                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    handler?.postDelayed(r, clapSpeed)
                } else {
                    handler?.removeCallbacks(r)
                    handler = null
                }
            }
        })


        val sensorService = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorService.registerListener(object : SensorEventListener {
            /*
             * The gForce that is necessary to register as shake.
             * Must be greater than 1G (one earth gravity unit).
             * You can install "G-Force", by Blake La Pierre
             * from the Google Play Store and run it to see how
             *  many G's it takes to register a shake
             */
            private val SHAKE_THRESHOLD_GRAVITY = 2.7f
            private var shakeTimestamp = 0L

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val gX = x / SensorManager.GRAVITY_EARTH
                    val gY = y / SensorManager.GRAVITY_EARTH
                    val gZ = z / SensorManager.GRAVITY_EARTH

                    // gForce will be close to 1 when there is no movement.
                    val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

                    if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                        val now = System.currentTimeMillis()
                        // ignore shake events too close to each other (500ms)
                        if (shakeTimestamp + clapSpeed > now)
                            return

                        shakeTimestamp = now

                        performClap()
                    }
                }
            }
        }, sensorService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
    }

    private val scaleAnimation = AnimationSet(true).apply {
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
    }

    fun performClap() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createOneShot(10, 255))
        else vibrator.vibrate(50)
        ClapSoundManager.playClap()
        binding.buttonClap.startAnimation(scaleAnimation.apply { duration = clapSpeed })
    }


    override fun onCreateOptionsMenu(menu: Menu) = true.also {
        menuInflater.inflate(R.menu.menu_style, menu)
        menu.findItem(R.id.menu_theme_light).isVisible = clappAppInstance.isDarkTheme
        menu.findItem(R.id.menu_theme_dark).isVisible = !clappAppInstance.isDarkTheme
        menu.tintMenuItemsCompat()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menu_share -> true.also {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, getString(R.string.str_share))
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)))
                }

                R.id.menu_theme_dark -> true.also {
                    clappAppInstance.setCurrentTheme(R.style.AppTheme_Dark, true)
                    recreate()
                }

                R.id.menu_theme_light -> true.also {
                    clappAppInstance.setCurrentTheme(R.style.AppTheme_Light, false)
                    recreate()
                }

                R.id.menu_privacy_policy -> true.also {
                    openInBrowser(Uri.parse(getString(R.string.url_privacy_policy)))
                }

                else -> super.onOptionsItemSelected(item)
            }
}
