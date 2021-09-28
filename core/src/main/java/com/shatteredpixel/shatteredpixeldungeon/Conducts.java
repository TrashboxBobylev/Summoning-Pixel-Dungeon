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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Conducts {
    public enum Conduct {
        NULL,
        NO_ARMOR(1.1f),
        PACIFIST(1.7f),
        CRIPPLED(1.5f),
        NO_MAGIC(1.5f),
        ZEN(2.0f),
        BERSERK(1.03f),
        WRAITH(1.33f),
        SLEEPY(0.6f),
        TRANSMUTATION(1.66f),
        KING(1.33f),
        EVERYTHING(0f),
        EXPLOSIONS(1.2f),
        INVISIBLE(1.8f),
        REGENERATION(1.33f),
        UNKNOWN(1.4f),
        NO_STR(2f),
        CHAMPS(1.4f),
        NO_REGEN(1.4f),
        CURSE(1.33f),
        ALLSIGHT(1.33f);

        public float scoreMod;

        Conduct(){
            scoreMod = 1f;
        }

        Conduct(float scoreMod){
            this.scoreMod = scoreMod;
        }

        @Override
        public String toString() {
            return Messages.get(Conducts.class, this.name());
        }

        public String desc(){
            return Messages.get(Conducts.class, name() + "_desc") + "\n\n" + Messages.get(Dungeon.class, "score", new DecimalFormat("#.##").format(scoreMod));
        }
    }

    public static class ConductStorage implements Bundlable {

        public ArrayList<Conduct> conducts;

        public ConductStorage() {
            conducts = new ArrayList<>();
        }

        public static ConductStorage createFromConducts(Conduct... conducts){
            ConductStorage storage = new ConductStorage();
            storage.conducts = new ArrayList<>(Arrays.asList(conducts));
            return storage;
        }

        public static ConductStorage createFromConducts(ConductStorage storage){
            ConductStorage storage1 = new ConductStorage();
            storage1.conducts = new ArrayList<>(storage.conducts);
            return storage;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            ArrayList<String> conductIds = new ArrayList<>();
            for (Conduct conduct: conducts){
                conductIds.add(conduct.name());
            }
            bundle.put("conduct", conductIds.toArray(new String[0]));
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            if (bundle.getStringArray("conduct") != null) {
                String[] conductIds = bundle.getStringArray("conduct");
                for (String conduct : conductIds) {
                    conducts.add(Conduct.valueOf(conduct));
                }
            }
        }

        public String getDebugString(){
            StringBuilder str = new StringBuilder();
            for (Conduct conduct : conducts){
                str.append(conduct.name()).append(",");
            }
            str.delete(str.length() - 2, str.length() - 1);
            return str.toString();
        }

        public boolean isConductedAtAll(){
            return !conducts.isEmpty();
        }

        public boolean oneConduct(){
            return conducts.size() == 1;
        }

        public float scoreMod(){
            float total = 1;
            for (Conduct conduct : conducts){
                total *= conduct.scoreMod;
            }
            return total;
        }

        public boolean isConducted(Conduct mask){
            return isConductedAtAll() && conducts.contains(mask);
        }

        public Conduct getFirst(){
            if (isConductedAtAll()) return conducts.get(0);
            return null;
        }
    }
}
