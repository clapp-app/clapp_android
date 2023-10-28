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

package eu.insertcode.clapp

import android.app.Application
import android.preference.PreferenceManager
import androidx.annotation.StyleRes


/**
 * Created by maartendegoede on 2019-03-31.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */

lateinit var clappAppInstance: ClappApp

class ClappApp : Application() {
    enum class IconMode {
        Clapp, Peach
    }

    var themeId = R.style.AppTheme_Light
    var isDarkTheme = false
    var iconMode = IconMode.Clapp

    fun setCurrentTheme(@StyleRes theme: Int, isDark: Boolean) {
        themeId = theme
        isDarkTheme = isDark
        setTheme(themeId)
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putInt("defaultTheme", theme)
                .putBoolean("isDarkTheme", isDark)
                .apply()
    }

    fun setCurrentIconMode(iconMode: IconMode) {
        this.iconMode = iconMode
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("iconMode", iconMode.name)
                .apply()
    }

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            themeId = getInt("defaultTheme", themeId)
            isDarkTheme = getBoolean("isDarkTheme", isDarkTheme)
            iconMode = IconMode.entries.find { it.name == getString("iconMode", null) } ?: iconMode
            setTheme(themeId)
        }
        clappAppInstance = this
    }

    override fun onTerminate() {
        super.onTerminate()
        ClapSoundManager.terminate()
    }
}