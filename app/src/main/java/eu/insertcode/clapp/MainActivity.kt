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
import android.content.Intent
import android.graphics.PorterDuff
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
import eu.insertcode.clapp.extensions.SimpleOnSeekBarChangeListener
import eu.insertcode.clapp.extensions.getColorCompat
import eu.insertcode.clapp.extensions.openInBrowser
import eu.insertcode.clapp.extensions.tintMenuItemsCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val vibrator by lazy { this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    private var clapSpeed = 200L

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(clappAppInstance.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        volumeControlStream = AudioManager.STREAM_MUSIC

        seekbar.progressDrawable.setColorFilter(getColorCompat(R.color.colorSecondary), PorterDuff.Mode.SRC_IN)
        seekbar.thumb.setColorFilter(getColorCompat(R.color.colorSecondary), PorterDuff.Mode.SRC_IN)
        seekbar.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // (progress-max)*-1+min, calculation explained in view
                clapSpeed = ((progress - seekbar.max) * -1 + 100).toLong()
            }
        })

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
                button_clap.startAnimation(scaleAnimation.apply { duration = clapSpeed })

                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    handler?.postDelayed(r, clapSpeed)
                } else {
                    handler?.removeCallbacks(r)
                    handler = null
                }
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
    }


    override fun onCreateOptionsMenu(menu: Menu) = true.also {
        menuInflater.inflate(R.menu.menu_style, menu)
        menu.tintMenuItemsCompat()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menu_share -> true.also {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, getString(R.string.str_share, getString(R.string.str_onboarding_description)))
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)))
                }
                R.id.menu_theme_dark -> true.also {
                    clappAppInstance.setCurrentTheme(R.style.AppTheme_Dark, true)
                    recreate()
                }
                R.id.menu_theme_black -> true.also {
                    clappAppInstance.setCurrentTheme(R.style.AppTheme_Black, true)
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
