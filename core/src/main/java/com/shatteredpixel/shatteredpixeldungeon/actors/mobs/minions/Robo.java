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

import com.shatteredpixel.shatteredpixeldungeon.sprites.DM150Sprite;

public class Robo extends Minion{
    {
        spriteClass = DM150Sprite.class;
        isTanky = true;
        baseMinDR = 10;
        baseMaxDR = 18;
    }

    @Override
    protected float attackDelay() {
        float mod = 0;
        switch (lvl){
            case 0: mod = 3; break;
            case 1: mod = 2f; break;
            case 2: mod = 1f; break;
        }
        return super.attackDelay() * mod;
    }

    @Override
    protected boolean getCloser(int target) {
        if (!enemySeen) return super.getCloser(target);
        else return false;
    }
}
