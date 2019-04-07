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

package eu.insertcode.clapp.extensions

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Menu
import android.widget.SeekBar
import androidx.annotation.ColorRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import eu.insertcode.clapp.R
import eu.insertcode.clapp.clappAppInstance


/**
 * Created by maartendegoede on 2019-04-04.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */


/**
 * Workaround for a bug in MaterialComponents
 */
fun Menu.tintMenuItemsCompat() {
    for (i in 0 until size()) {
        getItem(i).icon?.setTintCompat(if (clappAppInstance.isDarkTheme) R.color.colorOnSurfaceDark else R.color.colorOnSurface)
    }
}

fun Drawable.setTintCompat(@ColorRes tint: Int) = DrawableCompat.setTint(this, getColorCompat(tint))
fun getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(clappAppInstance, color)


open class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

}


fun Activity.openInBrowser(uri: Uri) {
    setDeepLinkingState(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)

    CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setInstantAppsEnabled(false)
            .build()
            .launchUrl(this, uri)

    setDeepLinkingState(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
}

private fun Activity.setDeepLinkingState(state: Int) {
    applicationContext.packageManager.setComponentEnabledSetting(
            ComponentName(packageName, "$packageName.SplashActivity"),
            state,
            PackageManager.DONT_KILL_APP
    )
}