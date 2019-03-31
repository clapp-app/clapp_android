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

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_onboarding.*


class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        button_start.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("hasShownOnboarding", true)
                    .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        background.setImageBitmap(blurRenderScript(BitmapFactory.decodeResource(resources, R.drawable.clapp_banner)))
    }

    private fun blurRenderScript(smallBitmap: Bitmap): Bitmap {
        if (Build.VERSION.SDK_INT < 17) return smallBitmap

        val output = Bitmap.createBitmap(smallBitmap.width, smallBitmap.height, Bitmap.Config.ARGB_8888)

        val renderScript = RenderScript.create(this)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        val outAlloc = Allocation.createFromBitmap(renderScript, output)
        script.apply {
            setRadius(2f)
            setInput(Allocation.createFromBitmap(renderScript, smallBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE))
            forEach(outAlloc)
        }
        outAlloc.copyTo(output)

        renderScript.destroy()

        return output
    }
}
