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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SupportPower;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SlimeMinionSprite;
import com.watabou.utils.Random;

public class Slime extends Minion {
    {
        spriteClass = SlimeMinionSprite.class;
        baseMinDR = 2;
        baseMaxDR = 4;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return Dungeon.level.adjacent( pos, enemy.pos ) && enemy.buff(Paralysis.class) == null;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc( enemy, damage );
        int chance = 0;
        int duration = 0;
        switch (lvl){
            case 0: chance = 0; duration = 1; break;
            case 1: chance = 45; duration = 3; break;
            case 2: chance = 60; duration = 5; break;
        }
        if (Random.Int(100) >= chance) {
            Buff.affect( enemy, Paralysis.class, duration );
            if (buff(SupportPower.class) != null) Buff.prolong(enemy, Paralysis.class, lvl*5);
        }
        return damage;
    }
}
