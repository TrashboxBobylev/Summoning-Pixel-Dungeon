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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SupportPower;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ChickenSprite;

public class Chicken extends Minion {
    {
        spriteClass = ChickenSprite.class;
        attunement = 0.5f;
        baseSpeed = 2;
        flying = true;
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.5f;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (lvl == 1) Buff.affect(enemy, Cripple.class, 2f);
        if (lvl == 2) {
            Buff.affect(enemy, Terror.class, 2f).object = Dungeon.hero.id();
            enemy.buff(Terror.class).ignoreNextHit = true;
        }
        return super.attackProc(enemy, damage);
    }

    @Override
    public int defenseSkill(Char enemy) {
        int i = super.defenseSkill(enemy)*3;
        if (buff(SupportPower.class) != null) i *= 1.6f;
        return i;
    }
}
