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

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.AudioManager.FLAG_SHOW_UI
import android.media.AudioManager.STREAM_MUSIC
import android.media.SoundPool


/**
 * Created by maartendegoede on 2019-03-31.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */
object ClapSoundManager {

    private val claps = listOf(
        R.raw.clapp01, R.raw.clapp11, R.raw.clapp21,
        R.raw.clapp02, R.raw.clapp12, R.raw.clapp22,
        R.raw.clapp03, R.raw.clapp13, R.raw.clapp23,
        R.raw.clapp04, R.raw.clapp14, R.raw.clapp24,
        R.raw.clapp05, R.raw.clapp15, R.raw.clapp25,
        R.raw.clapp06, R.raw.clapp16, R.raw.clapp26,
        R.raw.clapp07, R.raw.clapp17, R.raw.clapp27,
        R.raw.clapp08, R.raw.clapp18, R.raw.clapp28,
        R.raw.clapp09, R.raw.clapp19, R.raw.clapp29,
        R.raw.clapp10, R.raw.clapp20, R.raw.clapp30
    )
    private val widgetClaps by lazy { claps.subList(0, 4) }

    private val playableClaps = claps.associateBy({ it }, { null }).toMutableMap<Int, Int?>()
    private val audioManager by lazy { clappAppInstance.getSystemService(AUDIO_SERVICE) as AudioManager }
    private val soundPool = SoundPool(30, STREAM_MUSIC, 100).apply {
        widgetClaps.forEach { playableClaps[it] = load(clappAppInstance, it, 1) }
    }

    fun loadAll() {
        soundPool.apply {
            claps.forEach { playableClaps[it] = load(clappAppInstance, it, 1) }
        }
    }

    fun terminate() {
        soundPool.release()
    }

    fun playClap(fromWidget: Boolean = false) {
        if (audioManager.getStreamVolume(STREAM_MUSIC) == 0) {
            audioManager.setStreamVolume(STREAM_MUSIC, audioManager.getStreamMaxVolume(STREAM_MUSIC), FLAG_SHOW_UI)
        }

        val clap = (if (fromWidget) widgetClaps else claps).random()
        val sound = playableClaps[clap] ?: return
        playableClaps[clap] = sound
        soundPool.play(
            sound,
            1f, // normal leftVolume
            1f, // normal rightVolume
            1, // priority
            0, // no loop
            1f // normal playback rate
        )
    }
}
