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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class WraithTrap extends Trap{
    {
        color = VIOLET;
        shape = DIAMOND;
    }

    @Override
    public void activate() {
        for( int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.passable[pos+i] && Actor.findChar( pos+i ) == null) {
                Wraith wraith = Wraith.spawnAt(pos+i);
                if (wraith != null && Dungeon.hero.hasTalent(Talent.ARMORED_ARMADA)){
                    Buff.affect(wraith, Talent.ArmoredArmadaArmor.class).hits =
                            Dungeon.hero.pointsInTalent(Talent.ARMORED_ARMADA) > 2 ? 3 : 2;
                }
            }
        }
        Sample.INSTANCE.play(Assets.Sounds.CURSED);
    }
}
