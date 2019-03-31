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

import android.media.MediaPlayer

/**
 * Created by maartendegoede on 2019-03-31.
 * Copyright © 2019 insertCode.eu. All rights reserved.
 */
object ClapSoundManager {

    private val claps = arrayListOf(
            R.raw.clapp01, R.raw.clapp21, R.raw.clapp41,
            R.raw.clapp02, R.raw.clapp22, R.raw.clapp42,
            R.raw.clapp03, R.raw.clapp23, R.raw.clapp43,
            R.raw.clapp04, R.raw.clapp24, R.raw.clapp44,
            R.raw.clapp05, R.raw.clapp25, R.raw.clapp45,
            R.raw.clapp06, R.raw.clapp26, R.raw.clapp46,
            R.raw.clapp07, R.raw.clapp27, R.raw.clapp47,
            R.raw.clapp08, R.raw.clapp28, R.raw.clapp48,
            R.raw.clapp09, R.raw.clapp29, R.raw.clapp49,
            R.raw.clapp10, R.raw.clapp30, R.raw.clapp50,
            R.raw.clapp11, R.raw.clapp31, R.raw.clapp51,
            R.raw.clapp12, R.raw.clapp32, R.raw.clapp52,
            R.raw.clapp13, R.raw.clapp33, R.raw.clapp53,
            R.raw.clapp14, R.raw.clapp34, R.raw.clapp54,
            R.raw.clapp15, R.raw.clapp35, R.raw.clapp55,
            R.raw.clapp16, R.raw.clapp36, R.raw.clapp56,
            R.raw.clapp17, R.raw.clapp37, R.raw.clapp57,
            R.raw.clapp18, R.raw.clapp38, R.raw.clapp58,
            R.raw.clapp19, R.raw.clapp39, R.raw.clapp59,
            R.raw.clapp20, R.raw.clapp40, R.raw.clapp60,
            R.raw.clapp61
    )

    private val playableClaps = claps.map { MediaPlayer.create(clappAppInstance, it) }


    fun terminate() {
        playableClaps.forEach { it.release() }
    }

    fun playClap() {
        playableClaps.random().start()
    }
}
