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

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.data?.pathSegments?.first() == "w")
            openInBrowser(intent.data!!)
        else if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hasShownOnboarding", false))
            startActivity(Intent(this, MainActivity::class.java))
        else startActivity(Intent(this, OnBoardingActivity::class.java))
        finish()
    }

    private fun openInBrowser(uri: Uri) {
        setDeepLinkingState(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)

        CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setInstantAppsEnabled(false)
                .build()
                .launchUrl(this, uri)

        setDeepLinkingState(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
    }

    private fun setDeepLinkingState(state: Int) {
        applicationContext.packageManager.setComponentEnabledSetting(
                ComponentName(packageName, "$packageName.SplashActivity"),
                state,
                PackageManager.DONT_KILL_APP
        )
    }
}
