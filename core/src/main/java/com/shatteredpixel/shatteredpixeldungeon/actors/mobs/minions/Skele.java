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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Skele extends Minion {
    {
        spriteClass = SkeletonSprite.class;

        baseMinDR = 3;
        baseMaxDR = 5;

        properties.add(Property.UNDEAD);
        properties.add(Property.INORGANIC);
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.5f;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Char ch = Actor.findChar(i);
                if (ch instanceof Mob) ((Mob) ch).aggro(this);
            }
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public void die( Object cause ) {

        super.die( cause );

        if (cause == Chasm.class) return;

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
            //do not hurt allies or hero
            if (ch != null && ch.isAlive() && ch.alignment == Alignment.ENEMY && ch != Dungeon.hero) {
                float bleed;
                switch (lvl){
                    case 0: default:
                        bleed = 0;
                        break;
                    case 1:
                        bleed = 0.50f;
                        break;
                    case 2:
                        bleed = 0.95f;
                        break;
                }
                int damage = Bomb.damageRoll();
                damage = Math.max( 0,  damage - (ch.drRoll()) );
                ch.damage(Math.round(damage * (1 - bleed)), this );
                Buff.affect(ch, Bleeding.class).set(damage*bleed/2);
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            Sample.INSTANCE.play( Assets.Sounds.BONES );
        }
    }
}
