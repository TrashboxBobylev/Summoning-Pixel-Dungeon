/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.badlogic.gdx.backends.android.AndroidClipboard;

import java.io.IOException;
import java.io.InputStreamReader;

public class LogHandling {
    public static boolean extractLogToFile(){
        PackageManager manager = AndroidLauncher.instance.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(AndroidLauncher.instance.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER)){
            model = Build.MANUFACTURER + " " + model;
        }

        InputStreamReader reader = null;
        try {
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            StringBuilder out = new StringBuilder();
            out.append("Android version: ").append(Build.VERSION.SDK_INT).append("\n");
            out.append("Device: ").append(model).append("\n");
            out.append("Mod version: ").append(info == null ? "(undefined" : info.versionCode).append("\n");
            char[] buffer = new char[10000];

            int charRead;
            while((charRead = reader.read(buffer, 0, buffer.length)) > 0 ) {
                out.append(buffer, 0, charRead);
            }
            AndroidClipboard androidClipboard = new AndroidClipboard(AndroidLauncher.instance.getContext());
            androidClipboard.setContents(out.toString());

            reader.close();
        } catch (IOException e) {
            if (reader != null) {
                try {reader.close();} catch (IOException ignored) {}
            }
            e.printStackTrace();

            return false;
        }
        return true;
    }
}
