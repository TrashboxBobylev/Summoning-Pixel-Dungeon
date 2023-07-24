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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

public class Aromatic extends CloakGlyph{
    private static ItemSprite.Glowing GRASSY = new ItemSprite.Glowing( 0x2ee62e);

    @Override
    public void proc(CloakOfShadows cloak, Char defender, int charges) {
        PathFinder.buildDistanceMap( defender.pos, BArray.not( Dungeon.level.solid, null ), (int) (3*efficiency()));
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE && i != defender.pos) {
                Char mob = Actor.findChar(i);
                if (mob instanceof Mob && mob.alignment == Char.Alignment.ENEMY){
                    Buff.affect(mob, Roots.class, 1f + efficiency());
                }
                CellEmitter.get( i ).start( LeafParticle.LEVEL_SPECIFIC, 0.25f, 10 );
            }
        }
    }

    @Override
    public float chargeModifier(CloakOfShadows cloak, Char defender) {
        return super.chargeModifier(cloak, defender)*2f;
    }

    public boolean inRange(Char target, int pos){
        return Dungeon.level.trueDistance(target.pos, pos) <= 3*efficiency();
    }

    public float seedPreservation(){
        return 0.60f*efficiency();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GRASSY;
    }
}
