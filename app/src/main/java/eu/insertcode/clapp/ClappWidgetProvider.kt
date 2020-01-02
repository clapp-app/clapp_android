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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


class ClappWidgetProvider : AppWidgetProvider() {
    private val actionClapp = "ACTION_CLAPP"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(
                context.packageName,
                R.layout.clappwidget
            ).apply {
                setOnClickPendingIntent(
                    R.id.button_clap,
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(context, ClappWidgetProvider::class.java).also {
                            it.action = actionClapp
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == actionClapp) {
            ClapSoundManager.playClap(fromWidget = true)
        } else super.onReceive(context, intent)
    }
}