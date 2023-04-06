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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollHunterSprite;

public class GnollHunter extends Minion {
    {
        spriteClass = GnollHunterSprite.class;
        baseDefense = 3;
        independenceRange = 12;

        properties.add(Property.RANGED);
        properties.add(Property.IGNORE_ARMOR);
    }

    //he is ranged minion
    @Override
    protected boolean canAttack( Char enemy ) {
        Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.FRIENDLY_PROJECTILE);
        return !Dungeon.level.adjacent( pos, enemy.pos ) && attack.collisionPos == enemy.pos;
    }

    //run away when getting closer
    @Override
    protected boolean getCloser( int target ) {
        if (state == HUNTING) {
            baseSpeed = enemy.baseSpeed*2;
            return enemySeen && getFurther( target );
        } else {
            baseSpeed = 1;
            return super.getCloser( target );
        }
    }

    @Override
    protected float attackDelay() {
        float mod = 0;
        switch (lvl){
            case 0: mod = 1; break;
            case 1: mod = 0.66f; break;
            case 2: mod = 0.30f; break;
        }
        return super.attackDelay() * mod;
    }
}
