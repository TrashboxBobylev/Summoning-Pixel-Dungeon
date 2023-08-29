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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.Wet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Victide extends CloakGlyph{
    private static ItemSprite.Glowing WATERY = new ItemSprite.Glowing( 0x139fff, 0.8f);

    @Override
    public void proc(CloakOfShadows cloak, Char defender, int charges) {
        PathFinder.buildDistanceMap( defender.pos, BArray.not( Dungeon.level.solid, null ), 2 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE && i != defender.pos) {
                Char mob = Actor.findChar(i);
                if (mob instanceof Mob && mob.alignment == Char.Alignment.ENEMY && Random.Int(Math.round(2 / efficiency())) == 0){
                    Splash.at(i, 0x00AAFF, 5);
                    Buff.prolong(mob, Wet.class, 5f*efficiency());
                }
                if (!Dungeon.level.water[i] && Random.Int(Math.round(10 / efficiency())) == 0){
                    Splash.at(i, 0x00AAFF, 8);
                    Dungeon.level.setCellToWater(false, i);
                }
            }
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WATERY;
    }
}
