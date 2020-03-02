/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.summoningpixeldungeon.mechanics.miniontiers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

//simple json storage for tier's json data
public class TierStorage {

    public static JSONObject DATA;

    public static void initTiers() throws IOException, JSONException {
        FileHandle file = Gdx.files.internal("tiers.json");

        if (file.exists()){
            InputStream stream = file.read();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            DATA = (JSONObject)new JSONTokener( reader.readLine() ).nextValue();
            reader.close();
            stream.close();

        } else {
            throw new FileNotFoundException("file not found: " + file.path());
        }
    }

    public static JSONObject getTierInfo(String minionName) throws JSONException {
        return DATA.getJSONObject(minionName);
    }
}
