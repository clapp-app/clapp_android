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

import android.graphics.drawable.Drawable
import android.view.Menu
import androidx.annotation.ColorRes
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