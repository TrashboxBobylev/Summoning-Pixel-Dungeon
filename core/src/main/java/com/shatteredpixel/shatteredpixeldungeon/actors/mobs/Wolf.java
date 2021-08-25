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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAttunement;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WolfSprite;
import com.watabou.utils.Random;

public class Wolf extends Mob {

    {
        spriteClass = WolfSprite.class;

        HP = HT = 70;
        defenseSkill = 22;

        EXP = 16;
        maxLvl = 25;
        state = PASSIVE;

        loot = new ElixirOfAttunement();
        lootChance = 1f;

        properties.add(Property.BLOB_IMMUNE);
        properties.add(Property.INORGANIC);
    }

    @Override
    public void damage( int dmg, Object src ) {

        if (state == PASSIVE) {
            state = HUNTING;
        }

        if (!(src instanceof Hero) && !(src instanceof Wand && !(src instanceof WandOfWarding)) && !(src instanceof Trap))
        super.damage( dmg, src );
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        Buff.prolong(Dungeon.hero, Vertigo.class, 3f);
        return super.attackProc(enemy, damage);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 17, 21 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 27;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 6);
    }
}
