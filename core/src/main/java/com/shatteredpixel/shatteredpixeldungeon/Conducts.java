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

import java.text.DecimalFormat;

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
        UNKNOWN(1.4f);

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


}
