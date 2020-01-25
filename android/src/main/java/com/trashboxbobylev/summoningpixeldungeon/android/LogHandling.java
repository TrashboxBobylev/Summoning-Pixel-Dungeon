/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.summoningpixeldungeon.android;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogHandling {
    public static String extractLogToFile(){
        PackageManager manager = AndroidLauncher.instance.getPackageManager();
        PackageInfo info = null;
        try {
            manager.getPackageInfo(AndroidLauncher.instance.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER)){
            model = Build.MANUFACTURER + " " + model;
        }
        String path = Environment.getExternalStorageDirectory() + "/" + "SummPDCrashes/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String fullName = path + sdf.format(new Date());

        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model +"\n");
            writer.write("Mod version: " + (info == null ? "(undefined" : info.versionCode) + "\n");
            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1) break;
                writer.write(buffer, 0, n);
            } while (true);

            writer.close();
            reader.close();
        } catch (IOException e) {
            if (writer != null) {
                try {writer.close();} catch (IOException ignored) {}
            }
            if (reader != null) {
                try {reader.close();} catch (IOException ignored) {}
            }

            return null;
        }
        return fullName;
    }
}
